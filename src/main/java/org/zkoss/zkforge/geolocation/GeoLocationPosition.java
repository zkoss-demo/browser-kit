package org.zkoss.zkforge.geolocation;

public class GeoLocationPosition implements GeolocationPositionResult {
    private GeoLocationCoordinates coords = new GeoLocationCoordinates();
    private long timestamp;

    public GeoLocationCoordinates getCoords() {
        return coords;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public GeoLocationPosition getPosition() {
        return this;
    }

    @Override
    public GeolocationPositionError getError() {
        throw new RuntimeException("not an " + GeolocationPositionError.class);
    }
}
