package org.zkoss.zkforge.clipboard;

import org.zkoss.zk.ui.*;
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
 * String text = ClipboardHelper.readText();
 * 
 * // Writing to clipboard
 * ClipboardHelper.writeText("Hello World");
 * }</pre>
 *
 * ClipboardHelper with singleton desktop-level result handling pattern.
 * Only one helper of this type is allowed per page.
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API
 */
public class ClipboardHelper {
    protected static final String CLIPBOARD_HELPER_KEY = "browserkit.clipboardhelper";
    protected static String CLIPBOARD_HELPER_JS_PATH = "~./js/ClipboardHelper.js";
    public static final String EVENT_NAME = "onClipboardAction";
    protected Script clipboardHelperScript;

    protected Consumer<ClipboardResult> textCallback = r -> {};
    protected Consumer<ClipboardImageResult> imageCallback = r -> {};
    protected ClipboardActionListener listener;
    protected Desktop desktop;

    public ClipboardHelper(Consumer<ClipboardResult> textCallback) {
        this(textCallback, null);
    }

    public ClipboardHelper(Consumer<ClipboardResult> textCallback, Consumer<ClipboardImageResult> imageCallback) {
        ensureExecutionAvailable();
        ensureDesktopScopeSingleton();
        setCallbacks(textCallback, imageCallback);
        addPageListener();
        addHelperJavaScript(CLIPBOARD_HELPER_JS_PATH);
    }

    protected void setCallbacks(Consumer<ClipboardResult> textCallback, Consumer<ClipboardImageResult> imageCallback) {
        this.textCallback = textCallback != null ? textCallback : (r -> {});
        this.imageCallback = imageCallback != null ? imageCallback : (r -> {});
    }

    protected void ensureDesktopScopeSingleton() {
        desktop = Executions.getCurrent().getDesktop();
        // Enforce singleton constraint
        if (desktop.getAttribute(CLIPBOARD_HELPER_KEY) != null) {
            throw new IllegalStateException(
                "Only one ClipboardHelper allowed per page. " +
                "Existing instance found.");
        }
        desktop.setAttribute(CLIPBOARD_HELPER_KEY, this);
    }

    protected void addHelperJavaScript(String jsPath) {
        clipboardHelperScript = new Script();
        clipboardHelperScript.setSrc(jsPath);
        clipboardHelperScript.setPage(desktop.getFirstPage());
    }

    protected void removeHelperJavaScript() {
        clipboardHelperScript.detach();
    }

    protected void addPageListener() {
        listener = new ClipboardActionListener();
        listener.setCallbacks(textCallback, imageCallback);
        desktop.getFirstPage().addEventListener(EVENT_NAME, listener);
    }

    protected void removePageListener() {
        desktop.getFirstPage().removeEventListener(EVENT_NAME, listener);
    }

    public void writeText(String text) {
        Clients.evalJavaScript("ClipboardHelper.writeText('" + text + "')");
    }

    public void readText() {
        Clients.evalJavaScript("ClipboardHelper.readText()");
    }

    public void readImage() {
        // imageCallback is always non-null now
        Clients.evalJavaScript("ClipboardHelper.readImage()");
    }

    /**
     * Dispose this helper, removing the page listener and the helper JavaScript.
     * After calling this method, this instance should not be used anymore.
     */
    public void dispose() {
        desktop.removeAttribute(CLIPBOARD_HELPER_KEY);
        removePageListener();
        removeHelperJavaScript();
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

    /**
     * returns the ClipboardHelper associated with the current desktop, or null if none.
     * @return
     */
    static public ClipboardHelper getInstance() {
        return (ClipboardHelper) Executions.getCurrent().getDesktop().getAttribute(CLIPBOARD_HELPER_KEY);
    }
}