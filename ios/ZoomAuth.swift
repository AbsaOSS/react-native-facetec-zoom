//
//  ZoomAuth.swift
//  ZoomSdkExample
//
//  Created by Willian Angelo on 25/01/2018.
//  Copyright © 2018 Facebook. All rights reserved.
//

import UIKit
import FaceTecSDK

@objc(ZoomAuth)
class ZoomAuth:  RCTViewManager, URLSessionDelegate {

  var verifyResolver: RCTPromiseResolveBlock? = nil
  var verifyRejecter: RCTPromiseRejectBlock? = nil
  var returnBase64: Bool = false
  var initialized = false
  var licenseKey: String!
  var sessionToken: String!

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
      let root = UIApplication.shared.keyWindow!.rootViewController!
      var optionsWithKey = options
      optionsWithKey["licenseKey"] = self.licenseKey
      NSLog("FaceTec - INSIDE verify, sessionToken: \(self.sessionToken)")
      let _ = LivenessCheckProcessor(options: optionsWithKey, fromVC: root, sessionToken: self.sessionToken, zoomAuth: self)
    }
  }

  // Show the final result and transition back into the main interface.
  func onProcessingComplete(isSuccess: Bool, faceTecSessionResult: FaceTecSessionResult?) {
    let statusCode = faceTecSessionResult?.status.rawValue ?? -1
    var resultJson:[String:Any] = [
      "success": isSuccess,
      "status": statusCode
    ]

    if (!isSuccess) {
      self.sendResult(resultJson)
      return
    }

    resultJson["sessionId"] = faceTecSessionResult?.sessionId ?? ""

    if faceTecSessionResult?.faceScanBase64 == nil {
          self.sendResult(resultJson)
          return
        }

        var faceMetrics: [String:Any] = [:]
        if self.returnBase64 && faceTecSessionResult?.faceScanBase64 != nil {
          faceMetrics["facemap"] = faceTecSessionResult!.faceScanBase64
        }

    //    resultJson["faceMetrics"] = faceMetrics
        if faceTecSessionResult?.auditTrailCompressedBase64 == nil {
          self.sendResult(resultJson)
          return
        }

        var auditTrail:[String] = []
        if self.returnBase64 {
          if let auditTrailBase64 = faceTecSessionResult?.auditTrailCompressedBase64 {
            faceMetrics["auditTrail"] = auditTrailBase64
          }

          resultJson["faceMetrics"] = faceMetrics
          self.sendResult(resultJson)
          return
        }
  }

  // Show the final result and transition back into the main interface.
  func onProcessingComplete(isSuccess: Bool, faceTecSessionResult: FaceTecSessionResult?, faceTecIDScanResult: FaceTecIDScanResult?) {
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
    let imageData = image.jpegData(compressionQuality: 0.9)! as NSData;
    return imageData.base64EncodedString(options: [])
  }

  func uiImageToImageStoreKey (_ image: UIImage, completionHandler: @escaping (String?) -> Void) -> Void {
    let bridge = getRCTBridge()
    let imageStore: RCTImageStoreManager = bridge.imageStoreManager;
    imageStore.store(image, with: completionHandler)
  }

  func storeDataInImageStore (_ data: Data, completionHandler: @escaping (String?) -> Void) -> Void {
    let bridge = getRCTBridge()
    let imageStore: RCTImageStoreManager = bridge.imageStoreManager;
    imageStore.storeImageData(data, with: completionHandler)
  }

  // React Method
  @objc func getVersion(_ resolve: RCTPromiseResolveBlock,
                        rejecter reject: RCTPromiseRejectBlock) -> Void {

      let result: String = FaceTec.sdk.version

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
    self.licenseKey = options["licenseKey"] as? String

    if (options["faceTecServerBaseURL"] != nil) {
        ZoomGlobalState.ZoomServerBaseURL = options["faceTecServerBaseURL"] as! String
    }

    if (options["headers"] != nil) {
        ZoomGlobalState.headers = options["headers"] as! [String: String]
    }

    FaceTec.sdk.auditTrailType = .height640 // otherwise no auditTrail images

    // Create the customization object
    let currentCustomization: FaceTecCustomization = FaceTecCustomization()
    // disable the "Your App Logo" section

    currentCustomization.overlayCustomization.brandingImage = nil

    let overlayCustomization: Dictionary<String, Any> = options["overlayCustomization"] as! Dictionary<String, Any>
    if (!overlayCustomization.isEmpty) {
          currentCustomization.overlayCustomization.backgroundColor = convertToUIColor(hex: overlayCustomization["backgroundColor"] as! String)
        }

//    currentCustomization.overlayCustomization.blurEffectOpacity = 0
//    currentCustomization.frameCustomization.blurEffectOpacity = 0
//    currentCustomization.guidanceCustomization.showIntroScreenBrandingImage = false

    // Sample UI Customization: vertically center the ZoOm frame within the device's display
//    if (options["centerFrame"] as? Bool)! {
//      centerZoomFrameCustomization(currentZoomCustomization: currentCustomization)
//    } else if (options["topMargin"] != nil) {
//      currentCustomization.frameCustomization.topMargin = options["topMargin"] as! Int32
//    }

    addFrameCustomizations(currentCustomization: currentCustomization, options: options)
    addFeedbackCustomizations(currentCustomization: currentCustomization, options: options)
    addOvalCustomizations(currentCustomization: currentCustomization, options: options)
    addGuidanceCustomizations(currentCustomization: currentCustomization, options: options)
    addResultScreenCustomizations(currentCustomization: currentCustomization, options: options)

    // Apply the customization changes
    FaceTec.sdk.setCustomization(currentCustomization)

    self.getDeviceToken() { deviceToken in
        NSLog("FaceTec - INSIDE self.getDeviceToken(), deviceToken: \(deviceToken)")
        FaceTec.sdk.initializeInProductionMode(
            productionKeyText: deviceToken,
            deviceKeyIdentifier: options["licenseKey"] as! String,
            faceScanEncryptionKey: options["facemapEncryptionKey"] as! String,
          completion: { (licenseKeyValidated: Bool) -> Void in
            //
            // We want to ensure that licenseKey is valid before enabling verification
            //
            if licenseKeyValidated {
              self.initialized = true
              let message = "licenseKey validated successfully"
              print(message)
              resolve([
                "success": true
              ])
            }
            else {
              let statusText = FaceTec.sdk.getStatus()
              NSLog("FaceTec - INSIDE self.getDeviceToken() FAILURE, statusText: \(statusText)")
              let status = FaceTec.sdk.getStatus().rawValue
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


  }

  func addFrameCustomizations(currentCustomization: FaceTecCustomization, options: Dictionary<String, Any>) {
    // Sample UI Customization: vertically center the ZoOm frame within the device's display
//     if (options["centerFrame"] as? Bool)! {
//       centerZoomFrameCustomization(currentZoomCustomization: currentCustomization)
//     }
    let frameCustomization: Dictionary<String, Any> = options["frameCustomization"] as! Dictionary<String, Any>
    if (!frameCustomization.isEmpty) {
        currentCustomization.frameCustomization.backgroundColor = convertToUIColor(hex: frameCustomization["backgroundColor"] as! String)
        currentCustomization.frameCustomization.borderColor = convertToUIColor(hex: frameCustomization["borderColor"] as! String)
    }
  }

  func addFeedbackCustomizations(currentCustomization: FaceTecCustomization, options: Dictionary<String, Any>) {
    let feedbackCustomization: Dictionary<String, Any> = options["feedbackCustomization"] as! Dictionary<String, Any>
    // Create gradient layer for a custom feedback bar background on iOS
    if (!feedbackCustomization.isEmpty) {
      let backgroundColors = feedbackCustomization["backgroundColor"] as! Array<String>
      let zoomGradientLayer = createGradientLayer(_self: self, hexColor1: backgroundColors[0], hexColor2: backgroundColors[1])
      currentCustomization.feedbackCustomization.backgroundColor = zoomGradientLayer
      currentCustomization.feedbackCustomization.textColor = convertToUIColor(hex: feedbackCustomization["textColor"] as! String)
      print("Feedback customizations applied.")
    }
  }

  func addOvalCustomizations(currentCustomization: FaceTecCustomization, options: Dictionary<String, Any>) {
    let ovalCustomization: Dictionary<String, Any> = options["ovalCustomization"] as! Dictionary<String, Any>
    if (!ovalCustomization.isEmpty) {
      let supportedColorOvalCustomizations = ["strokeColor", "progressColor1", "progressColor2"]
      for property in supportedColorOvalCustomizations {
        if (ovalCustomization[property] != nil) {
          let value = ovalCustomization[property]
          currentCustomization.ovalCustomization.setValue(convertToUIColor(hex: value as! String), forKey: property)
        }
      }
      print("Oval customizations applied.")
    }
  }

  func addGuidanceCustomizations(currentCustomization: FaceTecCustomization, options: Dictionary<String, Any>) {
    let guidanceCustomization: Dictionary<String, Any> = options["guidanceCustomization"] as! Dictionary<String, Any>
    // Create gradient layer for a custom feedback bar background on iOS
    if (!guidanceCustomization.isEmpty) {
      currentCustomization.guidanceCustomization.foregroundColor = convertToUIColor(hex: guidanceCustomization["foregroundColor"] as! String)
      currentCustomization.guidanceCustomization.readyScreenHeaderTextColor = convertToUIColor(hex: guidanceCustomization["readyScreenHeaderTextColor"] as! String)
      currentCustomization.guidanceCustomization.readyScreenSubtextTextColor = convertToUIColor(hex: guidanceCustomization["readyScreenSubtextTextColor"] as! String)
      currentCustomization.guidanceCustomization.retryScreenHeaderTextColor = convertToUIColor(hex: guidanceCustomization["retryScreenHeaderTextColor"] as! String)
      currentCustomization.guidanceCustomization.retryScreenSubtextTextColor = convertToUIColor(hex: guidanceCustomization["retryScreenSubtextTextColor"] as! String)
      currentCustomization.guidanceCustomization.retryScreenImageBorderColor = convertToUIColor(hex: guidanceCustomization["retryScreenImageBorderColor"] as! String)

      currentCustomization.guidanceCustomization.buttonTextNormalColor = convertToUIColor(hex: guidanceCustomization["buttonTextNormalColor"] as! String)
      currentCustomization.guidanceCustomization.buttonTextHighlightColor = convertToUIColor(hex: guidanceCustomization["buttonTextHighlightColor"] as! String)
      currentCustomization.guidanceCustomization.buttonTextDisabledColor = convertToUIColor(hex: guidanceCustomization["buttonTextDisabledColor"] as! String)

      currentCustomization.guidanceCustomization.buttonBackgroundNormalColor = convertToUIColor(hex: guidanceCustomization["buttonBackgroundNormalColor"] as! String)
      currentCustomization.guidanceCustomization.buttonBackgroundHighlightColor = convertToUIColor(hex: guidanceCustomization["buttonBackgroundHighlightColor"] as! String)
      currentCustomization.guidanceCustomization.buttonBackgroundDisabledColor = convertToUIColor(hex: guidanceCustomization["buttonBackgroundNormalColor"] as! String, alpha: guidanceCustomization["buttonBackgroundDisabledColorAlpha"] as! Int)

      print("Guidance customizations applied.")
    }
  }

  func addResultScreenCustomizations(currentCustomization: FaceTecCustomization, options: Dictionary<String, Any>) {
    let resultScreenCustomization: Dictionary<String, Any> = options["resultScreenCustomization"] as! Dictionary<String, Any>
    // Create gradient layer for a custom feedback bar background on iOS
    if (!resultScreenCustomization.isEmpty) {
      currentCustomization.resultScreenCustomization.foregroundColor = convertToUIColor(hex: resultScreenCustomization["foregroundColor"] as! String)
      currentCustomization.resultScreenCustomization.activityIndicatorColor = convertToUIColor(hex: resultScreenCustomization["activityIndicatorColor"] as! String)
      currentCustomization.resultScreenCustomization.uploadProgressFillColor = convertToUIColor(hex: resultScreenCustomization["uploadProgressFillColor"] as! String)
      currentCustomization.resultScreenCustomization.uploadProgressTrackColor = convertToUIColor(hex: resultScreenCustomization["uploadProgressTrackColor"] as! String)

      currentCustomization.resultScreenCustomization.resultAnimationBackgroundColor = convertToUIColor(hex: resultScreenCustomization["resultAnimationBackgroundColor"] as! String)
      currentCustomization.resultScreenCustomization.resultAnimationForegroundColor = convertToUIColor(hex: resultScreenCustomization["resultAnimationForegroundColor"] as! String)

      print("Guidance customizations applied.")
    }
  }

// topMargin and sizeRatio not defined in FaceTecCustomization.frameCustomization
  func centerZoomFrameCustomization(currentZoomCustomization: FaceTecCustomization) {
//     let screenHeight: CGFloat = UIScreen.main.fixedCoordinateSpace.bounds.size.height
//     var frameHeight: CGFloat = screenHeight * CGFloat(currentZoomCustomization.frameCustomization.sizeRatio)
    // Detect iPhone X and iPad displays
//     if UIScreen.main.fixedCoordinateSpace.bounds.size.height >= 812 {
//       let screenWidth = UIScreen.main.fixedCoordinateSpace.bounds.size.width
//       frameHeight = screenWidth * (16.0/9.0) * CGFloat(currentZoomCustomization.frameCustomization.sizeRatio)
//     }
//     let topMarginToCenterFrame = (screenHeight - frameHeight)/2.0
//
//     currentZoomCustomization.frameCustomization.topMargin = Int32(topMarginToCenterFrame)
  }

    func getDeviceToken(deviceTokenCallback: @escaping (String) -> ()) {
        let endpoint = ZoomGlobalState.ZoomServerBaseURL + "/session-token"
        let request = NSMutableURLRequest(url: NSURL(string: endpoint)! as URL)
        request.httpMethod = "GET"
        // Required parameters to interact with the FaceTec Managed Testing API.
        request.addValue(self.licenseKey, forHTTPHeaderField: "X-Device-Key")
        request.addValue(ZoomGlobalState.headers["X-Authorization"] as! String, forHTTPHeaderField: "X-Authorization")
        request.addValue(FaceTec.sdk.createFaceTecAPIUserAgentString(""), forHTTPHeaderField: "User-Agent")

        let session = URLSession(configuration: URLSessionConfiguration.default, delegate: self, delegateQueue: OperationQueue.main)
        let task = session.dataTask(with: request as URLRequest, completionHandler: { data, response, error in
            // Ensure the data object is not nil otherwise callback with empty dictionary.
            guard let data = data else {
                print("Exception raised while attempting HTTPS call.")
                self.onProcessingComplete(isSuccess: false, faceTecSessionResult: nil)
                return
            }
            if let responseJSONObj = try? JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions.allowFragments) as! [String: AnyObject] {
                if((responseJSONObj["sessionToken"] as? String) != nil)
                {
                    NSLog("FaceTec - INSIDE getDeviceToken, sessionToken: \(responseJSONObj["sessionToken"] as! String)")
                    self.sessionToken = responseJSONObj["sessionToken"] as! String
                }
                else {
                    print("Exception raised while attempting HTTPS call.")
                    self.onProcessingComplete(isSuccess: false, faceTecSessionResult: nil)
                }
                if((responseJSONObj["deviceToken"] as? String) != nil)
                {
                    let rawDeviceToken = responseJSONObj["deviceToken"] as! String
                    NSLog("FaceTec - INSIDE getDeviceToken, raw deviceToken: \(rawDeviceToken)")
                    let deviceToken = rawDeviceToken.replacingOccurrences(of: ":", with: "=").replacingOccurrences(of: "new_line", with: "\n")
                    NSLog("FaceTec - INSIDE getDeviceToken, deviceToken: \(deviceToken)")
                    deviceTokenCallback(deviceToken)
                    return
                }
                else {
                    print("Exception raised while attempting HTTPS call.")
                    self.onProcessingComplete(isSuccess: false, faceTecSessionResult: nil)
                }
            }
        })
        task.resume()
    }
}

func createGradientLayer(_self: ZoomAuth, hexColor1: String, hexColor2: String) -> CAGradientLayer {
  let gradientLayer = CAGradientLayer()
  gradientLayer.frame = _self.view().bounds
  gradientLayer.colors = [convertToUIColor(hex: hexColor1).cgColor, convertToUIColor(hex: hexColor2).cgColor]
  _self.view().layer.addSublayer(gradientLayer)
  return gradientLayer
}

func convertToUIColor(hex: String, alpha: Int = 1) -> UIColor {
  if hex.hasPrefix("#") {
    let start = hex.index(hex.startIndex, offsetBy: 1)
    let hexColor = String(hex[start...])

    if hexColor.count == 6 {
      let scanner = Scanner(string: hexColor)
      var hexNumber: UInt64 = 0

      if scanner.scanHexInt64(&hexNumber) {
        let red = CGFloat((hexNumber & 0xff0000) >> 16) / 255
        let green = CGFloat((hexNumber & 0xff00) >> 8) / 255
        let blue = CGFloat(hexNumber & 0xff) / 255
        var alphaIndex:CGFloat
        if (alpha > 1) {
            alphaIndex = CGFloat(alpha) / 255
        } else {
            alphaIndex = CGFloat(alpha)
        }

        return UIColor(red: red, green: green, blue: blue, alpha: alphaIndex)
      }
    }
  }

  return UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 1.0)
}

