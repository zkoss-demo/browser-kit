package org.zkoss.zkforge.geolocation;

import java.util.Map;

/**
 * https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPositionError
 */
public class GeolocationPositionError implements GeolocationPositionResult {
    protected final int code; // Error code
    protected final String message; // Human-readable error message

    protected static Map<Integer, String> codeDescriptions = Map.of(
            1, "The acquisition of the geolocation information failed because the page didn't have the necessary permissions, for example because it is blocked by a Permissions Policy.",
            2, "The acquisition of the geolocation failed because at least one internal source of position returned an internal error.",
            3, "The time allowed to acquire the geolocation was reached before the information was obtained."
    );
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

    public String getDescription() {
        return codeDescriptions.get(code);
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
