package test.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;
import test.BrowserFlagSetter;

/**
 * Form composer demonstrating targeted clipboard event delivery.
 *
 * This example uses the ClipboardHelper.readTextTo(Component) API to request
 * clipboard data with targeting to this specific component. The clipboard event
 * is delivered only to this form, not broadcast to all components on the page.
 *
 * This allows multiple forms to request clipboard data simultaneously without
 * interference or need for external filtering logic.
 */
public class FormComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        // Listen for clipboard events targeted to this component
        // With readTextTo(), events are automatically targeted, so filtering is minimal
        pastingTarget.addEventListener(ClipboardEvent.EVENT_NAME, (ClipboardEvent event) -> {
            if (!event.isSuccess()) {
                System.err.println("Error: " + event.getResult().getError().getMessage());
            }
            // Since the event is targeted to this component only, we receive it only if WE requested it
            if (event.isTextResult() && event.getResult().getAction() == ClipboardAction.READ) {
                pastingTarget.setValue(event.getClipboardText().getText());
            }
        });
    }

    @Listen("onClick = #read")
    public void read() {
        // Use readTextTo() to deliver clipboard result only to THIS component
        // This eliminates event pollution and avoids cross-form interference
        ClipboardHelper.readTextTo(pastingTarget);
    }

}