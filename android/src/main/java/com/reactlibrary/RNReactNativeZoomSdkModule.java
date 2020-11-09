package com.reactlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
//import androidx.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facetec.zoom.sdk.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import ZoomProcessors.LivenessCheckProcessor;
import ZoomProcessors.Processor;
import ZoomProcessors.ZoomGlobalState;
import io.tradle.reactimagestore.ImageStoreModule;

public class RNReactNativeZoomSdkModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RNReactNativeZoomSdk";
    private final ReactApplicationContext reactContext;
    private Promise verificationPromise;
    private boolean initialized;
    private boolean returnBase64 = false;
    private String licenseKey;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);
            if (requestCode != ZoomSDK.REQUEST_CODE_SESSION) return;
            if (verificationPromise == null) return;

            // Save results
            ZoomSessionResult result = ZoomSessionActivity.getZoomSessionResultFromActivityResult(data);

            WritableMap resultObj;
            try {
                resultObj = convertZoomVerificationResult(result);
            } catch (IOException i) {
                resultObj = Arguments.createMap();
                resultObj.putBoolean("success", false);
                resultObj.putString("status", "ImageStoreError");
                resultObj.putString("error", i.getMessage());
            }

            verificationPromise.resolve(resultObj);
            verificationPromise = null;
        }
    };


    public RNReactNativeZoomSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.verificationPromise = null;
        this.initialized = false;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "RNReactNativeZoomSdk";
    }

    @ReactMethod
    public void getVersion(final Promise promise) {
        promise.resolve(ZoomSDK.version());
    }

    @ReactMethod
    public void preload() {
        // preload sdk resources so the UI is snappy (optional)
        ZoomSDK.preload(getCurrentActivity());
    }

    @ReactMethod
    public void initialize(final ReadableMap opts, final Promise promise) {
        Log.d(TAG, "initializing");

        licenseKey = opts.getString("licenseKey");

        if (opts.hasKey("zoomServerBaseUrl")) {
            ZoomGlobalState.ZoomServerBaseURL = opts.getString("zoomServerBaseUrl");
        }

        if (!opts.isNull("headers")) {
            ReadableMap headers = opts.getMap("headers");

            for (Map.Entry<String, Object> entry : headers.toHashMap().entrySet()) {
               ZoomGlobalState.headers.put(entry.getKey(), entry.getValue().toString());
            }
        }

        final String facemapEncryptionKey = opts.hasKey("facemapEncryptionKey") ? opts.getString("facemapEncryptionKey") : ZoomGlobalState.PublicFaceMapEncryptionKey;


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ZoomCustomization currentCustomization = new ZoomCustomization();
                ZoomSDK.setAuditTrailType(ZoomAuditTrailType.HEIGHT_640);
//        currentCustomization.showPreEnrollmentScreen = opts.getBoolean("showPreEnrollmentScreen");
//        currentCustomization.showUserLockedScreen = opts.getBoolean("showUserLockedScreen");
//        currentCustomization.showRetryScreen= opts.getBoolean("showRetryScreen");
//        currentCustomization.enableLowLightMode = opts.getBoolean("enableLowLightMode");

                addFrameCustomizations(currentCustomization, opts);
                addFeedbackCustomizations(currentCustomization, opts);
                addOvalCustomizations(currentCustomization, opts);
                addGuidanceCustomizations(currentCustomization, opts);
                addResultScreenCustomization(currentCustomization, opts);
                addOverlayCustomization(currentCustomization, opts);

                ZoomSDK.setCustomization(currentCustomization);
                ZoomSDK.initialize(getCurrentActivity(), licenseKey, new ZoomSDK.InitializeCallback() {
                    @Override
                    public void onCompletion(boolean successful) {
                        WritableMap map = Arguments.createMap();
                        map.putBoolean("success", successful);
                        if (successful) {
                            initialized = true;
                        } else {
                            map.putString("status", getSdkStatusString());
                        }

                        Log.d(TAG, String.format("initialized: %b", successful));
                        promise.resolve(map);
                    }
                });

                ZoomSDK.setFaceMapEncryptionKey(facemapEncryptionKey);
            }
        });
    }

    private void addFrameCustomizations(ZoomCustomization currentCustomization, ReadableMap opts) {
        ZoomFrameCustomization frameCustomization = new ZoomFrameCustomization();
        frameCustomization.topMargin = opts.getInt("topMargin");
        frameCustomization.sizeRatio = (float)opts.getDouble("sizeRatio");

        if (opts.hasKey("backgroundColor")) {
            frameCustomization.backgroundColor = Color.parseColor(opts.getString("backgroundColor"));
        }

        if (opts.hasKey("borderColor")) {
            frameCustomization.borderColor= Color.parseColor(opts.getString("borderColor"));
        }

        currentCustomization.setFrameCustomization(frameCustomization);
    }

    private void addFeedbackCustomizations(ZoomCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("feedbackCustomization")) {
            ZoomFeedbackCustomization feedbackCustomization = new ZoomFeedbackCustomization();
            ReadableMap feedbackCustomizationOptions = opts.getMap("feedbackCustomization");

            if (feedbackCustomizationOptions.hasKey("backgroundColor")) {
                ReadableArray backgroundColors = feedbackCustomizationOptions.getArray("backgroundColor");

                // This attribute contains array of 2 colors for gradient, but it seems like Java API
                // doesn't allow to set gradient, so we take just the first.
                String backgroundColor = backgroundColors.getString(0);
                feedbackCustomization.backgroundColors = Color.parseColor(backgroundColor);
            }

            currentCustomization.setFeedbackCustomization(feedbackCustomization);
            Log.d(TAG, "Feedback customizations applied.");
        }
    }

    private void addOvalCustomizations(ZoomCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("ovalCustomization")) {
            ZoomOvalCustomization ovalCustomization = new ZoomOvalCustomization();
            ReadableMap ovalCustomizationOptions = opts.getMap("ovalCustomization");
            if (ovalCustomizationOptions.hasKey("strokeColor")) {
                ovalCustomization.strokeColor = Color.parseColor(ovalCustomizationOptions.getString("strokeColor"));
            }
            if (ovalCustomizationOptions.hasKey("progressColor1")) {
                ovalCustomization.progressColor1 = Color.parseColor(ovalCustomizationOptions.getString("progressColor1"));
            }
            if (ovalCustomizationOptions.hasKey("progressColor2")) {
                ovalCustomization.progressColor2 = Color.parseColor(ovalCustomizationOptions.getString("progressColor2"));
            }
            currentCustomization.setOvalCustomization(ovalCustomization);
            Log.d(TAG, "Oval customizations applied.");
        }
    }


    private void addGuidanceCustomizations(ZoomCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("guidanceCustomization")) {
            ZoomGuidanceCustomization guidanceCustomization = new ZoomGuidanceCustomization();
            ReadableMap guidanceCustomizationOptions = opts.getMap("guidanceCustomization");

            if (guidanceCustomizationOptions.hasKey("foregroundColor")) {
                guidanceCustomization.foregroundColor = Color.parseColor(guidanceCustomizationOptions.getString("foregroundColor"));
            }
            if (guidanceCustomizationOptions.hasKey("readyScreenHeaderTextColor")) {
                guidanceCustomization.readyScreenHeaderTextColor = Color.parseColor(guidanceCustomizationOptions.getString("readyScreenHeaderTextColor"));
            }
            if (guidanceCustomizationOptions.hasKey("readyScreenSubtextTextColor")) {
                guidanceCustomization.readyScreenSubtextTextColor = Color.parseColor(guidanceCustomizationOptions.getString("readyScreenSubtextTextColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonTextNormalColor")) {
                guidanceCustomization.buttonTextNormalColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonTextNormalColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonTextHighlightColor")) {
                guidanceCustomization.buttonTextHighlightColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonTextHighlightColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonTextDisabledColor")) {
                guidanceCustomization.buttonTextDisabledColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonTextDisabledColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonBackgroundNormalColor")) {
                guidanceCustomization.buttonBackgroundNormalColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonBackgroundNormalColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonBackgroundHighlightColor")) {
                guidanceCustomization.buttonBackgroundHighlightColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonBackgroundHighlightColor"));
            }
            if (guidanceCustomizationOptions.hasKey("buttonBackgroundDisabledColor")) {
                guidanceCustomization.buttonBackgroundDisabledColor = Color.parseColor(guidanceCustomizationOptions.getString("buttonBackgroundDisabledColor"));
            }

            currentCustomization.setGuidanceCustomization(guidanceCustomization);
            Log.d(TAG, "Guidance customizations applied.");
        }
    }

    private void addResultScreenCustomization(ZoomCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("resultScreenCustomization")) {
            ZoomResultScreenCustomization resultScreenCustomization = new ZoomResultScreenCustomization();
            ReadableMap resultScreenCustomizationOptions = opts.getMap("resultScreenCustomization");

            if (resultScreenCustomizationOptions.hasKey("foregroundColor")) {
                resultScreenCustomization.foregroundColor = Color.parseColor(resultScreenCustomizationOptions.getString("foregroundColor"));
            }
            if (resultScreenCustomizationOptions.hasKey("activityIndicatorColor")) {
                resultScreenCustomization.activityIndicatorColor = Color.parseColor(resultScreenCustomizationOptions.getString("activityIndicatorColor"));
            }
            if (resultScreenCustomizationOptions.hasKey("uploadProgressFillColor")) {
                resultScreenCustomization.uploadProgressFillColor = Color.parseColor(resultScreenCustomizationOptions.getString("uploadProgressFillColor"));
            }
            if (resultScreenCustomizationOptions.hasKey("uploadProgressTrackColor")) {
                resultScreenCustomization.uploadProgressTrackColor = Color.parseColor(resultScreenCustomizationOptions.getString("uploadProgressTrackColor"));
            }
            if (resultScreenCustomizationOptions.hasKey("resultAnimationBackgroundColor")) {
                resultScreenCustomization.resultAnimationBackgroundColor = Color.parseColor(resultScreenCustomizationOptions.getString("resultAnimationBackgroundColor"));
            }
            if (resultScreenCustomizationOptions.hasKey("resultAnimationForegroundColor")) {
                resultScreenCustomization.resultAnimationForegroundColor = Color.parseColor(resultScreenCustomizationOptions.getString("resultAnimationForegroundColor"));
            }

            currentCustomization.setResultScreenCustomization(resultScreenCustomization);
            Log.d(TAG, "Result Screen customizations applied.");
        }
    }

    private void addOverlayCustomization(ZoomCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("overlayCustomization")) {
            ZoomOverlayCustomization overlayCustomization = new ZoomOverlayCustomization();
            ReadableMap overlayCustomizationOptions = opts.getMap("overlayCustomization");

            if (overlayCustomizationOptions.hasKey("brandingImage")) {
                overlayCustomization.brandingImage = overlayCustomizationOptions.getInt("brandingImage");
                overlayCustomization.showBrandingImage = false;
            }

            currentCustomization.setOverlayCustomization(overlayCustomization);
            Log.d(TAG, "Oval customizations applied.");
        }
    }

    // private void enroll(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     String userId = args.getString(0);
    //     String secret = args.getString(1);

    //     Intent enrollmentIntent = new Intent(this.cordova.getActivity(), ZoomEnrollmentActivity.class);
    //     enrollmentIntent.putExtra(ZoomSDK.EXTRA_ENROLLMENT_USER_ID, userId);
    //     enrollmentIntent.putExtra(ZoomSDK.EXTRA_USER_ENCRYPTION_SECRET, secret);

    //     pendingCallbackContext = callbackContext;

    //     this.cordova.startActivityForResult(this, enrollmentIntent, ZoomSDK.REQUEST_CODE_ENROLLMENT);
    // }

    // private void authenticate(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     String userId = args.getString(0);
    //     String secret = args.getString(1);

    //     Intent authenticationIntent = new Intent(this.cordova.getActivity(), ZoomAuthenticationActivity.class);
    //     authenticationIntent.putExtra(ZoomSDK.EXTRA_AUTHENTICATION_USER_ID, userId);
    //     authenticationIntent.putExtra(ZoomSDK.EXTRA_USER_ENCRYPTION_SECRET, secret);

    //     pendingCallbackContext = callbackContext;

    //     this.cordova.startActivityForResult(this, authenticationIntent, ZoomSDK.REQUEST_CODE_AUTHENTICATION);
    // }

    @ReactMethod
    public void verify(ReadableMap opts, final Promise promise) {
        if (!initialized) {
            promise.reject(new RuntimeException("NotInitialized"));
            return;
        }

        verificationPromise = promise;
        returnBase64 = opts.getBoolean("returnBase64");
        Activity activity = getCurrentActivity();
        new LivenessCheckProcessor(activity, this.licenseKey);
    }

