package org.zkoss.zkforge.clipboard;

import org.zkoss.zk.au.*;
import org.zkoss.zk.ui.event.Events;

public class ClipboardAuService implements AuService {

    @Override
    public boolean service(AuRequest request, boolean everError) {
        final String cmd = request.getCommand();
        if (ClipboardEvent.EVENT_NAME.equals(cmd)) {
            ClipboardEvent event = ClipboardEvent.getEvent(request);
            Events.postEvent(event);
            return true; // Handled - stop further processing
        }

        return false; // Not handled - continue to next handler
    }
}