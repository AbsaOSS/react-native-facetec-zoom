package FaceTecProcessors;

import android.graphics.Color;

//import com.facetec.faceTec.sampleapp.R;
import com.facetec.sdk.FaceTecCustomization;
import com.facetec.sdk.FaceTecSDK;

public class ThemeHelpers {

    public static void setAppTheme(String theme) {
        if(theme.equals("ZoOm Theme")) {
            FaceTecGlobalState.currentCustomization = new FaceTecCustomization();
        }
        else if(theme.equals("Well-Rounded")) {
            int primaryColor = Color.parseColor("#09B5A3"); // green
            int backgroundColor = Color.parseColor("#FFFFFF"); // white

            // Overlay Customization
            FaceTecGlobalState.currentCustomization.getOverlayCustomization().backgroundColor = Color.parseColor("#00000000");
            // FaceTecGlobalState.currentCustomization.getOverlayCustomization().brandingImage = R.drawable.blank_image;
            // Guidance Customization
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonCornerRadius = 30;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenOvalFillColor = Color.parseColor("#4D09B5A3");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundCornerRadius = 5;
            // Guidance Image Customization
            // FaceTecGlobalState.currentCustomization.getGuidanceCustomization().getImageCustomization().cameraPermissionsScreenImage = R.drawable.camera_green;
            // ID Scan Customization
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().showSelectionScreenBrandingImage = false;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonCornerRadius = 5;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundCornerRadius = 5;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundCornerRadius = 5;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenPassportFrameImage = R.drawable.faceTec_passport_frame_green;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenIDCardFrameImage = R.drawable.faceTec_id_card_frame_green;
            // Result Screen Customization
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().activityIndicatorColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressTrackColor = Color.parseColor("#33000000");
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressFillColor = primaryColor;
            // Feedback Customization
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().backgroundColors = primaryColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().textColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().cornerRadius = 5;
            // Frame Customization
            FaceTecGlobalState.currentCustomization.getFrameCustomization().backgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderWidth = 0;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().cornerRadius = 20;
            // Oval Customization
            FaceTecGlobalState.currentCustomization.getOvalCustomization().strokeColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor1 = primaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor2 = primaryColor;
            // Cancel Button Customization
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImage = R.drawable.cancel_round_green;
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImageLowLight = R.drawable.cancel_round_green;

        }
        else if(theme.equals("Dark-Side")) {
            int primaryColor = Color.parseColor("#00AEED"); // blue
            int backgroundColor = Color.parseColor("#000000"); // black

            // Overlay Customization
            FaceTecGlobalState.currentCustomization.getOverlayCustomization().backgroundColor = backgroundColor;
            // FaceTecGlobalState.currentCustomization.getOverlayCustomization().brandingImage = R.drawable.blank_image;
            // Guidance Customization
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundNormalColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderWidth = 1;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenOvalFillColor = Color.parseColor("#4D00AEED");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundCornerRadius = 5;
            // Guidance Image Customization
            // FaceTecGlobalState.currentCustomization.getGuidanceCustomization().getImageCustomization().cameraPermissionsScreenImage = R.drawable.camera_blue;
            // ID Scan Customization
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().showSelectionScreenBrandingImage = false;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundNormalColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderWidth = 1;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundCornerRadius = 8;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundCornerRadius = 8;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenPassportFrameImage = R.drawable.faceTec_passport_frame_blue;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenIDCardFrameImage = R.drawable.faceTec_id_card_frame_blue;
            // Result Screen Customization
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().activityIndicatorColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressTrackColor = Color.parseColor("#33FFFFFF");
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressFillColor = primaryColor;
            // Feedback Customization
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().backgroundColors = primaryColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().textColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().cornerRadius = 5;
            // Frame Customization
            FaceTecGlobalState.currentCustomization.getFrameCustomization().backgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderWidth = 2;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().cornerRadius = 5;
            // Oval Customization
            FaceTecGlobalState.currentCustomization.getOvalCustomization().strokeColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor1 = Color.parseColor("#BF00AEED");
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor2 = Color.parseColor("#BF00AEED");
            // Cancel Button Customization
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImage = R.drawable.double_chevron_left_blue;
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImageLowLight = R.drawable.double_chevron_left_blue;
        }
        else if(theme.equals("Bitcoin Exchange")) {
            int primaryColor = Color.parseColor("#F79634"); // orange
            int secondaryColor = Color.parseColor("#FFFF1E"); // yellow
            int backgroundColor = Color.parseColor("#424242"); // dark grey

            // Overlay Customization
            FaceTecGlobalState.currentCustomization.getOverlayCustomization().backgroundColor = Color.parseColor("#00000000");
            // FaceTecGlobalState.currentCustomization.getOverlayCustomization().brandingImage = R.drawable.bitcoin_exchange_logo;
            // Guidance Customization
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonCornerRadius = 5;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenOvalFillColor = Color.parseColor("#4DF79634");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundCornerRadius = 5;
            // Guidance Image Customization
            // FaceTecGlobalState.currentCustomization.getGuidanceCustomization().getImageCustomization().cameraPermissionsScreenImage = R.drawable.camera_orange;
            // ID Scan Customization
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().showSelectionScreenBrandingImage = false;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonCornerRadius = 5;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundCornerRadius = 8;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundCornerRadius = 8;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenPassportFrameImage = R.drawable.faceTec_passport_frame_orange;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenIDCardFrameImage = R.drawable.faceTec_id_card_frame_orange;
            // Result Screen Customization
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().activityIndicatorColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressTrackColor = Color.parseColor("#33000000");
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressFillColor = primaryColor;
            // Feedback Customization
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().backgroundColors = primaryColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().textColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().cornerRadius = 5;
            // Frame Customization
            FaceTecGlobalState.currentCustomization.getFrameCustomization().backgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderColor = secondaryColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderWidth = 0;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().cornerRadius = 5;
            // Oval Customization
            FaceTecGlobalState.currentCustomization.getOvalCustomization().strokeColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor1 = secondaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor2 = secondaryColor;
            // Cancel Button Customization
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImage = R.drawable.back_orange;
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImageLowLight = R.drawable.back_orange;
        }
        else if(theme.equals("eKYC")) {
            int primaryColor = Color.parseColor("#ED1C24"); // red
            int secondaryColor = Color.parseColor("#000000"); // black
            int backgroundColor = Color.parseColor("#FFFFFF"); // white

            // Overlay Customization
            FaceTecGlobalState.currentCustomization.getOverlayCustomization().backgroundColor = Color.parseColor("#00000000");
            // FaceTecGlobalState.currentCustomization.getOverlayCustomization().brandingImage = R.drawable.ekyc_logo;
            // Guidance Customization
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().foregroundColor = secondaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundNormalColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonCornerRadius = 8;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenOvalFillColor = Color.parseColor("#4DF79634");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundCornerRadius = 3;
            // Guidance Image Customization
            // FaceTecGlobalState.currentCustomization.getGuidanceCustomization().getImageCustomization().cameraPermissionsScreenImage = R.drawable.camera_red;
            // ID Scan Customization
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBrandingImage = R.drawable.ekyc_logo;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().showSelectionScreenBrandingImage = true;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundNormalColor = Color.parseColor("#00000000");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundHighlightColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonCornerRadius = 8;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundCornerRadius = 2;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenPassportFrameImage = R.drawable.faceTec_passport_frame_red;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenIDCardFrameImage = R.drawable.faceTec_id_card_frame_red;
            // Result Screen Customization
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().foregroundColor = secondaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().activityIndicatorColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressTrackColor = Color.parseColor("#33000000");
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressFillColor = primaryColor;
            // Feedback Customization
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().backgroundColors = secondaryColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().textColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().cornerRadius = 3;
            // Frame Customization
            FaceTecGlobalState.currentCustomization.getFrameCustomization().backgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderWidth = 2;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().cornerRadius = 8;
            // Oval Customization
            FaceTecGlobalState.currentCustomization.getOvalCustomization().strokeColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor1 = Color.parseColor("#BFED1C24");
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor2 = Color.parseColor("#BFED1C24");
            // Cancel Button Customization
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImage = R.drawable.cancel_box_red;
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImageLowLight = R.drawable.cancel_box_red;
        }
        else if(theme.equals("Sample Bank")) {
            int primaryColor = Color.parseColor("#FFFFFF"); // white
            int backgroundColor = Color.parseColor("#1D174F"); // navy

            // Overlay Customization
            FaceTecGlobalState.currentCustomization.getOverlayCustomization().backgroundColor = primaryColor;
            // FaceTecGlobalState.currentCustomization.getOverlayCustomization().brandingImage = R.drawable.sample_bank_logo;
            // Guidance Customization
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBackgroundHighlightColor = Color.parseColor("#BFFFFFFF");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().buttonCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenOvalFillColor = Color.parseColor("#4DFFFFFF");
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getGuidanceCustomization().readyScreenTextBackgroundCornerRadius = 2;
            // Guidance Image Customization
            // FaceTecGlobalState.currentCustomization.getGuidanceCustomization().getImageCustomization().cameraPermissionsScreenImage = R.drawable.camera_white_navy;
            // ID Scan Customization
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBrandingImage = R.drawable.sample_bank_logo;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().showSelectionScreenBrandingImage = true;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenBackgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().selectionScreenForegroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextNormalColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundNormalColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonTextHighlightColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBackgroundHighlightColor = Color.parseColor("#BFFFFFFF");
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonBorderWidth = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().buttonCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenTextBackgroundCornerRadius = 2;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundBorderWidth = 0;
            FaceTecGlobalState.currentCustomization.getIdScanCustomization().reviewScreenTextBackgroundCornerRadius = 2;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenPassportFrameImage = R.drawable.faceTec_passport_frame_navy;
            // FaceTecGlobalState.currentCustomization.getIdScanCustomization().captureScreenIDCardFrameImage = R.drawable.faceTec_id_card_frame_navy;
            // Result Screen Customization
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().backgroundColors = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().foregroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().activityIndicatorColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationBackgroundColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().resultAnimationForegroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressTrackColor = Color.parseColor("#33FFFFFF");
            FaceTecGlobalState.currentCustomization.getResultScreenCustomization().uploadProgressFillColor = primaryColor;
            // Feedback Customization
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().backgroundColors = primaryColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().textColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFeedbackCustomization().cornerRadius = 2;
            // Frame Customization
            FaceTecGlobalState.currentCustomization.getFrameCustomization().backgroundColor = backgroundColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderColor = primaryColor;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().borderWidth = 2;
            FaceTecGlobalState.currentCustomization.getFrameCustomization().cornerRadius = 2;
            // Oval Customization
            FaceTecGlobalState.currentCustomization.getOvalCustomization().strokeColor = Color.parseColor("#B3B3B3");
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor1 = Color.parseColor("#BFFFFFFF");
            FaceTecGlobalState.currentCustomization.getOvalCustomization().progressColor2 = Color.parseColor("#BFFFFFFF");
            // Cancel Button Customization
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImage = R.drawable.cancel_white;
            // FaceTecGlobalState.currentCustomization.getCancelButtonCustomization().customImageLowLight = R.drawable.cancel_navy;
        }

        FaceTecSDK.setCustomization(FaceTecGlobalState.currentCustomization);
    }
}
