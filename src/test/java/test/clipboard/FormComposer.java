package test.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;
import test.BrowserFlagSetter;

/**
 */
public class FormComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;
    private boolean requestingClipboard = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init(); // Initialize the ClipboardHelper
        // Listen for clipboard events on this composer's root component
        comp.addEventListener(ClipboardEvent.EVENT_NAME, (ClipboardEvent event)->
        {
            if (!event.isSuccess()) System.err.println("Error: " + event.getResult().getError().getMessage());
            if (event.isTextResult()
                && event.getResult().getAction() == ClipboardAction.READ) {
                pastingTarget.setValue(event.getClipboardText().getText());
            }
        });
    }

    @Listen("onClick = #read")
    public void read() {
        ClipboardHelper.readText();
    }

}