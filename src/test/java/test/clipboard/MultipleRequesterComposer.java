package test.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;
import test.BrowserFlagSetter;

/**
 */
public class MultipleRequesterComposer extends SelectorComposer<Component> {
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