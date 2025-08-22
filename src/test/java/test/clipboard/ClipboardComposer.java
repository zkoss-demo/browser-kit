package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.ClipboardHelper;
import org.zkoss.zkforge.clipboard.ClipboardResult;
import org.zkoss.zul.Textbox;

/**
 * demonstrates how to use the ClipboardHelper.
 */
public class ClipboardComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;
    private ClipboardHelper clipboardHelper;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        clipboardHelper = new ClipboardHelper(this::processData);
    }

    public void processData(ClipboardResult result){
        if (result.isSuccess()){
            if (result.getAction() == ClipboardHelper.ClipboardAction.READ) {
                pastingTarget.setValue(result.getText());
            }
        } else {
            System.err.println("Error: " + result.getError());
        }
    }

    @Listen("onClick = #read ")
    public void read(){
        clipboardHelper.readText();
    }

    @Listen("onClick = #write ")
    public void write(){
        clipboardHelper.writeText("content from server");
    }
}
