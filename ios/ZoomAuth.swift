//
//  ZoomAuth.swift
//  ZoomSdkExample
//
//  Created by Willian Angelo on 25/01/2018.
//  Copyright © 2018 Facebook. All rights reserved.
//

import UIKit
import ZoomAuthenticationHybrid

@objc(ZoomAuth)
class ZoomAuth:  RCTViewManager, ZoomVerificationDelegate {

  var verifyResolver: RCTPromiseResolveBlock? = nil
  var verifyRejecter: RCTPromiseRejectBlock? = nil
  var returnBase64: Bool = false
  var initialized = false

  func getRCTBridge() -> RCTBridge
  {
    let root = UIApplication.shared.keyWindow!.rootViewController!.view as! RCTRootView;
    return root.bridge;
  }

  // React Method
  @objc func verify(_ options: Dictionary<String, Any>, // options not used at the moment
                      resolver resolve: @escaping RCTPromiseResolveBlock,
                      rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    self.verifyResolver = resolve
    self.verifyRejecter = reject
    self.returnBase64 = (options["returnBase64"] as? Bool)!
    DispatchQueue.main.async {
      let verificationVC = Zoom.sdk.createVerificationVC(
        delegate: self
      )

      if (options["useOverlay"] as? Bool)! {
        verificationVC.modalPresentationStyle = .overCurrentContext
      }

      let root = UIApplication.shared.keyWindow!.rootViewController!;
      root.present(verificationVC, animated: true, completion: nil)
    }
  }

  func getLiveness(result: ZoomDevicePartialLivenessResult) -> String {
    switch (result) {
      case .livenessUndetermined:
        return "CouldNotDetermineMatch"
      case .partialLivenessSuccess:
        return "LowConfidenceMatch"
    }
  }

  // surely there's an easier way...
  func getVerificationResultStatus(_ result: ZoomVerificationResult) -> String {
    switch(result.status) {
      case .userProcessedSuccessfully:
        return "UserProcessedSuccessfully"
      case .failedBecauseAppTokenNotValid:
        return "FailedBecauseAppTokenNotValid"
      case .userNotProcessed:
        return "UserNotProcessed"
      case .failedBecauseUserCancelled:
        return "FailedBecauseUserCancelled"
      case .failedBecauseCameraPermissionDenied:
        return "FailedBecauseCameraPermissionDeniedByUser"
      case .failedBecauseOfOSContextSwitch:
        return "FailedBecauseOfOSContextSwitch"
      case .failedBecauseOfTimeout:
        return "FailedBecauseOfTimeout"
      case .failedBecauseOfLowMemory:
        return "FailedBecauseOfLowMemory"
      case .failedBecauseNoConnectionInDevMode:
        return "FailedBecauseNoConnectionInDevMode"
      case .failedBecauseOfflineSessionsExceeded:
        return "FailedBecauseOfflineSessionsExceeded"
      case .failedBecauseEncryptionKeyInvalid:
        return "FailedBecauseEncryptionKeyInvalid"
      case .failedBecauseOfLandscapeMode:
        return "FailedBecauseOfLandscapeMode"
      case .failedBecauseOfReversePortraitMode:
        return "FailedBecauseOfReversePortraitMode"
      case .unknownError:
        return "UnknownError"
    }
  }

  func onZoomVerificationResult(result: ZoomVerificationResult) {
    print("\(result.status)")

    let status = getVerificationResultStatus(result)
    // CASE: user performed a ZoOm and passed the liveness check
    if result.status != .userProcessedSuccessfully {
      // handle other error
      self.sendResult([
        "status": status
      ])

      return
    }

    var resultJson:[String:Any] = [
      "status": status,
      "countOfZoomSessionsPerformed": result.countOfZoomSessionsPerformed,
      "sessionId": result.sessionId,
      //        "description": result.description
    ]

    if result.faceMetrics == nil {
      self.sendResult(resultJson)
      return
    }

    let face = result.faceMetrics!
    let liveness = getLiveness(result: face.devicePartialLivenessResult)
    var faceMetrics: [String:Any] = [
      "livenessResult": liveness,
      "livenessScore": face.devicePartialLivenessScore
    ]

    resultJson["faceMetrics"] = faceMetrics
    if face.auditTrail == nil {
      self.sendResult(resultJson)
      return
    }

    var auditTrail:[String] = []
    var count = face.auditTrail!.count
    if self.returnBase64 {
      for image in face.auditTrail! {
        auditTrail.append(uiImageToBase64(image))
      }

      faceMetrics["auditTrail"] = auditTrail
      resultJson["faceMetrics"] = faceMetrics
      self.sendResult(resultJson)
      return
    }

    for image in face.auditTrail! {
      uiImageToImageStoreKey(image) { (tag) in
        if (tag != nil) {
          auditTrail.append(tag!)
        }

        count -= 1
        if count == 0 {
          faceMetrics["auditTrail"] = auditTrail
          resultJson["faceMetrics"] = faceMetrics
          self.sendResult(resultJson)
        }
      }
    }

//    EXAMPLE: retrieve facemap
//    if let zoomFacemap = result.faceMetrics?.zoomFacemap {
//      // handle ZoOm Facemap
//    }
  }

