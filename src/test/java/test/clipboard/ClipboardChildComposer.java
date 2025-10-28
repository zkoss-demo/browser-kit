package test.clipboard;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;

import static test.clipboard.ClipboardParentComposer.CLIPBOARD_QUEUE;

/**
 * the child(inner) page's controller receives a ClipboardEvent.
 */
public class ClipboardChildComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        // Listen for a specific event queue
        EventQueues.lookup(CLIPBOARD_QUEUE).subscribe(event ->{
            if (event instanceof ClipboardEvent ){
                handleReadClipboard((ClipboardEvent)event);
            }
        });
    }

    @Listen("onClick = #read")
    public void read() {
        ClipboardHelper.readText();
    }

    /**
     * ZK EE can use @Subscribe to subscribe an event queue.
     * see <a href="https://docs.zkoss.org/zk_dev_ref/mvc/subscribe_to_eventqueues">subscribe_to_eventqueues</a>
     */
    public void handleReadClipboard(ClipboardEvent event){
        if (!event.isSuccess()) System.err.println("Error: " + event.getResult().getError().getMessage());
        if (event.isTextResult()
                && event.getResult().getAction() == ClipboardAction.READ) {
            pastingTarget.setValue(event.getClipboardText().getText());
        }
    }

}