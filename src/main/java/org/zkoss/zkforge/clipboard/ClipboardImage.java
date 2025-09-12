package org.zkoss.zkforge.clipboard;

import com.google.gson.annotations.Expose;

/**
 * Result object for image-based clipboard operations. Read-only.
 * 
 * <p>Contains image-specific properties such as MIME type, image dimensions, and binary image data.</p>
 */
public class ClipboardImage extends ClipboardResult {
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
     * Gets the image height in pixels.
     * 
     * @return the image height, or 0 if not available
     */
    public int getHeight() {
        return height;
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
     * Checks if the image is of a supported format based on MIME type.
     * 
     * @return true if the image format is supported, false otherwise
     */
    public boolean isSupportedFormat() {
        return isSupportedMimeType(mimeType);
    }
    
    /**
     * Sets the MIME type of the image.
     * 
     * @param mimeType the image MIME type
     */
    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    /**
     * Sets the image dimensions.
     * 
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    private boolean isSupportedMimeType(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals("image/png") || 
               mimeType.equals("image/jpeg") || 
               mimeType.equals("image/jpg") ||
               mimeType.equals("image/gif") ||
               mimeType.equals("image/webp");
    }
}