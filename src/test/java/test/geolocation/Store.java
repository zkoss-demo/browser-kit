package test.geolocation;

/**
 * Represents a store location with geographic coordinates.
 * Used for demonstrating GeolocationHelper API in store locator scenarios.
 */
public class Store {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double distanceKm; // calculated distance from user

    public Store(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    /**
     * Get formatted distance string for display
     */
    public String getDistanceDisplay() {
        if (distanceKm < 1) {
            return String.format("%.0f m", distanceKm * 1000);
        } else {
            return String.format("%.2f km", distanceKm);
        }
    }

    @Override
    public String toString() {
        return name + " - " + address;
    }
}
