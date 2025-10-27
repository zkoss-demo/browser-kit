package org.zkoss.zkforge.clipboard;

import java.util.Base64;
import java.util.Map;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ClipboardEvent extends Event {
    public static final String EVENT_NAME = "onClipboardAction";
    private ClipboardResult result;
    protected static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public ClipboardEvent(ClipboardResult result, Component target) {
        // null target indicates the event is sent to all root components
        super(EVENT_NAME, target);
        this.result = result;
    }

    public static ClipboardEvent getEvent(AuRequest request) {
        Map<String, Object> data = request.getData();
        if (data == null) {
            ClipboardResult errorResult = new ClipboardText();
            errorResult.setError(new ClipboardError(ClipboardError.SERVER_ERROR, "No data received from request"));
            return new ClipboardEvent(errorResult, request.getComponent());
        }

        ClipboardResult result = parseResponse(data);
        if (result == null) {
            result = new ClipboardText();
            result.setError(new ClipboardError(ClipboardError.SERVER_ERROR, "Failed to parse clipboard response"));
        }
        return new ClipboardEvent(result, request.getComponent());
    }

    /**
     * Checks if the clipboard operation was successful.
     *
     * @return true if the operation succeeded, false otherwise
     */
    public boolean isSuccess() {
        return result != null && result.isSuccess();
    }

    /**
     * Gets the clipboard result.
     *
     * @return the clipboard result (ClipboardText or ClipboardImage)
     */
    public ClipboardResult getResult() {
        return result;
    }

    /**
     * Checks if this is a text result.
     *
     * @return true if the result contains text data
     */
    public boolean isTextResult() {
        return result instanceof ClipboardText;
    }

    /**
     * Calling {@link #isTextResult()} before calling this method.
     * Gets the clipboard text if available.
     *
     * @return the ClipboardText result, or null if this is not a text result
     */
    public ClipboardText getClipboardText() {
        return isTextResult() ? (ClipboardText) result : null;
    }

    /**
     * Calling {@link #isTextResult()} before calling this method.
     * Gets the clipboard image if available.
     *
     * @return the ClipboardImage result, or null if this is not an image result
     */
    public ClipboardImage getClipboardImage() {
        return result instanceof ClipboardImage ? (ClipboardImage) result : null;
    }

    /**
     * Parses the AU request data from JavaScript clipboard operations into a ClipboardResult object.
     *
     * @param data The AU request data map
     * @return ClipboardResult containing the parsed data
     */
    protected static ClipboardResult parseResponse(Map<String, Object> data) {
        if (isImageAction(data)) {
            return parseImageResponse(data);
        } else {
            return parseTextResponse(data);
        }
    }


    private static boolean isImageAction(Map<String, Object> data) {
        return ClipboardAction.READ_IMAGE.toString().equals(data.get("action"));
    }

    private static ClipboardResult parseTextResponse(Map<String, Object> data) {
        return GSON.fromJson(GSON.toJson(data), ClipboardText.class);
    }

    /**
     * Parses the AU request data from JavaScript clipboard image operations into a ClipboardImage object.
     *
     * @param data The AU request data map
     * @return ClipboardImage containing image details
     */
    private static ClipboardImage parseImageResponse(Map<String, Object> data) {
        ClipboardImage result = GSON.fromJson(GSON.toJson(data), ClipboardImage.class);

        decodeImageData(data, result);

        return result;
    }

    private static void decodeImageData(Map<String, Object> data, ClipboardImage result) {
        if (data.get("imageData") != null) {
            try {
                String base64Data = data.get("imageData").toString();
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                result.setImageData(imageBytes);
            } catch (IllegalArgumentException e) {
                ClipboardError error = new ClipboardError(ClipboardError.SERVER_ERROR, "Invalid image data format");
                result.setError(error);
            }
        }
    }


}