package org.zkoss.zkforge.clipboard;

import com.google.gson.annotations.Expose;

/**
 * Result object for clipboard image operations containing either success data or error information.
 * 
 * <p>This class extends ClipboardResult to provide additional image-specific properties
 * such as MIME type, image dimensions, and binary image data.</p>
 * 
 * <p>Success is indicated when {@code getError()} returns {@code null}.
 * Use {@code isSuccess()} for convenient success checking.</p>
 */
public class ClipboardImageResult extends ClipboardResult {
    @Expose
    private String mimeType;
    
    @Expose(deserialize = false)
    private byte[] imageData;
    
    @Expose
    private int width;
    @Expose
    private int height;
    @Expose
    private long size;

    /**
     * Gets the MIME type of the clipboard image.
     * 
     * @return the image MIME type (e.g., "image/png", "image/jpeg"), or null if no image data
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type of the clipboard image.
     * 
     * @param mimeType the image MIME type
     */
    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Gets the binary image data from the clipboard.
     * 
     * @return the image data as a byte array, or null if no image data or error occurred
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * Sets the binary image data.
     * 
     * @param imageData the image data as a byte array
     */
    void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    /**
     * Checks if this result contains valid image data.
     * 
     * @return true if image data is available and the operation succeeded, false otherwise
     */
    public boolean hasImageData() {
        return isSuccess() && imageData != null && imageData.length > 0;
    }

    /**
     * Gets the image width in pixels.
     * 
     * @return the image width, or 0 if not available
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the image width in pixels.
     * 
     * @param width the image width
     */
    void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the image height in pixels.
     * 
     * @return the image height, or 0 if not available
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the image height in pixels.
     * 
     * @param height the image height
     */
    void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the size of the image data in bytes.
     * 
     * @return the image size in bytes, or 0 if not available
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the size of the image data in bytes.
     * 
     * @param size the image size in bytes
     */
    void setSize(long size) {
        this.size = size;
    }

    /**
     * Sets the error message for this result.
     * 
     * @param error the error message
     */
    void setError(String error) {
        super.setError(error);
    }

    /**
     * Checks if the image is of a supported format based on MIME type.
     * 
     * @return true if the image format is supported, false otherwise
     */
    public boolean isSupportedFormat() {
        if (mimeType == null) return false;
        return mimeType.equals("image/png") || 
               mimeType.equals("image/jpeg") || 
               mimeType.equals("image/jpg") ||
               mimeType.equals("image/gif") ||
               mimeType.equals("image/webp");
    }
}