//  @ReactMethod
//  public void handleVerificationSuccessResult(ZoomVerificationResult successResult) {
//    // retrieve the ZoOm facemap as byte[]
//    if (successResult.getFaceMetrics() != null) {
//      // this is the raw biometric data which can be uploaded, or may be
//      // base64 encoded in order to handle easier at the cost of processing and network usage
//      byte[] zoomFacemap = successResult.getFaceMetrics().getZoomFacemap();
//      // handle facemap
//    }
//  }
//
    // private void getUserEnrollmentStatus(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     final Context context = this.cordova.getActivity().getApplicationContext();
    //     final String userId = args.getString(0);

    //     cordova.getThreadPool().execute(new Runnable() {
    //         @Override
    //         public void run() {
    //             ZoomSDK.UserEnrollmentStatus status = ZoomSDK.getUserEnrollmentStatus(context, userId);
    //             switch (status) {
    //                 case USER_ENROLLED:
    //                     callbackContext.success("Enrolled");
    //                     break;
    //                 case USER_INVALIDATED:
    //                     callbackContext.success("Invalidated");
    //                     break;
    //                 case USER_NOT_ENROLLED:
    //                 default:
    //                     callbackContext.success("NotEnrolled");
    //             }
    //         }
    //     });
    // }

//  @Override
//  public void onNewIntent(Intent intent) { }


