class ClipboardHelper {
    static CLIPBOARD_DATA_EVENT = 'onClipboardData';

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
        this.getAnchorWidget().fire(ClipboardHelper.CLIPBOARD_DATA_EVENT,
                                    data,
                                    { toServer: true });
    }
}