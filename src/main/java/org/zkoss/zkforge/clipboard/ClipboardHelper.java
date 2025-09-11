package org.zkoss.zkforge.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import java.util.Optional;

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
 * // Get the singleton instance
 * ClipboardHelper helper = ClipboardHelper.getInstance();
 * 
 * // Reading from clipboard (results delivered via desktop events)
 * helper.readText();
 * 
 * // Writing to clipboard
 * helper.writeText("Hello World");
 * }</pre>
 *
 * ClipboardHelper with singleton desktop-level result handling pattern.
 * Only one helper of this type is allowed per page.
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API
 */
public class ClipboardHelper {
    protected static final String CLIPBOARD_HELPER_KEY = "browserkit.clipboardhelper";
    protected static String CLIPBOARD_HELPER_JS_PATH = "~./js/ClipboardHelper.js";
    protected Script clipboardHelperScript;
    protected Desktop desktop;
    private ClipboardAuService auService;

    public static ClipboardHelper getInstance() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        return Optional.ofNullable((ClipboardHelper) desktop.getAttribute(CLIPBOARD_HELPER_KEY))
                .orElseGet(ClipboardHelper::new);
    }

    private ClipboardHelper() {
        ensureDesktopScopeSingleton();
        addAuService();
        addHelperJavaScript(CLIPBOARD_HELPER_JS_PATH);
    }

    protected void ensureDesktopScopeSingleton() {
        desktop = Executions.getCurrent().getDesktop();
        desktop.setAttribute(CLIPBOARD_HELPER_KEY, this);
    }

    protected void addHelperJavaScript(String jsPath) {
        clipboardHelperScript = new Script();
        clipboardHelperScript.setSrc(jsPath);
        clipboardHelperScript.setPage(desktop.getFirstPage());
    }

    protected void removeHelperJavaScript() {
        if (clipboardHelperScript != null) {
            clipboardHelperScript.detach();
            clipboardHelperScript = null;
        }
    }

    protected void addAuService() {
        auService = new ClipboardAuService();
        desktop.addListener(auService);
    }

    private void removeAuService() {
        if (auService != null) {
            desktop.removeListener(auService);
            auService = null;
        }
    }

    public void writeText(String text) {
        if (auService == null) return; // disposed
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        // Escape single quotes to prevent JavaScript syntax errors
        String escapedText = text.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
        Clients.evalJavaScript("ClipboardHelper.writeText('" + escapedText + "')");
    }

    public void readText() {
        if (auService == null) return; // disposed
        Clients.evalJavaScript("ClipboardHelper.readText()");
    }

    public void readImage() {
        if (auService == null) return; // disposed
        Clients.evalJavaScript("ClipboardHelper.readImage()");
    }

    /**
     * Dispose this helper, removing the AU service and the helper JavaScript.
     * After calling this method, this instance should not be used anymore.
     */
    public void dispose() {
        desktop.removeAttribute(CLIPBOARD_HELPER_KEY);
        removeAuService();
        removeHelperJavaScript();
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }
}