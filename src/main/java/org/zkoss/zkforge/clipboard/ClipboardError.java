package org.zkoss.zkforge.clipboard;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Represents a clipboard operation error with structured error information.
 * Based on W3C Clipboard API DOMException patterns and browser implementations.
 * 
 * @see <a href="https://www.w3.org/TR/clipboard-apis/">W3C Clipboard APIs</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API">MDN Clipboard API</a>
 */
public class ClipboardError {
    public static final int SERVER_ERROR = 0;         // server error

    @Expose
    protected final int code;
    @Expose
    protected final String message;


    public ClipboardError(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    


}