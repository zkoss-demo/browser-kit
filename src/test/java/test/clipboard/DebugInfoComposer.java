package test.clipboard;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
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
import org.zkoss.zul.Window;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Composer for Use Case 3: Developer Tools & Debug Information
 * Demonstrates copying technical information for troubleshooting purposes.
 */
public class DebugInfoComposer extends SelectorComposer<Component> {
    
    @Wire
    private Button copyDebugInfo;
    
    @Wire
    private Label debugStatusLabel;
    
    private ClipboardHelper clipboardHelper;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        // Get clipboard helper instance
        clipboardHelper = ClipboardHelper.getInstance();
        
        // Listen for clipboard events on this composer's root component
        comp.addEventListener(ClipboardEvent.EVENT_NAME, this::handleClipboardEvent);
        
    }
    
    public void handleClipboardEvent(org.zkoss.zk.ui.event.Event event) {
        if (event instanceof ClipboardEvent) {
            ClipboardEvent clipboardEvent = (ClipboardEvent) event;
            ClipboardResult result = clipboardEvent.getClipboardResult();
            
            if (result.isSuccess()) {
                showStatus("Debug info copied - ready to paste in support ticket", "success");
            } else {
                showStatus("Copy failed: " + result.getError(), "error");
                // Fallback: show debug info in popup for manual copy
            }
        }
    }
    
    @Listen("onClick = #copyDebugInfo")
    public void copyDebugInformation() {
        showStatus("Copying debug information...", "info");
        String debugInfo = generateDebugInformation();
        clipboardHelper.writeText(debugInfo);
    }
    
    /**
     * Generate comprehensive debug information
     */
    private String generateDebugInformation() {
        StringBuilder debugInfo = new StringBuilder();
        
        debugInfo.append("=== ZK Application Debug Information ===\n");
        debugInfo.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        // Session information
        debugInfo.append("-- Session Details --\n");
        debugInfo.append("Session : ").append(Sessions.getCurrent().toString()).append("\n");
        debugInfo.append("Max Inactive Interval: ").append(Sessions.getCurrent().getMaxInactiveInterval()).append("s\n\n");
        
        // Request information
        debugInfo.append("-- Request Details --\n");
        if (Executions.getCurrent() != null) {
            debugInfo.append("Server Name: ").append(Executions.getCurrent().getServerName()).append("\n");
            debugInfo.append("Server Port: ").append(Executions.getCurrent().getServerPort()).append("\n");
            debugInfo.append("Context Path: ").append(Executions.getCurrent().getContextPath()).append("\n");
            debugInfo.append("Desktop Path: ").append(Executions.getCurrent().getDesktop().getRequestPath()).append("\n");
            debugInfo.append("Remote Address: ").append(Executions.getCurrent().getRemoteAddr()).append("\n");
            debugInfo.append("Remote Host: ").append(Executions.getCurrent().getRemoteHost()).append("\n");
            
            // Browser information
            debugInfo.append("\n-- Browser Information --\n");
            debugInfo.append("User-Agent: ").append(Executions.getCurrent().getHeader("User-Agent")).append("\n");
            debugInfo.append("Accept-Language: ").append(Executions.getCurrent().getHeader("Accept-Language")).append("\n");
            debugInfo.append("Accept-Encoding: ").append(Executions.getCurrent().getHeader("Accept-Encoding")).append("\n");
        }
        
        // System information
        debugInfo.append("\n-- System Information --\n");
        debugInfo.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        debugInfo.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        debugInfo.append("OS Name: ").append(System.getProperty("os.name")).append("\n");
        debugInfo.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        debugInfo.append("OS Architecture: ").append(System.getProperty("os.arch")).append("\n");
        
        // ZK information
        debugInfo.append("\n-- ZK Framework --\n");
        debugInfo.append("ZK Version: ").append(org.zkoss.zk.Version.UID).append("\n");
        if (Executions.getCurrent() != null) {
            debugInfo.append("Desktop ID: ").append(Executions.getCurrent().getDesktop().getId()).append("\n");
        }
        
        debugInfo.append("\n=== End Debug Information ===\n");
        
        return debugInfo.toString();
    }
    

    /**
     * Show status message with appropriate styling
     */
    private void showStatus(String message, String type) {
        debugStatusLabel.setValue(message);
        
        switch (type) {
            case "success":
                debugStatusLabel.setStyle("color: #4CAF50; font-style: italic;");
                break;
            case "error":
                debugStatusLabel.setStyle("color: #f44336; font-style: italic;");
                break;
            case "warning":
                debugStatusLabel.setStyle("color: #ff9800; font-style: italic;");
                break;
            case "info":
            default:
                debugStatusLabel.setStyle("color: #666; font-style: italic;");
                break;
        }
        
        // Auto-clear status after 4 seconds for success/info messages
        if ("success".equals(type) || "info".equals(type)) {
            Clients.evalJavaScript("setTimeout(() => { " +
                "const label = zk.$('#" + debugStatusLabel.getUuid() + "'); " +
                "if (label) label.setValue(''); " +
                "}, 4000);");
        }
    }
}