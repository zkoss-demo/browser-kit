// Mock geolocation error scenarios
const GeolocationMock = {
    originalGetCurrentPosition: navigator.geolocation.getCurrentPosition,

    simulatePermissionDenied() {
        navigator.geolocation.getCurrentPosition = (success, error) => {
            error({
                code: 1, // PERMISSION_DENIED
                message: "User denied Geolocation"
            });
        };
    },

    simulatePositionUnavailable() {
        navigator.geolocation.getCurrentPosition = (success, error) => {
            error({
                code: 2, // POSITION_UNAVAILABLE
                message: "Position unavailable"
            });
        };
    },

    simulateTimeout() {
        navigator.geolocation.getCurrentPosition = (success, error) => {
            error({
                code: 3, // TIMEOUT
                message: "Timeout getting geolocation"
            });
        };
    },

    restore() {
        navigator.geolocation.getCurrentPosition = this.originalGetCurrentPosition;
    }
};