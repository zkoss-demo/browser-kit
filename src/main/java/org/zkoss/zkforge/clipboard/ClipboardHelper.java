package org.zkoss.zkforge.clipboard;

import com.google.gson.Gson;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

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
 * ClipboardHelper.readText(result -> {
 *     if (result.isSuccess()) {
 *         String text = result.getText();
 *         // Handle clipboard text
 *     } else {
 *         // Handle error
 *     }
 * });
 * 
 * // Writing to clipboard
 * ClipboardHelper.writeText("Hello World", result -> {
 *     if (result.isSuccess()) {
 *         // Text successfully written
 *     }
 * });
 * }</pre>
 * 
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API">Clipboard API - MDN</a>
 */
public class ClipboardHelper {
    public static final String WIDGET_NAME = ClipboardHelper.class.getSimpleName();
    public static final String CSS_CLASS = "z-" + WIDGET_NAME.toLowerCase();
    public static final String CLIPBOARD_HELPER_JS_PATH = "~./js/" + WIDGET_NAME + ".js";
    private static Div anchor;
    private final Consumer<ClipboardResult> callback;

    /**
     * Creates a new ClipboardHelper instance with the specified callback.
     * 
     * @param callback the callback to handle clipboard operation results
     * @throws IllegalStateException if no ZK execution context is available
     */
    public ClipboardHelper(Consumer<ClipboardResult> callback) {
        ensureExecutionAvailable();
        this.callback = callback;
        initAnchorComponent();
        initHelperJavaScript(CLIPBOARD_HELPER_JS_PATH);
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
     */
    protected void initAnchorComponent() {
        anchor = new Div();
        anchor.setSclass(CSS_CLASS);
        anchor.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        anchor.addEventListener("onClipboardData", event -> {
            callback.accept(parseResponse((JSONObject) event.getData()));
        });
    }

    private static final Gson gson = new Gson();

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
        return gson.fromJson(data.toString(), ClipboardResult.class);
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