// Demonstrates calling the FaceTec Managed Testing API and/or ZoOm Server

package processors;

import android.os.Build;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

import com.facetec.sdk.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.util.concurrent.TimeUnit;

// Possible directives after parsing the result from ZoOm Server.
enum UXNextStep {
    Succeed,
    Retry,
    Cancel
}

public class NetworkingHelpers {
    private static OkHttpClient _apiClient = null;
    static OkHttpClient createApiClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.readTimeout(30, TimeUnit.SECONDS);
        OkHttpClient client = clientBuilder.build();

        // Enabling support for TLSv1.1 and TLSv1.2 on Android 4.4 and below.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .sslSocketFactory(new TLSSocketFactory())
                        .build();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            Log.d("FaceTecSDK", "Http client read timeout in millis: " + client.readTimeoutMillis());
            return client;
        }

        Log.d("FaceTecSDK", "Http client read timeout in millis: " + client.readTimeoutMillis());
        return client;
    }

    static String OK_HTTP_BUILDER_TAG = "faceTecAPIRequest";
    static String OK_HTTP_RESPONSE_CANCELED = "Canceled";

    // Set up common parameters needed to communicate to the API.
    public static JSONObject getCommonParameters(FaceTecSessionResult faceTecSessionResult) {
        String faceTecFaceMapBase64 = faceTecSessionResult.getFaceScanBase64();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("faceMap", faceTecFaceMapBase64);
            parameters.put("sessionId", faceTecSessionResult.getSessionId());
            if(!faceTecSessionResult.getAuditTrailCompressedBase64()[0].isEmpty()) {
                String compressedBase64AuditTrailImage = faceTecSessionResult.getAuditTrailCompressedBase64()[0];
                parameters.put("auditTrailImage", compressedBase64AuditTrailImage);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
            Log.d("FaceTecSDK", "Exception raised while attempting to create JSON payload for upload.");
        }
        return parameters;
    }

//    // Set up parameters needed to communicate to the API for Liveness + Matching (Authenticate).
//    public static JSONObject getAuthenticateParameters(FaceTecSessionResult faceTecSessionResult) {
//        String faceTecFaceMapBase64 = faceTecSessionResult.getFaceMetrics().getFaceMapBase64();
//
//        JSONObject parameters = new JSONObject();
//        JSONObject sourceObject = new JSONObject();
//        JSONObject targetObject = new JSONObject();
//
//        try {
//            targetObject.put("faceMap", faceTecFaceMapBase64);
//            sourceObject.put("enrollmentIdentifier", FaceTecGlobalState.randomUsername);
//            parameters.put("performContinuousLearning", true);
//            parameters.put("target", targetObject);
//            parameters.put("source", sourceObject);
//            parameters.put("sessionId", faceTecSessionResult.getSessionId());
//        }
//        catch(JSONException e) {
//            e.printStackTrace();
//            Log.d("FaceTecSDK", "Exception raised while attempting to create JSON payload for upload.");
//        }
//        return parameters;
//    }
//
//    // Set up parameters needed to communicate to the API for Photo ID Match.
//    public static JSONObject getPhotoIDParameters(FaceTecIDScanResult faceTecIDScanResult) {
//        String faceTecIDScanBase64 = faceTecIDScanResult.getIDScanMetrics().getIDScanBase64();
//        String sessionId = faceTecIDScanResult.getIDScanMetrics().getSessionId();
//        JSONObject parameters = new JSONObject();
//        try {
//            parameters.put("enrollmentIdentifier", FaceTecGlobalState.randomUsername);
//            parameters.put("idScan", faceTecIDScanBase64);
//            parameters.put("sessionId", sessionId);
//        }
//        catch(JSONException e) {
//            e.printStackTrace();
//            Log.d("FaceTecSDK", "Exception raised while attempting to create JSON payload for upload.");
//        }
//        return parameters;
//    }

    // Create and send the request.  Parse the results and send the caller what the next step should be (Succeed, Retry, or Cancel).
    public static void getLivenessCheckResponseFromFaceTecServer(String licenseKey, FaceTecSessionResult faceTecSessionResult, FaceTecFaceScanResultCallback faceTecFaceScanResultCallback, FaceTecManagedAPICallback resultCallback ) {
        JSONObject parameters = getCommonParameters(faceTecSessionResult);
        callToFaceTecServerForResult(
                FaceTecGlobalState.FaceTecServerBaseURL + "/liveness",
                parameters,
                licenseKey,
                faceTecSessionResult,
                faceTecFaceScanResultCallback,
                resultCallback
        );
    }

    // Create and send the request.  Parse the results and send the caller what the next step should be (Succeed, Retry, or Cancel).
