package org.zkoss.zkforge.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * Static helper class that provides Java access to the browser's Clipboard API.
 * 
 * <p>This class wraps the native browser Clipboard API and provides static methods to read from and write to
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
 * // Reading from clipboard (results delivered via desktop events)
 * ClipboardHelper.readText();
 * 
 * // Writing to clipboard
 * ClipboardHelper.writeText("Hello World");
 * }</pre>
 *
 * <p>Results are delivered asynchronously via {@link ClipboardEvent} posted to the desktop.
 * Components can listen for these events to handle clipboard results.</p>
 * 
 * <p>Based on <a href="https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API">MDN Clipboard API</a></p>
 */
public class ClipboardHelper {
    protected static final String CLIPBOARD_HELPER_KEY = "browserkit.clipboardhelper";
    protected static final String CLIPBOARD_HELPER_JS_PATH = "~./js/ClipboardHelper.js";
    protected static ClipboardAuService auService;

    /**
     * Write text to the system clipboard.
     * 
     * @param text the text to write to the clipboard
     */
    public static void writeText(String text) {
        if (text == null)  return;
        // Escape single quotes to prevent JavaScript syntax errors
        String escapedText = text.replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
        Clients.evalJavaScript("ClipboardHelper.writeText('" + escapedText + "')");
    }

    /**
     * Read text from the system clipboard.
     * Results are delivered asynchronously via {@link ClipboardEvent}.
     * 
     * @throws IllegalStateException if called outside an execution context
     */
    public static void readText() {
        Clients.evalJavaScript("ClipboardHelper.readText()");
    }

    /**
     * Read image from the system clipboard.
     * Results are delivered asynchronously via {@link ClipboardEvent}.
     * 
     * @throws IllegalStateException if called outside an execution context
     */
    public static void readImage() {
        Clients.evalJavaScript("ClipboardHelper.readImage()");
    }

    /**
     * Initialize clipboard helper for the current desktop if not already initialized.
     * This method ensures the AU service and JavaScript are properly set up.
     * You should call this method before you call any action like {@link #writeText(String)} or {@link #readText()}.
     * You should call this method in a Composer's life cycle method e.g. {@link org.zkoss.zk.ui.util.Composer#doAfterCompose(Component)}.
     */
    public static void init() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        // Check if already initialized for this desktop
        if (desktop.getAttribute(CLIPBOARD_HELPER_KEY) != null) {
            return;
        }
        // Mark as initialized
        desktop.setAttribute(CLIPBOARD_HELPER_KEY, true);

        addAuService();
        addHelperScript();
    }

    private static void addAuService() {
        Desktop desktop = Executions.getCurrent().getDesktop();
        if (auService == null) {
            auService = new ClipboardAuService();
        }
        desktop.addListener(auService);
    }

    protected static void addHelperScript() {
        Desktop desktop = Executions.getCurrent().getDesktop();
        Script clipboardHelperScript = new Script();
        clipboardHelperScript.setId(CLIPBOARD_HELPER_KEY);
        clipboardHelperScript.setSrc(CLIPBOARD_HELPER_JS_PATH);
        clipboardHelperScript.setPage(desktop.getFirstPage());
    }

    /**
     * Dispose clipboard helper for the current desktop.
     * Removes the AU service listener and JavaScript helper for this desktop.
     * 
     * @throws IllegalStateException if called outside an execution context
     */
    public static void dispose() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        
        // Mark as not initialized
        desktop.removeAttribute(CLIPBOARD_HELPER_KEY);
        
        if (auService != null) {
            desktop.removeListener(auService);
        }
        
        Selectors.find(desktop.getFirstPage(), "#" + CLIPBOARD_HELPER_KEY)
                .forEach(Component::detach);
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }
}