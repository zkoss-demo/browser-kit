package test.geolocation;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkforge.geolocation.GeoLocationPosition;
import org.zkoss.zkforge.geolocation.GeolocationHelper;
import org.zkoss.zkforge.geolocation.GeolocationPositionError;
import org.zkoss.zul.Label;

public class LocationComposer extends SelectorComposer {

    private GeolocationHelper geoLocationHelper;
    @Wire
    private Label locationLabel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        // Use singleton pattern with separate callbacks for success and error
        geoLocationHelper = new GeolocationHelper(
            this::handleLocationSuccess,
            this::handleLocationError
        );
        geoLocationHelper.getCurrentPosition();
    }

    private void handleLocationSuccess(GeoLocationPosition position) {
        locationLabel.setValue(position.toString());
    }
    
    private void handleLocationError(GeolocationPositionError error) {
        Clients.log("Geolocation error: " + error.getMessage());
        locationLabel.setValue("Location unavailable: " + error.getMessage());
    }

    @Listen("onClick = #getLocation")
    public void getLocation(){
        geoLocationHelper.getCurrentPosition();
    }
    @Listen("onClick = #dispose")
    public void dispose(){
        geoLocationHelper.dispose();
    }
}
