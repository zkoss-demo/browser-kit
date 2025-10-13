package test.geolocation;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkforge.geolocation.*;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Store Locator demo showcasing GeolocationHelper API usage.
 * Demonstrates a real-world use case: finding nearby stores based on a user's location.
 */
public class StoreLocatorComposer extends SelectorComposer<Component> {

    @Wire
    private Label statusLabel;

    @Wire
    private Label userLocationLabel;

    @Wire
    private Listbox storeListbox;

    private static List<Store> stores;
    private GeolocationCoordinates userCoords;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Initialize sample stores (fictional coffee shops in San Francisco Bay Area)
        initializeSampleStores();

        // Initialize GeolocationHelper
        GeolocationHelper.init();
        storeListbox.setItemRenderer((Listitem item, Store store, int index) -> {
            item.appendChild(new Listcell(String.valueOf(index + 1)));
            item.appendChild(new Listcell(store.getName()));
            item.appendChild(new Listcell(store.getAddress()));
            item.appendChild(new Listcell(store.getDistanceDisplay()));
        });

        statusLabel.setValue("Click 'Find Nearby Stores' to see stores sorted by distance");
    }

    /**
     * Initialize sample store locations (fictional stores for demo purposes)
     */
    private void initializeSampleStores() {
        stores = new ArrayList<>();

        stores.add(new Store("Bay Coffee Roasters", "123 Market St, San Francisco, CA", 37.7749, -122.4194));
        stores.add(new Store("Golden Gate Cafe", "456 Mission St, San Francisco, CA", 37.7859, -122.4364));
        stores.add(new Store("Marina Coffee House", "789 Chestnut St, San Francisco, CA", 37.8024, -122.4343));
        stores.add(new Store("Berkeley Bean", "321 University Ave, Berkeley, CA", 37.8715, -122.2730));
        stores.add(new Store("Oakland Brews", "555 Broadway, Oakland, CA", 37.8044, -122.2712));
        stores.add(new Store("South Bay Coffee Co", "888 First St, San Jose, CA", 37.3382, -121.8863));
        stores.add(new Store("Stanford Coffee Shop", "123 University Ave, Palo Alto, CA", 37.4419, -122.1430));
    }

    @Listen("onClick = #findStoresBtn")
    public void findNearbyStores() {
        statusLabel.setValue("Getting your location...");
        userLocationLabel.setValue("");
        storeListbox.getItems().clear();
        GeolocationHelper.getCurrentPosition();
    }

    @Listen(GeolocationEvent.EVENT_NAME + " = #root")
    public void handleGeolocation(GeolocationEvent event) {
        if (event.isSuccess()) {
            handleLocationSuccess(event.getGeoLocationPosition());
        } else {
            handleLocationError(event.getGeoLocationPositionError());
        }
    }

    private void handleLocationSuccess(GeolocationPosition position) {
        userCoords = position.getCoords();

        // Display user's location
        userLocationLabel.setValue(String.format(
            "Your location: %.4f°, %.4f° (accuracy: %.0f m)",
            userCoords.getLatitude(),
            userCoords.getLongitude(),
            userCoords.getAccuracy()
        ));

        calculateDistances();

        // Sort stores by distance
        stores.sort(Comparator.comparingDouble(Store::getDistanceKm));

        displayStores();

        statusLabel.setValue("Found " + stores.size() + " stores nearby:");
    }

    /**
     * Calculate the distance from user to each store using Haversine formula
     */
    private void calculateDistances() {
        double userLat = userCoords.getLatitude();
        double userLng = userCoords.getLongitude();

        for (Store store : stores) {
            double distance = calculateHaversineDistance(
                userLat, userLng,
                store.getLatitude(), store.getLongitude()
            );
            store.setDistanceKm(distance);
        }
    }

    /**
     * Calculate the distance between two coordinates using Haversine formula
     * @return distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double EARTH_RADIUS_KM = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Display sorted stores in the listbox
     */
    private void displayStores() {
        ListModelList<Store> model = new ListModelList<>(stores);
        storeListbox.setModel(model);
    }

    private void handleLocationError(GeolocationPositionError error) {
        statusLabel.setValue("Unable to get location: " + error.getMessage());
        userLocationLabel.setValue("");
        storeListbox.getItems().clear();

        Clients.showNotification(
            "Geolocation Error: " + error.getMessage() +
            " (Code: " + error.getCode() + ")",
            "error",
            null,
            "top_center",
            5000
        );
    }

    @Listen("onClick = #disposeBtn")
    public void dispose() {
        GeolocationHelper.dispose();
        statusLabel.setValue("GeolocationHelper disposed");
    }
}
