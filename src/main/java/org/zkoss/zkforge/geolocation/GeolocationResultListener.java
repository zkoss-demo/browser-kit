package org.zkoss.zkforge.geolocation;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.event.*;

import java.util.function.Consumer;

public class GeolocationResultListener implements EventListener<Event> {
    private static final Logger log = LoggerFactory.getLogger(GeolocationResultListener.class);

    protected static final Gson GSON = new Gson();
    protected Consumer<GeoLocationPosition> positionCallback;
    protected Consumer<GeolocationPositionError> errorCallback;
    
    @Override
    public void onEvent(Event event) {
        JSONObject data = (JSONObject)event.getData();
        if (isSuccess(data)){
            GeoLocationPosition geoLocationPosition = parsePosition(data);
            positionCallback.accept(geoLocationPosition);

        }else{
            GeolocationPositionError geolocationPositionError = parseError(data);
            errorCallback.accept(geolocationPositionError);
        }
    }

    protected boolean isSuccess(JSONObject data) {
        return data.get("position")!=null;
    }

    protected static GeoLocationPosition parsePosition(JSONObject data) {
        return GSON.fromJson(data.get("position").toString(), GeoLocationPosition.class);
    }

    protected static GeolocationPositionError parseError(JSONObject data) {
        return GSON.fromJson(data.get("error").toString(), GeolocationPositionError.class);
    }
    public void setCallbacks(Consumer<GeoLocationPosition> positionCallback,
                           Consumer<GeolocationPositionError> errorCallback) {
        this.positionCallback = positionCallback;
        this.errorCallback = errorCallback;
    }
    
    public void clearCallbacks() {
        this.positionCallback = null;
        this.errorCallback = null;
    }
}