package test.geolocation;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkforge.geolocation.*;
import org.zkoss.zul.Label;

public class LocationComposer extends SelectorComposer {

    @Wire
    private Label locationLabel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        GeolocationHelper.init();
        GeolocationHelper.getCurrentPosition();
    }

    private void handleLocationSuccess(GeolocationPosition position) {
        locationLabel.setValue(position.toString());
    }
    
    private void handleLocationError(GeolocationPositionError error) {
        Clients.log("Geolocation error: " + error.getMessage());
        locationLabel.setValue("Location unavailable: " + error.getMessage());
    }

    @Listen(GeolocationEvent.EVENT_NAME + "= #root")
    public void handle(GeolocationEvent event){
        if (event.isSuccess()){
            handleLocationSuccess(event.getGeoLocationPosition());
        }else{
            handleLocationError(event.getGeoLocationPositionError());
        }
    }

    @Listen("onClick = #getLocation")
    public void getLocation(){
        GeolocationHelper.getCurrentPosition();
    }
    @Listen("onClick = #dispose")
    public void dispose(){
        GeolocationHelper.dispose();
    }
}
