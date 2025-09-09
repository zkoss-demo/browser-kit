package org.zkoss.zkforge.geolocation;

/**
 * a java representation of GeolocationCoordinates, https://developer.mozilla.org/en-US/docs/Web/API/GeolocationCoordinates
 * make all fields protected to allow extension for js api evolution.
 */
public class GeoLocationCoordinates {
    protected double latitude;
    protected double longitude;
    protected double altitude;
    protected double accuracy;
    protected double altitudeAccuracy;
    protected double heading;
    protected double speed;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public double getHeading() {
        return heading;
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public String toString() {
        return "GeoLocationCoordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", altitudeAccuracy=" + altitudeAccuracy +
                ", heading=" + heading +
                ", speed=" + speed +
                '}';
    }
}