//    @NonNull
    private String getSdkStatusString() {
        ZoomSDKStatus status = ZoomSDK.getStatus(reactContext.getApplicationContext());
        return status.name();

//    switch (status) {
//      case NEVER_INITIALIZED:
//        return "NeverInitialized";
//      case INITIALIZED:
//        return "Initialized";
//      case INVALID_DEVICE_LICENSE_KEY_IDENTIFIER:
//        return "InvalidLicenseKey";
//      case VERSION_DEPRECATED:
//        return "VersionDeprecated";
//      case OFFLINE_SESSIONS_EXCEEDED:
//        return "OfflineSessionsExceeded";
//      case DEVICE_IN_LANDSCAPE_MODE:
//        return "FailedBecauseOfLandscapeMode";
//      case DEVICE_IN_REVERSE_PORTRAIT_MODE:
//        return "FailedBecauseOfReversePortraitMode";
//      case NETWORK_ISSUES:
//        return "NetworkIssues";
//      case DEVICE_LOCKED_OUT:
//        return "DeviceLockedOut";
//      case DEVICE_NOT_SUPPORTED:
//        return "DeviceNotSupported";
//      case LICENSE_EXPIRED_OR_INVALID:
//        return "LicenseExpiredOrInvalid";
//      default:
//        return "UnknownError";
//    }
    }

    private WritableMap convertZoomVerificationResult(ZoomSessionResult result) throws IOException {
        WritableMap resultObj = Arguments.createMap();
        WritableMap faceMetricsObj = Arguments.createMap();

        ZoomFaceBiometricMetrics faceMetrics = result.getFaceMetrics();

        String status = result.getStatus().name(); //convertZoomVerificationStatus(result.getStatus());
        resultObj.putString("status", status);
        resultObj.putBoolean("success", result.getStatus().equals(ZoomSessionStatus.SESSION_COMPLETED_SUCCESSFULLY));
        resultObj.putInt("countOfZoomSessionsPerformed", result.getCountOfZoomSessionsPerformed());
        resultObj.putString("sessionId", result.getSessionId());

        ArrayList<Bitmap> auditTrail = faceMetrics.getAuditTrail();
        byte[] facemap = faceMetrics.getFaceMap();
        if (facemap == null || facemap.length == 0)
            return resultObj;

        if (returnBase64) {
            WritableArray auditTrailBase64 = Arguments.createArray();
            for (Bitmap image : auditTrail) {
                auditTrailBase64.pushString(bitmapToBase64(image, 90));
            }

            faceMetricsObj.putArray("auditTrail", auditTrailBase64);
            faceMetricsObj.putString("facemap", bytesToBase64(facemap));
            resultObj.putMap("faceMetrics", faceMetricsObj);
            return resultObj;
        }

        WritableArray auditTrailTags = Arguments.createArray();
        for (Bitmap image : auditTrail) {
            Uri imageUri = ImageStoreModule.storeImageBitmap(this.reactContext, image, "image/png");
            auditTrailTags.pushString(imageUri.toString());
        }

        faceMetricsObj.putArray("auditTrail", auditTrailTags);
        faceMetricsObj.putString("facemap", getImageTagForBytes(facemap));


        resultObj.putMap("faceMetrics", faceMetricsObj);
        return resultObj;
    }

    private String getImageTagForBytes(byte[] bytes) throws IOException {
        return ImageStoreModule.storeImageBytes(this.reactContext, bytes).toString();
    }

    private static String bitmapToBase64(Bitmap bitmap, int quality) {
        return bytesToBase64(toJpeg(bitmap, quality));
    }

    private static String bytesToBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private static byte[] toJpeg(Bitmap bitmap, int quality) throws OutOfMemoryError {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

        try {
            return outputStream.toByteArray();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "problem compressing jpeg", e);
            }
        }
    }
}
