package org.zkoss.zkforge.clipboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.Event;

import java.util.Base64;
import java.util.Map;

public class ClipboardEvent extends Event {
    public static final String EVENT_NAME = "onClipboardAction";
    protected ClipboardResult clipboardResult;
    protected static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public ClipboardEvent(ClipboardResult clipboardResult) {
        // null target indicates the event is sent to all root components
        super(EVENT_NAME, null);
        this.clipboardResult = clipboardResult;
    }

    public static ClipboardEvent getEvent(AuRequest request) {
        Map<String, Object> data = request.getData();
        ClipboardResult result = parseResponse(data);
        return new ClipboardEvent(result);
    }

    /**
     * Parses the AU request data from JavaScript clipboard operations into a ClipboardResult object.
     * @param data The AU request data map
     * @return ClipboardResult containing the parsed data
     */
    protected static ClipboardResult parseResponse(Map<String, Object> data) {
        if (ClipboardAction.READ_IMAGE.toString().equals(data.get("action"))) {
            return parseImageResponse(data);
        } else {
            return GSON.fromJson(GSON.toJson(data), ClipboardResult.class);
        }
    }

    /**
     * Parses the AU request data from JavaScript clipboard image operations into a ClipboardImageResult object.
     * @param data The AU request data map
     * @return ClipboardImageResult containing image details
     */
    protected static ClipboardImageResult parseImageResponse(Map<String, Object> data) {
        // Use Gson to handle most fields automatically
        ClipboardImageResult result = GSON.fromJson(GSON.toJson(data), ClipboardImageResult.class);
        
        // Handle imageData separately with Base64 decoding
        if (data.get("imageData") != null) {
            try {
                String base64Data = data.get("imageData").toString();
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                result.setImageData(imageBytes);
            } catch (IllegalArgumentException e) {
                // If Base64 decoding fails, set error
                result.setError("Invalid image data format");
                return result;
            }
        }
        
        return result;
    }

    public boolean isSuccess() {
        return clipboardResult != null && clipboardResult.isSuccess();
    }

    public ClipboardResult getClipboardResult() {
        return clipboardResult;
    }

    public boolean isImageResult() {
        return clipboardResult instanceof ClipboardImageResult;
    }

    public ClipboardImageResult getClipboardImageResult() {
        return isImageResult() ? (ClipboardImageResult) clipboardResult : null;
    }
}