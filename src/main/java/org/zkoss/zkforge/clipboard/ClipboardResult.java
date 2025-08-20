package org.zkoss.zkforge.clipboard;

/**
 * Result object for clipboard operations containing either success data or error information.
 * 
 * <p>Success is indicated when {@code getError()} returns {@code null}.
 * Use {@code isSuccess()} for convenient success checking.</p>
 */
public class ClipboardResult {
    private ClipboardAction action;
    private String text;
    private String error;

    /**
     * Checks if the clipboard operation was successful.
     * 
     * @return true if the operation succeeded (no error), false otherwise
     */
    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Checks if the clipboard operation failed.
     * 
     * @return true if the operation failed (has error), false otherwise
     */
    public boolean isError() {
        return error != null;
    }

    /**
     * Gets the clipboard action that was performed.
     * 
     * @return the clipboard action (READ or write)
     */
    public ClipboardAction getAction() {
        return action;
    }

    /**
     * Gets the text content from clipboard read operations.
     * 
     * @return the clipboard text content, or null for write operations or errors
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the error message if the operation failed.
     * 
     * @return the error message, or null if the operation succeeded
     */
    public String getError() {
        return error;
    }
}