//    public static void getEnrollmentResponseFromFaceTecServer( FaceTecSessionResult faceTecSessionResult, FaceTecFaceMapResultCallback faceTecFaceScanResultCallback, FaceTecManagedAPICallback resultCallback ) {
//        JSONObject parameters = getCommonParameters(faceTecSessionResult);
//        try {
//            parameters.put("enrollmentIdentifier", FaceTecGlobalState.randomUsername);
//        }
//        catch(JSONException e) {
//            e.printStackTrace();
//        }
//        callToFaceTecServerForResult(
//                FaceTecGlobalState.FaceTecServerBaseURL + "/enrollment",
//                parameters,
//                faceTecSessionResult,
//                faceTecFaceScanResultCallback,
//                resultCallback
//        );
//    }
//
//    // Create and send the request.  Parse the results and send the caller what the next step should be (Succeed, Retry, or Cancel).
//    public static void getAuthenticateResponseFromFaceTecServer( FaceTecSessionResult faceTecSessionResult, FaceTecFaceMapResultCallback faceTecFaceScanResultCallback, FaceTecManagedAPICallback resultCallback ) {
//        JSONObject parameters = getAuthenticateParameters(faceTecSessionResult);
//        callToFaceTecServerForResult(
//                FaceTecGlobalState.FaceTecServerBaseURL + "/match-3d-3d",
//                parameters,
//                faceTecSessionResult,
//                faceTecFaceScanResultCallback,
//                resultCallback
//        );
//    }
//
//    // Create and send the request.  Parse the results and send the caller what the next step should be (Succeed, Retry, or Cancel).
//    public static void getPhotoIDMatchResponseFromFaceTecServer(FaceTecIDScanResult faceTecIDScanResult, FaceTecIDScanResultCallback faceTecIDScanResultCallback, FaceTecManagedAPICallback resultCallback ){
//        JSONObject parameters = getPhotoIDParameters(faceTecIDScanResult);
//        callFaceTecManagedAPIForIDCheck(
//                FaceTecGlobalState.FaceTecServerBaseURL + "/id-check",
//                parameters,
//                faceTecIDScanResult,
//                faceTecIDScanResultCallback,
//                resultCallback
//        );
//    }

    static synchronized OkHttpClient getApiClient() {
        if (_apiClient == null) {
            _apiClient = createApiClient();
        }
        return _apiClient;
    }

    // Makes the actual call to the API.
    // Note that for initial integration this sends to the FaceTec Managed Testing API.
    // After deployment of your own instance of ZoOm Server, this will be your own configurable endpoint.
    private static void callToFaceTecServerForResult(String endpoint, JSONObject parameters, String licenseKey, FaceTecSessionResult faceTecSessionResult, final FaceTecFaceScanResultCallback faceTecFaceScanResultCallback, final FaceTecManagedAPICallback resultCallback)  {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), parameters.toString());
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody,
                new ProgressRequestBody.Listener() {
                    @Override
                    public void onUploadProgressChanged(long bytesWritten, long totalBytes) {
                        final float uploadProgressPercent = ((float)bytesWritten) / ((float)totalBytes);
                        faceTecFaceScanResultCallback.uploadProgress(uploadProgressPercent);
                    }
                });
        // Do the network call and handle result
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .header("Content-Type", "application/json")
                .header("X-Device-License-Key", licenseKey)
                .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(faceTecSessionResult.getSessionId()))
                .url(endpoint)
                .post(progressRequestBody);

        if (!FaceTecGlobalState.headers.isEmpty()) {
            for (Map.Entry<String, String> entry : FaceTecGlobalState.headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }

        okhttp3.Request request = requestBuilder.build();

        Log.d("FaceTecSDK", "Sending request to FaceTec server...");
        getApiClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("FaceTecSDK", "Exception raised while attempting HTTPS call.");

                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
                if(!e.getMessage().equals(OK_HTTP_RESPONSE_CANCELED)) {
                    faceTecFaceScanResultCallback.cancel();
                }
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String responseString = response.body().string();
                response.body().close();
                try {
                    JSONObject responseJSON = new JSONObject(responseString);
                    Log.d("FaceTecSDK", "FaceTec server response. " + responseJSON.toString());
                    resultCallback.onResponse(responseJSON);
                }
                catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("FaceTecSDK", "Exception raised while attempting to parse JSON result.");
                    faceTecFaceScanResultCallback.cancel();
                }
            }
        });
    }

    // Makes the actual call to the API.
    // Note that for initial integration this sends to the FaceTec Managed Testing API.
    // After deployment of your own instance of ZoOm Server, this will be your own configurable endpoint.
