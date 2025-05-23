package zkforge.geolocation;

/**
 * just a marker interface to generalize the result of geolocation api
 */
public interface GeolocationPositionResult {
    GeoLocationHelper.GeoLocationPosition getPosition();
    GeoLocationHelper.GeolocationPositionError getError();
}
