package org.zkoss.zkforge.clipboard;

import com.google.gson.Gson;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import java.util.Base64;
import java.util.function.Consumer;

public class ClipboardActionListener implements EventListener<Event> {
    protected static final Gson GSON = new Gson();
    protected Consumer<ClipboardResult> textCallback;
    protected Consumer<ClipboardImageResult> imageCallback;
    protected Event handledEvent;

    /**
     * This event doesn't have any target component, as it is desktop-level. So ZK will invoke it multiple times if there are multiple root components.
     * @see org.zkoss.zk.ui.impl.UiEngineImpl UiEngineImpl#process()
     */
    @Override
    public void onEvent(Event event) {
        // follow HistoryPopStateEvent to avoid handling the same event for multiple times see org.zkoss.bind.BindComposer
        if (event == handledEvent) {
            return;
        }
        handledEvent = event;
        
        JSONObject data = (JSONObject) event.getData();
        ClipboardResult result = parseResponse(data);
        
        // Check which callback to call based on result type
        if (result instanceof ClipboardImageResult && imageCallback != null) {
            imageCallback.accept((ClipboardImageResult) result);
        } else {
            textCallback.accept(result);
        }
    }

    /**
     * Parses the JSON response from JavaScript clipboard operations into a ClipboardResult object.
     * 
     * @param data the JSON response from JavaScript
     * @return the parsed ClipboardResult
     */
    protected ClipboardResult parseResponse(JSONObject data) {
        if (ClipboardAction.READ_IMAGE.toString().equals(data.get("action"))) {
            return parseImageResponse(data);
        } else {
            return GSON.fromJson(data.toString(), ClipboardResult.class);
        }
    }

    /**
     * Parses the JSON response from JavaScript clipboard image operations into a ClipboardImageResult object.
     * 
     * @param data the JSON response from JavaScript
     * @return the parsed ClipboardImageResult
     */
    protected ClipboardImageResult parseImageResponse(JSONObject data) {
        // Use Gson to handle most fields automatically
        ClipboardImageResult result = GSON.fromJson(data.toString(), ClipboardImageResult.class);
        
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

    public void setCallbacks(Consumer<ClipboardResult> textCallback,
                           Consumer<ClipboardImageResult> imageCallback) {
        this.textCallback = textCallback;
        this.imageCallback = imageCallback;
    }
}