//    private static void callFaceTecManagedAPIForIDCheck(String endpoint, JSONObject parameters, FaceTecIDScanResult faceTecIDScanResult, final FaceTecIDScanResultCallback faceTecIDScanResultCallback, final FaceTecManagedAPICallback resultCallback) {
//        String sessionId = faceTecIDScanResult.getIDScanMetrics().getSessionId();
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), parameters.toString());
//        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody,
//                new ProgressRequestBody.Listener() {
//                    @Override
//                    public void onUploadProgressChanged(long bytesWritten, long totalBytes) {
//                        final float uploadProgressPercent = ((float)bytesWritten) / ((float)totalBytes);
//                        faceTecIDScanResultCallback.uploadProgress(uploadProgressPercent);
//                    }
//                });
//        // Do the network call and handle result
//        okhttp3.Request request = new okhttp3.Request.Builder()
//                .header("Content-Type", "application/json")
//                .header("X-Device-License-Key", FaceTecGlobalState.DeviceLicenseKeyIdentifier)
//                .header("User-Agent", FaceTecSDK.createFaceTecAPIUserAgentString(sessionId))
//                .url(endpoint)
//                .post(progressRequestBody)
//                .build();
//
//        getApiClient().newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                Log.d("FaceTecSDK", "Exception raised while attempting HTTPS call.");
//
//                // If this comes from HTTPS cancel call, don't set the sub code to NETWORK_ERROR.
//                if(!e.getMessage().equals(OK_HTTP_RESPONSE_CANCELED)) {
//                    faceTecIDScanResultCallback.cancel();
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                String responseString = response.body().string();
//                response.body().close();
//                try {
//                    JSONObject responseJSON = new JSONObject(responseString);
//                    resultCallback.onResponse(responseJSON);
//                }
//                catch(JSONException e) {
//                    e.printStackTrace();
//                    Log.d("FaceTecSDK", "Exception raised while attempting to parse JSON result.");
//                    faceTecIDScanResultCallback.cancel();
//                }
//            }
//        });
//    }
    /*
     * Cancels all in flight requests.
     */
    static public void cancelPendingRequests() {
        OkHttpClient client = getApiClient();

        // Cancel all queued calls
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(OK_HTTP_BUILDER_TAG))
                call.cancel();
        }
        // Cancel all running calls
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(OK_HTTP_BUILDER_TAG))
                call.cancel();
        }
    }

}

/*
 * Implementation of RequestBody that allows upload progress to be retrieved
 */
class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private Listener listener;

    ProgressRequestBody(RequestBody requestBody, Listener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        ProgressStream progressStream = new ProgressStream(sink.outputStream(), contentLength());
        BufferedSink progressSink = Okio.buffer(Okio.sink(progressStream));
        requestBody.writeTo(progressSink);
        progressSink.flush();
    }

    protected final class ProgressStream extends OutputStream {
        private final OutputStream stream;
        private long totalBytes;
        private long bytesSent;

        ProgressStream(OutputStream stream, long totalBytes) {
            this.stream = stream;
            this.totalBytes = totalBytes;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.stream.write(b, off, len);
            if(len < b.length) {
                this.bytesSent += len;
            }
            else {
                this.bytesSent += b.length;
            }
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes);
        }

        @Override
        public void write(int b) throws IOException {
            this.stream.write(b);
            this.bytesSent += 1;
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes);
        }
    }

    interface Listener {
        void onUploadProgressChanged(long bytesWritten, long totalBytes);
    }
}

abstract class FaceTecManagedAPICallback {
    public abstract void onResponse(JSONObject responseJSON);
}

