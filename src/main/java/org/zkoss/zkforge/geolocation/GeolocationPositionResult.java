package org.zkoss.zkforge.geolocation;

/**
 * just a marker interface to generalize the result of geolocation api
 */
public interface GeolocationPositionResult {
    GeoLocationPosition getPosition();
    GeolocationPositionError getError();
}
