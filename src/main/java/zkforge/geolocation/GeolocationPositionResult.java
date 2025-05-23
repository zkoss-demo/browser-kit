package zkforge.geolocation;

/**
 * just a marker interface to generalize the result of geolocation api
 */
public interface GeolocationPositionResult {
    GeolocationHelper.GeoLocationPosition getPosition();
    GeolocationHelper.GeolocationPositionError getError();
}
