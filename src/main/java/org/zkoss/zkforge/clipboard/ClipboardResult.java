package org.zkoss.zkforge.clipboard;

import com.google.gson.annotations.Expose;

/**
 * Abstract base class for all clipboard operation results.
 * 
 * <p>Success is indicated when {@code getError()} returns {@code null}.
 * Use {@code isSuccess()} for convenient success checking.</p>
 */
public abstract class ClipboardResult {
    @Expose
    private ClipboardAction action;
    @Expose
    private ClipboardError error;

    public boolean isSuccess() {
        return error == null;
    }

    /**
     * Gets the clipboard error information.
     * 
     * @return the ClipboardError object, or null if the operation succeeded
     */
    public ClipboardError getError() {
        return error;
    }
    

    /**
     * Gets the clipboard action that was performed.
     * 
     * @return the clipboard action (read, write, read_image)
     */
    public ClipboardAction getAction() {
        return action;
    }

    public void setError(ClipboardError error) {
        this.error = error;
    }
}