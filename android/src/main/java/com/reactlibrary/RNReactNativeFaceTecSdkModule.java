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
import com.facetec.sdk.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import processors.LivenessCheckProcessor;
import processors.NetworkingHelpers;
import processors.Processor;
import processors.FaceTecGlobalState;
import io.tradle.reactimagestore.ImageStoreModule;

public class RNReactNativeFaceTecSdkModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RNReactNativeFaceTecSdk";
    private final ReactApplicationContext reactContext;
    private Promise verificationPromise;
    private boolean initialized;
    private boolean returnBase64 = false;
    private String licenseKey;

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            super.onActivityResult(activity, requestCode, resultCode, data);
            if (requestCode != FaceTecSDK.REQUEST_CODE_SESSION) return;
            if (verificationPromise == null) return;

            // Save results
            FaceTecSessionResult result = FaceTecSessionActivity.getSessionResultFromActivityResult(data);

            WritableMap resultObj;
            try {
                resultObj = convertFaceTecVerificationResult(result);
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


    public RNReactNativeFaceTecSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.verificationPromise = null;
        this.initialized = false;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "RNReactNativeFaceTecSdk";
    }

    @ReactMethod
    public void getVersion(final Promise promise) {
        promise.resolve(FaceTecSDK.version());
    }

    @ReactMethod
    public void initialize(final ReadableMap opts, final Promise promise) {
        Log.d(TAG, "initializing");

        licenseKey = opts.getString("licenseKey");

        if (opts.hasKey("faceTecServerBaseURL")) {
            FaceTecGlobalState.FaceTecServerBaseURL = opts.getString("faceTecServerBaseURL");
        }

        if (!opts.isNull("headers")) {
            ReadableMap headers = opts.getMap("headers");

            for (Map.Entry<String, Object> entry : headers.toHashMap().entrySet()) {
               FaceTecGlobalState.headers.put(entry.getKey(), entry.getValue().toString());
            }
        }

        final String facemapEncryptionKey = opts.hasKey("facemapEncryptionKey") ? opts.getString("facemapEncryptionKey") : FaceTecGlobalState.PublicFaceMapEncryptionKey;


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                FaceTecCustomization currentCustomization = new FaceTecCustomization();
                FaceTecSDK.setAuditTrailType(FaceTecAuditTrailType.HEIGHT_640);

                addFrameCustomizations(currentCustomization, opts);
                addFeedbackCustomizations(currentCustomization, opts);
                addOvalCustomizations(currentCustomization, opts);
                addGuidanceCustomizations(currentCustomization, opts);
                addResultScreenCustomization(currentCustomization, opts);
                addOverlayCustomization(currentCustomization, opts);

                FaceTecSDK.setCustomization(currentCustomization);
                FaceTecSDK.initializeInDevelopmentMode(getCurrentActivity(), licenseKey, facemapEncryptionKey, new FaceTecSDK.InitializeCallback() {
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
            }
        });
    }

    private void addFrameCustomizations(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("frameCustomization")) {
            FaceTecFrameCustomization frameCustomization = new FaceTecFrameCustomization();
            ReadableMap frameCustomizationOptions = opts.getMap("frameCustomization");

            if (frameCustomizationOptions.hasKey("backgroundColor")) {
                frameCustomization.backgroundColor = Color.parseColor(frameCustomizationOptions.getString("backgroundColor"));
            }
            if (frameCustomizationOptions.hasKey("borderColor")) {
                frameCustomization.borderColor = Color.parseColor(frameCustomizationOptions.getString("borderColor"));
            }
            currentCustomization.setFrameCustomization(frameCustomization);
            Log.d(TAG, "Frame customizations applied.");
        }
    }

    private void addFeedbackCustomizations(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("feedbackCustomization")) {
            FaceTecFeedbackCustomization feedbackCustomization = new FaceTecFeedbackCustomization();
            ReadableMap feedbackCustomizationOptions = opts.getMap("feedbackCustomization");

            if (feedbackCustomizationOptions.hasKey("backgroundColor")) {
                ReadableArray backgroundColors = feedbackCustomizationOptions.getArray("backgroundColor");

                // This attribute contains array of 2 colors for gradient, but it seems like Java API
                // doesn't allow to set gradient, so we take just the first.
                String backgroundColor = backgroundColors.getString(0);
                feedbackCustomization.backgroundColors = Color.parseColor(backgroundColor);
            }
            if (feedbackCustomizationOptions.hasKey("textColor")) {
                feedbackCustomization.textColor = Color.parseColor(feedbackCustomizationOptions.getString("textColor"));
            }

            currentCustomization.setFeedbackCustomization(feedbackCustomization);
            Log.d(TAG, "Feedback customizations applied.");
        }
    }

    private void addOvalCustomizations(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("ovalCustomization")) {
            FaceTecOvalCustomization ovalCustomization = new FaceTecOvalCustomization();
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


    private void addGuidanceCustomizations(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("guidanceCustomization")) {
            FaceTecGuidanceCustomization guidanceCustomization = new FaceTecGuidanceCustomization();
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
            if (guidanceCustomizationOptions.hasKey("retryScreenHeaderTextColor")) {
                guidanceCustomization.retryScreenHeaderTextColor = Color.parseColor(guidanceCustomizationOptions.getString("retryScreenHeaderTextColor"));
            }
            if (guidanceCustomizationOptions.hasKey("retryScreenSubtextTextColor")) {
                guidanceCustomization.retryScreenSubtextTextColor = Color.parseColor(guidanceCustomizationOptions.getString("retryScreenSubtextTextColor"));
            }
            if (guidanceCustomizationOptions.hasKey("retryScreenImageBorderColor")) {
                guidanceCustomization.retryScreenImageBorderColor = Color.parseColor(guidanceCustomizationOptions.getString("retryScreenImageBorderColor"));
            }

            currentCustomization.setGuidanceCustomization(guidanceCustomization);
            Log.d(TAG, "Guidance customizations applied.");
        }
    }

    private void addResultScreenCustomization(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("resultScreenCustomization")) {
            FaceTecResultScreenCustomization resultScreenCustomization = new FaceTecResultScreenCustomization();
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

    private void addOverlayCustomization(FaceTecCustomization currentCustomization, ReadableMap opts) {
        if (!opts.isNull("overlayCustomization")) {
            FaceTecOverlayCustomization overlayCustomization = new FaceTecOverlayCustomization();
            ReadableMap overlayCustomizationOptions = opts.getMap("overlayCustomization");

            if (overlayCustomizationOptions.hasKey("brandingImage")) {
                overlayCustomization.brandingImage = overlayCustomizationOptions.getInt("brandingImage");
                overlayCustomization.showBrandingImage = false;
            }

            if (overlayCustomizationOptions.hasKey("backgroundColor")) {
                overlayCustomization.backgroundColor = Color.parseColor(overlayCustomizationOptions.getString("backgroundColor"));
            }

            currentCustomization.setOverlayCustomization(overlayCustomization);
            Log.d(TAG, "Oval customizations applied.");
        }
    }

    // private void enroll(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     String userId = args.getString(0);
    //     String secret = args.getString(1);

    //     Intent enrollmentIntent = new Intent(this.cordova.getActivity(), FaceTecEnrollmentActivity.class);
    //     enrollmentIntent.putExtra(FaceTecSDK.EXTRA_ENROLLMENT_USER_ID, userId);
    //     enrollmentIntent.putExtra(FaceTecSDK.EXTRA_USER_ENCRYPTION_SECRET, secret);

    //     pendingCallbackContext = callbackContext;

    //     this.cordova.startActivityForResult(this, enrollmentIntent, FaceTecSDK.REQUEST_CODE_ENROLLMENT);
    // }

    // private void authenticate(JSONArray args, final CallbackContext callbackContext) throws JSONException {
    //     String userId = args.getString(0);
    //     String secret = args.getString(1);

    //     Intent authenticationIntent = new Intent(this.cordova.getActivity(), FaceTecAuthenticationActivity.class);
    //     authenticationIntent.putExtra(FaceTecSDK.EXTRA_AUTHENTICATION_USER_ID, userId);
    //     authenticationIntent.putExtra(FaceTecSDK.EXTRA_USER_ENCRYPTION_SECRET, secret);

    //     pendingCallbackContext = callbackContext;

    //     this.cordova.startActivityForResult(this, authenticationIntent, FaceTecSDK.REQUEST_CODE_AUTHENTICATION);
    // }

    @ReactMethod
    public void verify(final ReadableMap opts, final Promise promise) {
        if (!initialized) {
            promise.reject(new RuntimeException("NotInitialized"));
            return;
        }

        verificationPromise = promise;
        returnBase64 = opts.getBoolean("returnBase64");
        final Activity activity = getCurrentActivity();

        getSessionToken(new SessionTokenCallback() {
            @Override
            public void onSessionTokenReceived(String sessionToken) {
                new LivenessCheckProcessor(licenseKey, sessionToken, activity);
            }
        });
    }

    interface SessionTokenCallback {
        void onSessionTokenReceived(String sessionToken);
    }

    public void getSessionToken(final SessionTokenCallback sessionTokenCallback) {
        // Do the network call and handle result
        okhttp3.Request request = new okhttp3.Request.Builder()
                .header("X-Device-Key", licenseKey)
                .header("X-Authorization", FaceTecGlobalState.headers.get("X-Authorization"))
                .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(""))
                .url(FaceTecGlobalState.FaceTecServerBaseURL + "/session-token")
                .get()
                .build();

        NetworkingHelpers.getApiClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("FaceTecSDKSampleApp", "Exception raised while attempting HTTPS call.");

                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
                if(!e.getMessage().equals(NetworkingHelpers.OK_HTTP_RESPONSE_CANCELED)) {
                    handleErrorGettingServerSessionToken();
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseJSON = new JSONObject(responseString);
                    if(responseJSON.getJSONObject("data").has("sessionToken")) {
                        sessionTokenCallback.onSessionTokenReceived(responseJSON.getJSONObject("data").getString("sessionToken"));
                    }
                    else {
                        handleErrorGettingServerSessionToken();
                    }
                }
                catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("FaceTecSDKSampleApp", "Exception raised while attempting to parse JSON result.");
                    handleErrorGettingServerSessionToken();
                }
            }
        });
    }

