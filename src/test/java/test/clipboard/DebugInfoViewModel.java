package test.clipboard;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zkforge.clipboard.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Demonstrates copying technical information for troubleshooting purposes.
 */
public class DebugInfoViewModel {
    public DebugInfoViewModel(){
        ClipboardHelper.init();
    }

    @Command
    public void handleClipboardAction(@ContextParam(ContextType.TRIGGER_EVENT) ClipboardEvent event) {
        ClipboardResult result = event.getClipboardResult();
        if (result.getAction() != ClipboardAction.WRITE) return; // Ignore other actions
        if (result.isSuccess()) {
            Notification.show("Debug info copied - ready to paste in support ticket", "info", null, "bottom_center", 3000);
        } else {
            Notification.show("Copy failed: " + result.getError(), "error", null, "bottom_center", 3000);
        }
    }

    @Command
    public void copyDebugInformation() {
        String debugInfo = generateDebugInformation();
        ClipboardHelper.writeText(debugInfo);
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
}