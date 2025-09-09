window.GeolocationHelper = {
    getCurrentPosition: function() {
        if (!navigator.geolocation) {
            zAu.send(new zk.Event(zk.Desktop._dt, 'onGetLocation', {
                error: {
                    code: 0,
                    message: 'Geolocation API not available'
                }
            }));
            return;
        }
        /**
         * https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPosition
         * https://developer.mozilla.org/en-US/docs/Web/API/GeolocationCoordinates
         */
        navigator.geolocation.getCurrentPosition(
            function(position) {
                // Send success result with position data
                zAu.send(new zk.Event(zk.Desktop._dt, 'onGetLocation', {
                    position: JSON.stringify(position)
                }));
            },
            function(error) {
                zAu.send(new zk.Event(zk.Desktop._dt, 'onGetLocation', {
                    error: JSON.stringify(error)
                }));
            }
        );
    }
};

