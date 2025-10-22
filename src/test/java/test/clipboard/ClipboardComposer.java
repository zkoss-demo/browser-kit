package test.clipboard;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Textbox;
import org.zkoss.zk.ui.event.Event;
import test.BrowserFlagSetter;

import javax.servlet.http.HttpServletRequest;

/**
 * Demonstrates how to use the ClipboardHelper with AuService architecture.
 */
public class ClipboardComposer extends SelectorComposer<Component> {
    @Wire
    private Textbox pastingTarget;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
        BrowserFlagSetter.storeBrowserAttribute();
        return super.doBeforeCompose(page, parent, compInfo);
    }

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

    @Listen("onClick = #write")
    public void write() {
        ClipboardHelper.writeText("content from server");
    }
}