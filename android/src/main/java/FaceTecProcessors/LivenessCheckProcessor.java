// Demonstrates performing a Liveness Check.

package FaceTecProcessors;

import android.content.Context;
import org.json.JSONObject;

import com.facetec.sdk.*;

public class LivenessCheckProcessor extends Processor implements FaceTecFaceMapProcessor {
    FaceTecFaceMapResultCallback faceTecFaceMapResultCallback;
    FaceTecSessionResult latestFaceTecSessionResult;
    private boolean _isSuccess = false;
    private final String licenseKey;

    public LivenessCheckProcessor(Context context, String licenseKey) {
        // Launch the ZoOm Session.
        this.licenseKey = licenseKey;
        FaceTecSessionActivity.createAndLaunchFaceTecSession(context, this);
    }

    public boolean isSuccess() {
        return _isSuccess;
    }

    // Required function that handles calling ZoOm Server to get result and decides how to continue.
    public void processFaceTecSessionResultWhileFaceTecWaits(final FaceTecSessionResult faceTecSessionResult, final FaceTecFaceMapResultCallback faceTecFaceMapResultCallback) {
        this.latestFaceTecSessionResult = faceTecSessionResult;
        this.faceTecFaceMapResultCallback = faceTecFaceMapResultCallback;

        // Cancel last request in flight.  This handles case where processing is is taking place but cancellation or Context Switch occurs.
        // Our handling here ends the latest in flight request and simply re-does the normal logic, which will cancel out.
        NetworkingHelpers.cancelPendingRequests();

        // cancellation, timeout, etc.
        if (faceTecSessionResult.getStatus() != FaceTecSessionStatus.SESSION_COMPLETED_SUCCESSFULLY) {
            faceTecFaceMapResultCallback.cancel();
            this.faceTecFaceMapResultCallback = null;
            return;
        }

        // Create and parse request to ZoOm Server.
        NetworkingHelpers.getLivenessCheckResponseFromFaceTecServer(licenseKey, faceTecSessionResult, this.faceTecFaceMapResultCallback, new FaceTecManagedAPICallback() {
            @Override
            public void onResponse(JSONObject responseJSON) {
                UXNextStep nextStep = ServerResultHelpers.getLivenessNextStep(responseJSON);

                if (nextStep == UXNextStep.Succeed) {
                    _isSuccess = true;
                    FaceTecCustomization.overrideResultScreenSuccessMessage = "Liveness\nConfirmed";
                    faceTecFaceMapResultCallback.succeed();
                }
                else if (nextStep == UXNextStep.Retry) {
                    faceTecFaceMapResultCallback.retry();
                }
                else {
                    faceTecFaceMapResultCallback.cancel();
                }
            }
        });

    }
}
