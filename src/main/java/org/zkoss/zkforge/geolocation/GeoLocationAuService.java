package org.zkoss.zkforge.geolocation;

import org.zkoss.zk.au.*;
import org.zkoss.zk.ui.event.Events;

public class GeoLocationAuService implements AuService {

    @Override
    public boolean service(AuRequest request, boolean everError) {
        final String cmd = request.getCommand();
        if (GeolocationEvent.EVENT_NAME.equals(cmd)) {
            GeolocationEvent event = GeolocationEvent.getEvent(request);
            Events.postEvent(event);
            return true; // Handled - stop further processing
        }

        return false; // Not handled - continue to next handler
    }
}