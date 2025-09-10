window.GeolocationHelper = {
    getCurrentPosition: function() {
        if (!navigator.geolocation) {
            this.fireEvent({
                error: JSON.stringify({
                    code: 0,
                    message: 'Geolocation API not available'
                })
            });
            return;
        }
        /**
         * https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPosition
         * https://developer.mozilla.org/en-US/docs/Web/API/GeolocationCoordinates
         */
        navigator.geolocation.getCurrentPosition(
            (position) => {
                this.fireEvent({
                    position: JSON.stringify(position)
                });
            },
            (error) => {
                this.fireEvent({
                    error: JSON.stringify(error)
                });
            }
        );
    },
    fireEvent: function(data) {
        zAu.send(new zk.Event(null, 'onGetLocation', data));
    },
};
