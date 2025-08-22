package test.clipboard;

import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.ClipboardHelper;
import org.zkoss.zkforge.clipboard.ClipboardImageResult;
import org.zkoss.zul.*;

/**
 * Demonstrates how to use the ClipboardHelper for reading images from clipboard.
 */
public class ClipboardImageComposer extends SelectorComposer<Component> {
    
    @Wire
    private Label statusMessage;
    @Wire
    private Div statusContainer;
    @Wire
    private Div imageContainer;
    @Wire
    private Image clipboardImage;

    private ClipboardHelper clipboardHelper;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        clipboardHelper = new ClipboardHelper(null, this::handleImageResult);
    }

    /**
     * Handles the "Read Image from Clipboard" button click for image-only demo
     */
    @Listen("onClick = #readImageButton")
    public void readImage() {
        showStatus("📋 Reading image from clipboard...", "info");
        hideImageResult();
        clipboardHelper.readImage();
    }
    

    /**
     * Processes image data from the image-only clipboard helper
     */
    public void handleImageResult(ClipboardImageResult result) {
        if (result.isSuccess() && result.hasImageData()) {
            displayImageResult(result);
            showStatus("✅ Image successfully read from clipboard!", "success");
        } else {
            String errorMsg = result.getError() != null ? result.getError() : "Unknown error occurred";
            showStatus("❌ Failed to read image: " + errorMsg, "error");
            hideImageResult();
        }
    }

    /**
     * Displays the image result in the main demo area
     */
    private void displayImageResult(ClipboardImageResult result) {
        try {
            // Create AImage from byte data
            AImage aImage = new AImage("clipboard-image", result.getImageData());
            clipboardImage.setContent(aImage);
            
            // Update info fields using native HTML spans (since they're in h: namespace)
            updateInfoField("mimeTypeInfo", result.getMimeType());
            updateInfoField("dimensionsInfo", result.getWidth() + " × " + result.getHeight() + " pixels");
            updateInfoField("fileSizeInfo", formatFileSize(result.getSize()));
            updateInfoField("supportedInfo", result.isSupportedFormat() ? "✅ Yes" : "⚠️ Unsupported");
            
            imageContainer.setVisible(true);
            
        } catch (Exception e) {
            showStatus("❌ Failed to display image: " + e.getMessage(), "error");
            imageContainer.setVisible(false);
        }
    }
    
    private void updateInfoField(String componentId, String value) {
        ((Label)getPage().getFellow(componentId)).setValue(value);
    }
    

    /**
     * Shows a read action status message
     */
    private void showStatus(String message, String type) {
        statusMessage.setValue(message);
        
        String cssClass = "success".equals(type) ? "success-message" : 
                         "error".equals(type) ? "error-message" : "image-info";
        statusMessage.setSclass(cssClass);
        statusMessage.setVisible(true);
        statusContainer.setVisible(true);
    }
    
    /**
     * Hides the image result display
     */
    private void hideImageResult() {
        imageContainer.setVisible(false);
    }
    

    /**
     * Formats file size in human readable format
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    /**
     * Escapes text for safe use in JavaScript strings
     */
    private String escapeJavaScript(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("'", "\\'")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
    /**
     * Escapes text for safe use in HTML
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#x27;");
    }
}