private void handleErrorGettingServerSessionToken() {
        WritableMap resultObj = Arguments.createMap();
        resultObj.putBoolean("success", false);
        verificationPromise.resolve(resultObj);
    }

//  @ReactMethod
//  public void handleVerificationSuccessResult(FaceTecVerificationResult successResult) {
//    // retrieve the ZoOm facemap as byte[]
//    if (successResult.getFaceMetrics() != null) {
//      // this is the raw biometric data which can be uploaded, or may be
//      // base64 encoded in order to handle easier at the cost of processing and network usage
//      byte[] zoomFacemap = successResult.getFaceMetrics().getFaceTecFacemap();
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
    //             FaceTecSDK.UserEnrollmentStatus status = FaceTecSDK.getUserEnrollmentStatus(context, userId);
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
        FaceTecSDKStatus status = FaceTecSDK.getStatus(reactContext.getApplicationContext());
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

    private WritableMap convertFaceTecVerificationResult(FaceTecSessionResult result) throws IOException {
        WritableMap resultObj = Arguments.createMap();
        WritableMap faceMetricsObj = Arguments.createMap();

        String status = result.getStatus().name(); //convertFaceTecVerificationStatus(result.getStatus());
        resultObj.putString("status", status);
        resultObj.putBoolean("success", result.getStatus().equals(FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY));
        resultObj.putString("sessionId", result.getSessionId());

        String[] auditTrail = result.getAuditTrailCompressedBase64();
        byte[] facemap = result.getFaceScan();
        if (facemap == null || facemap.length == 0)
          return resultObj;

        WritableArray auditTrailBase64 = Arguments.createArray();
        for (String s : auditTrail) {
          auditTrailBase64.pushString(s);
        }

        faceMetricsObj.putArray("auditTrail", auditTrailBase64);
        faceMetricsObj.putString("facemap", bytesToBase64(facemap));
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
