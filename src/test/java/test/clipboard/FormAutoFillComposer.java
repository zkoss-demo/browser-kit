package test.clipboard;

import org.zkoss.lang.Threads;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkforge.clipboard.*;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import java.util.concurrent.CompletableFuture;

/**
 * Composer for Use Case: Form Auto-Fill from Clipboard
 * Demonstrates smart parsing and auto-filling of form fields based on clipboard content.
 */
public class FormAutoFillComposer extends SelectorComposer<Component> {
    
    @Wire
    private Label statusLabel;
    
    @Wire
    private Textbox emailTextbox;
    
    @Wire
    private Textbox phoneTextbox;
    
    @Wire
    private Textbox noteTextbox;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        ClipboardHelper.init();
        // Listen for clipboard events on this composer's root component
        comp.addEventListener(ClipboardEvent.EVENT_NAME, this::handleClipboardEvent);

        comp.getDesktop().enableServerPush(true);
        showStatus("Ready - Copy some text and click 'Paste from Clipboard'", "info");
    }

    //onClipboardEvent listener
    public void handleClipboardEvent(org.zkoss.zk.ui.event.Event event) {
        ClipboardEvent clipboardEvent = (ClipboardEvent) event;
        if (!clipboardEvent.isSuccess()){
            showStatus("Could not read clipboard: " + ((ClipboardEvent) event).getResult().getError().getMessage(), "error");
            return;
        }
        if (clipboardEvent.getResult().getAction() != ClipboardAction.READ) return; // Ignore other actions
        String clipboardText = clipboardEvent.getClipboardText().getText();
        processClipboardContent(clipboardText);
    }
    
    @Listen("onClick = #pasteButton")
    public void pasteFromClipboard() {
        showStatus("Reading from clipboard...", "info");
        ClipboardHelper.readText();
    }
    
    /**
     * Smart parsing of clipboard content to auto-fill appropriate fields
     */
    private void processClipboardContent(String clipboardText) {
        if (clipboardText == null || clipboardText.trim().isEmpty()) {
            showStatus("Clipboard is empty", "warning");
            return;
        }
        
        String content = clipboardText.trim();
        
        // Email detection: contains @ and at least one dot
        if (content.contains("@") && content.contains(".") && isValidEmail(content)) {
            emailTextbox.setValue(content);
            emailTextbox.focus();
            showStatus("Email pasted successfully", "success");
            
        // Phone number detection: mostly digits with optional formatting
        } else if (content.matches("[\\d\\s\\-\\(\\)\\+\\.]{7,}") && hasMinimumDigits(content, 7)) {
            // Clean up phone number formatting
            String cleanPhone = content.replaceAll("[^\\d\\+]", "");
            if (cleanPhone.length() >= 7) {
                phoneTextbox.setValue(content); // Keep original formatting
                phoneTextbox.focus();
                showStatus("Phone number pasted", "success");
            } else {
                noteTextbox.setValue(content);
                noteTextbox.focus();
                showStatus("Text pasted to notes (not enough digits for phone)", "info");
            }
            
        // Default: put in notes field
        } else {
            noteTextbox.setValue(content);
            noteTextbox.focus();
            showStatus("Text pasted to notes", "success");
        }
    }
    
    /**
     * Basic email validation
     */
    private boolean isValidEmail(String email) {
        // Simple email validation - contains @ with text before and after, and at least one dot in domain
        String[] parts = email.split("@");
        if (parts.length != 2) return false;
        
        String localPart = parts[0];
        String domainPart = parts[1];
        
        return !localPart.isEmpty() && !domainPart.isEmpty() && 
               domainPart.contains(".") && !domainPart.startsWith(".") && !domainPart.endsWith(".");
    }
    
    /**
     * Check if string has minimum number of digits
     */
    private boolean hasMinimumDigits(String text, int minDigits) {
        return text.replaceAll("[^\\d]", "").length() >= minDigits;
    }
    
    /**
     * Show the status message with appropriate styling
     */
    private void showStatus(String message, String type) {
        statusLabel.setValue(message);
        
        switch (type) {
            case "success":
                statusLabel.setStyle("color: #4CAF50; font-style: italic;");
                break;
            case "error":
                statusLabel.setStyle("color: #f44336; font-style: italic;");
                break;
            case "warning":
                statusLabel.setStyle("color: #ff9800; font-style: italic;");
                break;
            case "info":
            default:
                statusLabel.setStyle("color: #666; font-style: italic;");
                break;
        }
        CompletableFuture.runAsync(() -> {
            Threads.sleep(3000);
            Executions.schedule(getPage().getDesktop(),
                    new EventListener<Event>() {
                        public void onEvent(Event event) {
                            if ("onClearStatus".equals(event.getName()))  statusLabel.setValue("");
                        }
                    }, new Event("onClearStatus"));
        });
    }
}