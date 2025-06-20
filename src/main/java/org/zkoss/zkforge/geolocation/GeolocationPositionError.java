package org.zkoss.zkforge.geolocation;

public class GeolocationPositionError implements GeolocationPositionResult {
    private final int code; // Error code
    private final String message; // Human-readable error message

    // Constructor
    public GeolocationPositionError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter for code
    public int getCode() {
        return code;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    @Override
    public GeoLocationPosition getPosition() {
        throw new RuntimeException("not an " + GeoLocationPosition.class);
    }

    @Override
    public GeolocationPositionError getError() {
        return this;
    }
}