  func sendResult(_ result: [String:Any]) -> Void {
    if (self.verifyResolver == nil) {
      return
    }

    self.verifyResolver!(result)
    self.cleanUp()
  }

  // not used at the moment
  func sendError(_ code: String, message: String, error: Error) -> Void {
    if (self.verifyRejecter == nil) {
      return
    }

    self.verifyRejecter!(code, message, error)
    self.cleanUp()
  }

  func cleanUp () -> Void {
    self.verifyResolver = nil
    self.verifyRejecter = nil
  }

  func uiImageToBase64 (_ image: UIImage) -> String {
    let imageData = UIImageJPEGRepresentation(image, 0.9)! as NSData;
    return imageData.base64EncodedString(options: [])
  }

  func uiImageToImageStoreKey (_ image: UIImage, completionHandler: @escaping (String?) -> Void) -> Void {
    let bridge = getRCTBridge()
    let imageStore: RCTImageStoreManager = bridge.imageStoreManager;
    imageStore.store(image, with: completionHandler)
  }

  // React Method
  @objc func preload() -> Void {
    Zoom.sdk.preload()
  }

  // React Method
  @objc func getVersion(_ resolve: RCTPromiseResolveBlock,
                        rejecter reject: RCTPromiseRejectBlock) -> Void {

      let result: String = Zoom.sdk.version

      if ( !result.isEmpty ) {
          resolve([
              result: result
          ])
      } else {
          let errorMsg = "SDK Errror"
          let err: NSError = NSError(domain: errorMsg, code: 0, userInfo: nil)
          reject("getVersion", errorMsg, err)
      }
  }

  // React Method
  @objc func initialize(_ options: Dictionary<String, Any>,
                        resolver resolve: @escaping RCTPromiseResolveBlock,
                        rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {

    Zoom.sdk.auditTrailType = .height640 // otherwise no auditTrail images

    // Create the customization object
    let currentCustomization: ZoomCustomization = ZoomCustomization()
    currentCustomization.showPreEnrollmentScreen = (options["showPreEnrollmentScreen"] as? Bool)!
    currentCustomization.showUserLockedScreen = (options["showUserLockedScreen"] as? Bool)!
    currentCustomization.showRetryScreen = (options["showRetryScreen"] as? Bool)!
    currentCustomization.enableLowLightMode = (options["enableLowLightMode"] as? Bool)!

    // Sample UI Customization: vertically center the ZoOm frame within the device's display
    if (options["centerFrame"] as? Bool)! {
      centerZoomFrameCustomization(currentZoomCustomization: currentCustomization)
    }

    // Apply the customization changes
    Zoom.sdk.setCustomization(currentCustomization)
    Zoom.sdk.initialize(
      appToken: options["appToken"] as! String,
      completion: { (appTokenValidated: Bool) -> Void in
        //
        // We want to ensure that App Token is valid before enabling verification
        //
        if appTokenValidated {
          self.initialized = true
          let message = "AppToken validated successfully"
          print(message)
          resolve([
            "success": true
          ])
        }
        else {
          let status = Zoom.sdk.getStatus().rawValue
          resolve([
            "success": false,
            "status": status
          ])

//          let errorMsg = "AppToken did not validate.  If Zoom ViewController's are launched, user will see an app token error state"
//          print(errorMsg)
//          let err: NSError = NSError(domain: errorMsg, code: 0, userInfo: nil)
//          reject("initialize", errorMsg, err)
        }
      }
    )
  }

  func centerZoomFrameCustomization(currentZoomCustomization: ZoomCustomization) {
    let screenHeight: CGFloat = UIScreen.main.fixedCoordinateSpace.bounds.size.height
    var frameHeight: CGFloat = screenHeight * CGFloat(currentZoomCustomization.frameCustomization.sizeRatio)
    // Detect iPhone X and iPad displays
    if UIScreen.main.fixedCoordinateSpace.bounds.size.height >= 812 {
      let screenWidth = UIScreen.main.fixedCoordinateSpace.bounds.size.width
      frameHeight = screenWidth * (16.0/9.0) * CGFloat(currentZoomCustomization.frameCustomization.sizeRatio)
    }
    let topMarginToCenterFrame = (screenHeight - frameHeight)/2.0

    currentZoomCustomization.frameCustomization.topMargin = Double(topMarginToCenterFrame)
  }
}
