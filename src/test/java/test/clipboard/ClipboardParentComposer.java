package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zkforge.clipboard.*;

public class ClipboardParentComposer extends SelectorComposer<Component> {
    public static final String CLIPBOARD_QUEUE = "clipboard_queue";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init(); // Initialize the ClipboardHelper
        comp.addEventListener(ClipboardEvent.EVENT_NAME, (ClipboardEvent event)->
            EventQueues.lookup(CLIPBOARD_QUEUE).publish(event)
        );
    }
}