// Helpers for parsing API response to determine if result was a success vs. user needs retry vs. unexpected (cancel out)
// Developers are encouraged to change API call parameters and results to fit their own
class ServerResultHelpers {
    // If Liveness was Determined, succeed.  Otherwise fail.  Unexpected responses cancel.
    public static UXNextStep getLivenessNextStep(JSONObject responseJSONObj){
        try {
            if (responseJSONObj.getJSONObject("data").getInt("livenessStatus") == 0) {
                return UXNextStep.Succeed;
            } else if (responseJSONObj.getJSONObject("data").getInt("livenessStatus") == 1) {
                return UXNextStep.Retry;
            } else {
                return UXNextStep.Cancel;
            }
        }
        catch(JSONException e ){
            return UXNextStep.Cancel;
        }
    }

    // If isEnrolled and Liveness was Determined, succeed.  Otherwise retry.  Unexpected responses cancel.
    public static UXNextStep getEnrollmentNextStep(JSONObject responseJSONObj){
        try {
            if (responseJSONObj.getJSONObject("meta").getInt("code") == 200
                    && ((responseJSONObj.getJSONObject("data").getBoolean("isEnrolled") == true)
                    && responseJSONObj.getJSONObject("data").getInt("livenessStatus") == 0))
            {
                return UXNextStep.Succeed;
            }
            else if (responseJSONObj.getJSONObject("meta").getInt("code") == 200
                    && (responseJSONObj.getJSONObject("data").getBoolean("isEnrolled") == false ))
            {
                return UXNextStep.Retry;
            }
            else
            {
                return UXNextStep.Cancel;
            }
        }
        catch(JSONException e ){
            return UXNextStep.Cancel;
        }
    }

    // If isEnrolled and Liveness was Determined, succeed.  Otherwise retry.  Unexpected responses cancel.
    public static UXNextStep getAuthenticateNextStep(JSONObject responseJSONObj){
        // If both FaceMaps have Liveness Proven, and Match Level is 10 (1 in 4.2 million), then succeed.  Otherwise retry.  Unexpected responses cancel.
        try {
            if(responseJSONObj.has("data") && responseJSONObj.getJSONObject("data").getInt("matchLevel") == 10 &&
                    responseJSONObj.getJSONObject("data").has("targetFaceMap") && responseJSONObj.getJSONObject("data").getJSONObject("targetFaceMap").getInt("livenessStatus") == 0 &&
                    responseJSONObj.getJSONObject("data").has("sourceFaceMap") && responseJSONObj.getJSONObject("data").getJSONObject("sourceFaceMap").getInt("livenessStatus") == 0) {
                return UXNextStep.Succeed;
            }
            else if(responseJSONObj.has("data") &&
                    responseJSONObj.getJSONObject("data").has("targetFaceMap") &&
                    responseJSONObj.getJSONObject("data").has("sourceFaceMap") &&
                    responseJSONObj.getJSONObject("data").getInt("matchLevel") != 10 ||
                    responseJSONObj.getJSONObject("data").getJSONObject("targetFaceMap").getInt("livenessStatus") != 0 ||
                    responseJSONObj.getJSONObject("data").getJSONObject("sourceFaceMap").getInt("livenessStatus") != 0) {
                return UXNextStep.Retry;
            }
            else {
                return UXNextStep.Cancel;
            }
        }
        catch(JSONException e) {
            return UXNextStep.Cancel;
        }
    }

    // If Liveness was Determined and 3D FaceMap matches the ID Scan, succeed.  Otheriwse retry.  Unexpected responses cancel.
    public static UXNextStep getPhotoIDMatchNextStep(JSONObject responseJSON) {
        // If Liveness Proven on FaceMap, and Match Level between FaceMap and ID Photo is non-zero, then succeed.  Otherwise retry.  Unexpected responses cancel.
        try {
            if(responseJSON.has("meta") && responseJSON.getJSONObject("meta").getBoolean("ok") &&
                    responseJSON.getJSONObject("data").getInt("livenessStatus") == 0 &&
                    responseJSON.getJSONObject("data").has("matchLevel") && responseJSON.getJSONObject("data").getInt("matchLevel") != 0) {

                return UXNextStep.Succeed;
            }
            else if(responseJSON.has("data") &&
                    responseJSON.getJSONObject("data").has("matchLevel") &&
                    responseJSON.getJSONObject("data").getInt("livenessStatus") != 0 ||
                    responseJSON.getJSONObject("data").getInt("matchLevel") == 0) {
                return UXNextStep.Retry;
            }
            else {
                return UXNextStep.Cancel;
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
            Log.d("FaceTecSDK", "Error while parsing JSON result.");
            return UXNextStep.Cancel;
        }
    }
}

// A custom networking class is required in order to support 4.4 and below.
class TLSSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
        }
        return socket;
    }
}
