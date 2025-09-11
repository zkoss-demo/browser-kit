package org.zkoss.zkforge.geolocation;

/**
 * a java representation of GeolocationPosition, https://developer.mozilla.org/en-US/docs/Web/API/GeolocationPosition
 */
public class GeolocationPosition {

    protected long timestamp;
    protected GeolocationCoordinates coords;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public GeolocationCoordinates getCoords() {
        return coords;
    }

    public void setCoords(GeolocationCoordinates coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        return "GeoLocationPosition{" +
                "timestamp=" + timestamp +
                ", coords=" + coords +
                '}';
    }
}
