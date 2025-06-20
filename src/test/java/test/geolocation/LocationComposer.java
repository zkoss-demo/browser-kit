package test.geolocation;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkforge.geolocation.GeoLocationPosition;
import org.zkoss.zkforge.geolocation.GeolocationHelper;
import org.zkoss.zkforge.geolocation.GeolocationPositionResult;
import org.zkoss.zul.Label;

public class LocationComposer extends SelectorComposer {

    private GeolocationHelper geoLocationHelper;
    @Wire
    private Label locationLabel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        geoLocationHelper = new GeolocationHelper(this::processLocation);
        geoLocationHelper.requestLocation();
    }

    public void processLocation(GeolocationPositionResult result){
        if (result instanceof GeoLocationPosition) {
            locationLabel.setValue(result.getPosition().getCoords().getLatitude()+","
                    +result.getPosition().getCoords().getLongitude());
        }else{
            Clients.log("error: " + result.getError().getMessage());
        }
    }

    @Listen("onClick = #getLocation")
    public void getLocation(){
        geoLocationHelper.requestLocation();
    }
}
