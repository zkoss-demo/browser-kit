package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.Textbox;
import zkforge.clipboard.*;

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
        pastingTarget.setValue(result.getText());
    }

    @Listen("onClick = #read ")
    public void read(){
        clipboardHelper.readText();
    }
}
