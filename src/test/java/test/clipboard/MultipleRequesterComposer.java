package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zkforge.clipboard.*;

/**
 */
public class MultipleRequesterComposer extends SelectorComposer<Component> {

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init(); // Initialize the ClipboardHelper
    }

}