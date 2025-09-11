package org.zkoss.zkforge.geolocation;

import com.google.gson.Gson;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.event.Event;

import java.util.Map;

public class GeolocationEvent extends Event {
    public static final String EVENT_NAME = "onGetLocation";
    protected GeolocationPosition geoLocationPosition;
    protected GeolocationPositionError geoLocationPositionError;
    protected static final Gson GSON = new Gson();

    public GeolocationEvent(GeolocationPosition geolocationPosition) {
        // null target indicates the event is sent to all root components
        super(EVENT_NAME, null);
        this.geoLocationPosition = geolocationPosition;
    }

    public GeolocationEvent(GeolocationPositionError geolocationPositionError) {
        super(EVENT_NAME, null);
        this.geoLocationPositionError = geolocationPositionError;
    }

    public static GeolocationEvent getEvent(AuRequest request) {
        GeolocationEvent event = null;
        Map<String, Object> data = request.getData();
        if (isSuccess(data)){
            event = new GeolocationEvent(parsePosition(data));
        }else{
            event = new GeolocationEvent(parseError(data));
        }
        return event;
    }

    protected static boolean isSuccess(Map data) {
        return data.get("position")!=null;
    }
    protected static GeolocationPosition parsePosition(Map data) {
        return GSON.fromJson(data.get("position").toString(), GeolocationPosition.class);
    }

    protected static GeolocationPositionError parseError(Map data) {
        return GSON.fromJson(data.get("error").toString(), GeolocationPositionError.class);
    }

    public boolean isSuccess() {
        return geoLocationPosition != null;
    }

    public GeolocationPosition getGeoLocationPosition() {
        return geoLocationPosition;
    }

    public GeolocationPositionError getGeoLocationPositionError() {
        return geoLocationPositionError;
    }
}
