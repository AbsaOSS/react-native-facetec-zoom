// Demonstrates performing a Liveness Check.

package processors;

import android.content.Context;
import org.json.JSONObject;

import com.facetec.sdk.*;

public class LivenessCheckProcessor extends Processor implements FaceTecFaceScanProcessor {
    FaceTecFaceScanResultCallback faceTecFaceScanResultCallback;
    FaceTecSessionResult latestFaceTecSessionResult;
    private boolean _isSuccess = false;
    private final String licenseKey;

    public LivenessCheckProcessor(Context context, String licenseKey) {
        // Launch the ZoOm Session.
        this.licenseKey = licenseKey;
        FaceTecSessionActivity.createAndLaunchSession(context, this);
    }

    public boolean isSuccess() {
        return _isSuccess;
    }

    // Required function that handles calling ZoOm Server to get result and decides how to continue.
    public void processSessionWhileFaceTecSDKWaits(final FaceTecSessionResult faceTecSessionResult, final FaceTecFaceScanResultCallback faceTecFaceScanResultCallback) {
        this.latestFaceTecSessionResult = faceTecSessionResult;
        this.faceTecFaceScanResultCallback = faceTecFaceScanResultCallback;

        // Cancel last request in flight.  This handles case where processing is is taking place but cancellation or Context Switch occurs.
        // Our handling here ends the latest in flight request and simply re-does the normal logic, which will cancel out.
        NetworkingHelpers.cancelPendingRequests();

        // cancellation, timeout, etc.
        if (faceTecSessionResult.getStatus() != FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY) {
            faceTecFaceScanResultCallback.cancel();
            this.faceTecFaceScanResultCallback = null;
            return;
        }

        // Create and parse request to ZoOm Server.
        NetworkingHelpers.getLivenessCheckResponseFromFaceTecServer(licenseKey, faceTecSessionResult, this.faceTecFaceScanResultCallback, new FaceTecManagedAPICallback() {
            @Override
            public void onResponse(JSONObject responseJSON) {
                UXNextStep nextStep = ServerResultHelpers.getLivenessNextStep(responseJSON);

                if (nextStep == UXNextStep.Succeed) {
                    _isSuccess = true;
                    FaceTecCustomization.overrideResultScreenSuccessMessage = "Liveness\nConfirmed";
                    faceTecFaceScanResultCallback.succeed();
                }
                else if (nextStep == UXNextStep.Retry) {
                    faceTecFaceScanResultCallback.retry();
                }
                else {
                    faceTecFaceScanResultCallback.cancel();
                }
            }
        });

    }
}
