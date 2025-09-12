package org.zkoss.zkforge.clipboard;

import com.google.gson.annotations.Expose;

/**
 * Result object for text-based clipboard operations. Read-only.
 */
public class ClipboardText extends ClipboardResult {
    @Expose
    private String text;

    /**
     * Gets the text content from clipboard operations.
     * 
     * @return the clipboard text content, or null if no text is available
     */
    public String getText() {
        return text;
    }


}