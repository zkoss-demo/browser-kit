class ClipboardHelper {
    static CLIPBOARD_ACTION_EVENT = 'onClipboardAction';

    /**
     * Writes text to the clipboard
     * @param {string} text - The text to write to the clipboard
     */
    static writeText(text) {
        if (navigator.clipboard) {
            navigator.clipboard.writeText(text)
                .then(() => {
                    this.fireEventToServer({ action: 'WRITE' });
                })
                .catch(error => {
                    this.fireEventToServer({
                        action: 'WRITE',
                        error: 'Failed to write to clipboard: ' + error.message
                    });
                });
        } else {
            this.fireEventToServer({
                action: 'WRITE',
                error: 'Clipboard API not supported by this browser.'
            });
        }
    }

    /**
     * Reads text from the clipboard
     */
    static readText() {
        if (navigator.clipboard) {
            navigator.clipboard.readText()
                .then(text => {
                    this.fireEventToServer({
                        action: 'READ',
                        text: text
                    });
                })
                .catch(error => {
                    this.fireEventToServer({
                        action: 'READ',
                        error: error.message
                    });
                });
        } else {
            this.fireEventToServer({
                action: 'READ',
                error: 'Clipboard API not supported by this browser.'
            });
        }
    }

    /**
     * Reads image data from the clipboard
     * Uses the modern clipboard.read() API to access ClipboardItem objects
     */
    static readImage() {
        // Check if the modern clipboard API is supported
        if (!navigator.clipboard || !navigator.clipboard.read) {
            this.fireEventToServer({
                action: 'READ_IMAGE',
                error: 'Clipboard read() API not supported by this browser. Requires Chrome 88+, Firefox 127+, or Edge 88+.'
            });
            return;
        }

        navigator.clipboard.read()
            .then(clipboardItems => {
                // Look for image data in clipboard items
                for (const item of clipboardItems) {
                    // Check each type in the clipboard item
                    for (const type of item.types) {
                        if (type.startsWith('image/')) {
                            // Found an image, process it
                            return item.getType(type).then(blob => {
                                return this.processImageBlob(blob, type);
                            });
                        }
                    }
                }
                
                // No image found
                this.fireEventToServer({
                    action: 'READ_IMAGE',
                    error: 'No image data found in clipboard'
                });
            })
            .catch(error => {
                // Handle various error scenarios
                let errorMessage = error.message;
                
                if (error.name === 'NotAllowedError') {
                    errorMessage = 'Clipboard access denied. Please ensure the page has focus and try again.';
                } else if (error.name === 'NotFoundError') {
                    errorMessage = 'No data found in clipboard.';
                } else if (error.name === 'DataError') {
                    errorMessage = 'Clipboard data is corrupted or in an unsupported format.';
                }
                
                this.fireEventToServer({
                    action: 'READ_IMAGE',
                    error: errorMessage
                });
            });
    }

    /**
     * Processes an image blob and converts it to base64 for transfer to server
     * @param {Blob} blob - The image blob from clipboard
     * @param {string} mimeType - The MIME type of the image
     */
    static processImageBlob(blob, mimeType) {
        this.blobToBase64(blob)
            .then(base64Data => {
                // Create image to get dimensions
                const img = new Image();
                img.onload = () => {
                    this.fireEventToServer({
                        action: 'READ_IMAGE',
                        mimeType: mimeType,
                        imageData: base64Data,
                        size: blob.size,
                        width: img.width,
                        height: img.height
                    });
                };
                
                img.onerror = () => {
                    // If we can't load the image for dimensions, send without them
                    this.fireEventToServer({
                        action: 'READ_IMAGE',
                        mimeType: mimeType,
                        imageData: base64Data,
                        size: blob.size,
                        width: 0,
                        height: 0
                    });
                };
                
                // Create object URL and load image
                const objectUrl = URL.createObjectURL(blob);
                img.src = objectUrl;
                
                // Clean up object URL after a short delay
                setTimeout(() => {
                    URL.revokeObjectURL(objectUrl);
                }, 1000);
            })
            .catch(error => {
                this.fireEventToServer({
                    action: 'READ_IMAGE',
                    error: 'Failed to process image data: ' + error.message
                });
            });
    }

    /**
     * Converts a blob to base64 string
     * @param {Blob} blob - The blob to convert
     * @returns {Promise<string>} Promise that resolves to base64 string (without data: prefix)
     * @private
     */
    static blobToBase64(blob) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onload = () => {
                // Remove the data:image/xxx;base64, prefix
                const base64 = reader.result.split(',')[1];
                resolve(base64);
            };
            reader.onerror = reject;
            reader.readAsDataURL(blob);
        });
    }

    /**
     * Gets the anchor widget for firing events
     * @returns {zk.Widget} The anchor widget
     * @private
     */
    static getAnchorWidget() {
        return zk.Widget.$('.z-'+ClipboardHelper.name.toLowerCase());
    }

    /**
     * Fires an event to the server with the given data
     * @param {Object} data - The data to send to the server
     * @private
     */
    static fireEventToServer(data) {
        this.getAnchorWidget().fire(ClipboardHelper.CLIPBOARD_ACTION_EVENT,
                                    data,
                                    { toServer: true });
    }
}