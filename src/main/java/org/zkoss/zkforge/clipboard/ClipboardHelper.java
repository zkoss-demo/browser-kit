package org.zkoss.zkforge.clipboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Base64;
import java.util.function.Consumer;

/**
 * Helper class that provides Java access to the browser's Clipboard API.
 * 
 * <p>This class wraps the native browser Clipboard API and provides methods to read from and write to
 * the system clipboard. All operations are asynchronous and require user interaction due to browser
 * security restrictions.</p>
 * 
 * <p><strong>Important:</strong> Clipboard operations must be triggered from user interactions 
 * (click, keypress, etc.) as they will fail if called outside of event handlers.</p>
 * 
 * <p><strong>Security Note:</strong> When handling clipboard content, sanitize user input to prevent
 * potential security vulnerabilities.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * // Reading from clipboard
 * String text = ClipboardHelper.readText();
 * 
 * // Writing to clipboard
 * ClipboardHelper.writeText("Hello World");
 * }</pre>
 * 
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API">Clipboard API - MDN</a>
 */
public class ClipboardHelper {
    public static final String WIDGET_NAME = ClipboardHelper.class.getSimpleName();
    public static final String CSS_CLASS = "z-" + WIDGET_NAME.toLowerCase();
    public static final String CLIPBOARD_HELPER_JS_PATH = "~./js/" + WIDGET_NAME + ".js";
    public static final String ON_CLIPBOARD_ACTION = "onClipboardAction";
    private Div anchor;
    private final Consumer<ClipboardResult> textCallback;
    private final Consumer<ClipboardImageResult> imageCallback;

    /**
     * Creates a new ClipboardHelper instance with the specified callback for text operations.
     * 
     * @param textCallback the callback to handle clipboard text operation results
     * @throws IllegalStateException if no ZK execution context is available
     */
    public ClipboardHelper(Consumer<ClipboardResult> textCallback) {
        this(textCallback, null);
    }

    /**
     * Creates a new ClipboardHelper instance with callbacks for both text and image operations.
     * 
     * @param textCallback the callback to handle clipboard text operation results
     * @param imageCallback the callback to handle clipboard image operation results, can be null
     * @throws IllegalStateException if no ZK execution context is available
     */
    public ClipboardHelper(Consumer<ClipboardResult> textCallback, Consumer<ClipboardImageResult> imageCallback) {
        ensureExecutionAvailable();
        this.textCallback = textCallback;
        initAnchorComponent();
        initHelperJavaScript(CLIPBOARD_HELPER_JS_PATH);
        this.imageCallback = imageCallback;
    }

    /**
     * Initializes the JavaScript helper script for clipboard operations.
     * 
     * @param jsPath the path to the JavaScript helper file
     */
    protected void initHelperJavaScript(String jsPath) {
        Script clipboardHelperScript = new Script();
        clipboardHelperScript.setSrc(jsPath);
        clipboardHelperScript.setPage(Executions.getCurrent().getDesktop().getFirstPage());
    }

    /**
     * Writes the specified text to the clipboard.
     * 
     * <p><strong>Important:</strong> This method must be called from within a user-initiated
     * event handler (e.g., button click) due to browser security restrictions.</p>
     * 
     * @param text the text to write to the clipboard
     */
    public void writeText(String text) {
        String jsCode = String.format("%s.writeText('%s')", WIDGET_NAME, text);
        Clients.evalJavaScript(jsCode);
    }

    /**
     * Reads text from the clipboard.
     * 
     * <p>The result will be provided asynchronously through the callback specified
     * in the constructor.</p>
     * 
     * <p><strong>Important:</strong> This method must be called from within a user-initiated
     * event handler (e.g., button click) due to browser security restrictions.</p>
     */
    public void readText() {
        String jsCode = String.format("%s.readText()", WIDGET_NAME);
        Clients.evalJavaScript(jsCode);
    }

    /**
     * Initializes the anchor component that receives clipboard operation events from JavaScript.
     * Reads image data from the clipboard.
     * 
     * <p>The result will be provided asynchronously through the image callback specified
     * in the constructor. If no image callback was provided, this method will throw an exception.</p>
     * 
     * <p><strong>Important:</strong> This method must be called from within a user-initiated
     * event handler (e.g., button click) due to browser security restrictions.</p>
     * 
     * <p><strong>Browser Support:</strong> This method requires modern browser support for
     * the Clipboard API read() method. Supported in Chrome 88+, Firefox 127+, and Edge 88+.
     * Limited support in Safari.</p>
     * 
     * @throws IllegalStateException if no image callback was provided in the constructor
     */
    protected void initAnchorComponent() {
        anchor = new Div();
        anchor.setSclass(CSS_CLASS);
        anchor.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        anchor.addEventListener(ON_CLIPBOARD_ACTION, event -> {
            ClipboardResult result = parseResponse((JSONObject) event.getData());

            // Check which callback to call based on result type
            if (result instanceof ClipboardImageResult && imageCallback != null) {
                imageCallback.accept((ClipboardImageResult) result);
            } else {
                textCallback.accept(result);
            }
        });
    }

    public void readImage() {
        if (imageCallback == null) {
            throw new IllegalStateException("Image callback not provided. Use ClipboardHelper(Consumer<ClipboardResult>, Consumer<ClipboardImageResult>) constructor to enable image support.");
        }
        String jsCode = String.format("%s.readImage()", WIDGET_NAME);
        Clients.evalJavaScript(jsCode);
    }

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    /**
     * Parses the JSON response from JavaScript clipboard operations into a ClipboardResult object.
     * 
     * <p>The JavaScript sends action as uppercase strings ('READ', 'WRITE') which are automatically
     * mapped to the ClipboardAction enum values.</p>
     * 
     * @param data the JSON response from JavaScript
     * @return the parsed ClipboardResult
     */
    protected ClipboardResult parseResponse(JSONObject data){
        if (ClipboardAction.READ_IMAGE.toString().equals(data.get("action"))){
            return parseImageResponse(data);
        }else{
            return gson.fromJson(data.toString(), ClipboardResult.class);
        }
    }

    /**
     * Parses the JSON response from JavaScript clipboard image operations into a ClipboardImageResult object.
     * 
     * <p>The JavaScript sends image data as Base64-encoded strings which are automatically
     * decoded into byte arrays.</p>
     * 
     * @param data the JSON response from JavaScript
     * @return the parsed ClipboardImageResult
     */
    protected ClipboardImageResult parseImageResponse(JSONObject data) {
        // Use Gson to handle most fields automatically
        ClipboardImageResult result = gson.fromJson(data.toString(), ClipboardImageResult.class);
        
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

    /**
     * Ensures that a ZK execution context is available for clipboard operations.
     * 
     * @throws IllegalStateException if no ZK execution context is available
     */
    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }
}