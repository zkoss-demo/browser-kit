package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Demonstrates how to use the ClipboardHelper with AuService architecture.
 */
public class ClipboardComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;
    private ClipboardHelper clipboardHelper;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        clipboardHelper = ClipboardHelper.getInstance();
        
        // Listen for clipboard events on this composer's root component
        comp.addEventListener(ClipboardEvent.EVENT_NAME, (EventListener<Event>) this::handleClipboardEvent);
    }

    public void handleClipboardEvent(Event event) {
        ClipboardEvent clipboardEvent = (ClipboardEvent) event;
        ClipboardResult result = clipboardEvent.getClipboardResult();
        if (result.isSuccess()) {
            if (result.getAction() == ClipboardAction.READ) {
                pastingTarget.setValue(result.getText());
            }
        } else {
            System.err.println("Error: " + result.getError());
        }
    }

    @Listen("onClick = #read")
    public void read() {
        clipboardHelper.readText();
    }

    @Listen("onClick = #write")
    public void write() {
        clipboardHelper.writeText("content from server");
    }
}