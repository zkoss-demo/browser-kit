package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkforge.clipboard.ClipboardEvent;
import org.zkoss.zkforge.clipboard.ClipboardHelper;
import org.zkoss.zkforge.clipboard.ClipboardResult;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

/**
 * Composer for Use Case 2: Form Auto-Fill from Clipboard
 * Demonstrates smart parsing and auto-filling of form fields based on clipboard content.
 */
public class FormAutoFillComposer extends SelectorComposer<Component> {
    
    @Wire
    private Button pasteButton;
    
    @Wire
    private Label statusLabel;
    
    @Wire
    private Textbox emailTextbox;
    
    @Wire
    private Textbox phoneTextbox;
    
    @Wire
    private Textbox noteTextbox;
    
    private ClipboardHelper clipboardHelper;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        // Get clipboard helper instance
        clipboardHelper = ClipboardHelper.getInstance();
        
        // Listen for clipboard events on this composer's root component
        comp.addEventListener(ClipboardEvent.EVENT_NAME, this::handleClipboardEvent);
        
        showStatus("Ready - Copy some text and click 'Paste from Clipboard'", "info");
    }
    
    public void handleClipboardEvent(org.zkoss.zk.ui.event.Event event) {
        if (event instanceof ClipboardEvent) {
            ClipboardEvent clipboardEvent = (ClipboardEvent) event;
            ClipboardResult result = clipboardEvent.getClipboardResult();
            
            if (result.isSuccess()) {
                String clipboardText = result.getText();
                processClipboardContent(clipboardText);
            } else {
                showStatus("Could not read clipboard: " + result.getError(), "error");
            }
        }
    }
    
    @Listen("onClick = #pasteButton")
    public void pasteFromClipboard() {
        showStatus("Reading from clipboard...", "info");
        clipboardHelper.readText();
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
     * Show status message with appropriate styling
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
        
        // Auto-clear status after 3 seconds for success/info messages
        if ("success".equals(type) || "info".equals(type)) {
            Clients.evalJavaScript("setTimeout(() => { " +
                "const label = zk.$('#" + statusLabel.getUuid() + "'); " +
                "if (label) label.setValue(''); " +
                "}, 3000);");
        }
    }
}