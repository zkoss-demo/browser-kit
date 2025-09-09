package org.zkoss.zkforge.geolocation;

/**
 * a java representation of GeolocationPosition, https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPosition
 */
public class GeoLocationPosition implements GeolocationPositionResult {

    protected long timestamp;
    protected GeoLocationCoordinates coords;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public GeoLocationCoordinates getCoords() {
        return coords;
    }

    public void setCoords(GeoLocationCoordinates coords) {
        this.coords = coords;
    }

    @Override
    public GeoLocationPosition getPosition() {
        return this;
    }

    @Override
    public GeolocationPositionError getError() {
        throw new RuntimeException("not an " + GeolocationPositionError.class);
    }

    @Override
    public String toString() {
        return "GeoLocationPosition{" +
                "timestamp=" + timestamp +
                ", coords=" + coords +
                '}';
    }
}
