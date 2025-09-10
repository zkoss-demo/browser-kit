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
    protected Consumer<GeolocationPosition> positionCallback;
    protected Consumer<GeolocationPositionError> errorCallback;
    protected Event handledEvent;

    /**
     * This event doesn't have any target component, as it is desktop-level. So ZK will invoke it multiple times if there are multiple root components.
     * @see org.zkoss.zk.ui.impl.UiEngineImpl UiEngineImpl#process()
     */
    @Override
    public void onEvent(Event event) {
        // follow HistoryPopStateEvent to avoid handling the same event for multiple times see org.zkoss.bind.BindComposer
        if (event == handledEvent){
            return;
        }
        handledEvent = event;
        JSONObject data = (JSONObject)event.getData();
        if (isSuccess(data)){
            GeolocationPosition geoLocationPosition = parsePosition(data);
            positionCallback.accept(geoLocationPosition);

        }else{
            GeolocationPositionError geolocationPositionError = parseError(data);
            errorCallback.accept(geolocationPositionError);
        }
    }

    protected boolean isSuccess(JSONObject data) {
        return data.get("position")!=null;
    }

    protected static GeolocationPosition parsePosition(JSONObject data) {
        return GSON.fromJson(data.get("position").toString(), GeolocationPosition.class);
    }

    protected static GeolocationPositionError parseError(JSONObject data) {
        return GSON.fromJson(data.get("error").toString(), GeolocationPositionError.class);
    }

    public void setCallbacks(Consumer<GeolocationPosition> positionCallback,
                           Consumer<GeolocationPositionError> errorCallback) {
        this.positionCallback = positionCallback;
        this.errorCallback = errorCallback;
    }
}