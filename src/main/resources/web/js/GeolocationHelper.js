
class GeolocationHelper {
    static GET_LOCATION_EVENT = 'onGetLocation';

    /**
     * Requests the current geolocation of the device
     */
    static requestLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                GeolocationHelper.handleSuccess,
                GeolocationHelper.handleError
            );
        } else {
            GeolocationHelper.fireEventToServer({
                error: "Geolocation is not supported by this browser."
            });
        }
    }

    /**
     * Gets the anchor widget for firing events
     * @returns {zk.Widget} The anchor widget
     * @private
     */
    static getAnchorWidget() {
        return zk.Widget.$('$' + GeolocationHelper.name);
    }

    /**
     * Handles successful geolocation retrieval
     * @param {GeolocationPosition} position - The position object
     * @private
     */
    static handleSuccess(position) {
        GeolocationHelper.fireEventToServer(position.toJSON());
    }

    /**
     * Handles geolocation errors
     * @param {GeolocationPositionError} error - The error object
     * @private
     */
    static handleError(error) {
        GeolocationHelper.fireEventToServer(error.toJSON());
    }

    /**
     * Fires an event to the server with the given data
     * @param {Object} data - The data to send to the server
     * @private
     */
    static fireEventToServer(data) {
        const widget = GeolocationHelper.getAnchorWidget();
        if (widget) {
            widget.fire(
                GeolocationHelper.GET_LOCATION_EVENT,
                data,
                { toServer: true }
            );
        }
    }
}