package org.zkoss.zkforge.geolocation;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Optional;

/**
 * Static helper class that provides Java access to the browser's Geolocation API.
 * 
 * <p>This class wraps the native browser Geolocation API and provides static methods to request
 * the user's current position. All operations are asynchronous and require user permission.</p>
 * 
 * <p><strong>Important:</strong> Geolocation operations require user permission and may be 
 * restricted in certain browsers or contexts (e.g., non-HTTPS).</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * // Request current position (results delivered via desktop events)
 * GeolocationHelper.getCurrentPosition();
 * </pre>
 *
 * <p>Results are delivered asynchronously via {@link GeolocationEvent} posted to the desktop.
 * Components can listen for these events to handle geolocation results.</p>
 * 
 * <p>Based on <a href="https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API">MDN Geolocation API</a></p>
 */
public class GeolocationHelper {
    protected static final String GEOLOCATION_HELPER_KEY = "browserkit.geolocationhelper";
    protected static final String GEOLOCATION_HELPER_JS_PATH = "~./js/GeolocationHelper.js";
    protected static GeoLocationAuService auService;

    /**
     * Request the current position from the browser.
     * Results are delivered asynchronously via {@link GeolocationEvent}.
     * 
     * <p>Components can listen for geolocation events:</p>
     * <pre>
     * &#64;Listen(GeolocationEvent.EVENT_NAME + " = #root")
     * public void handleGeolocation(GeolocationEvent event) {
     *     if (event.isSuccess()) {
     *         GeoLocationPosition position = event.getGeoLocationPosition();
     *         // Process position data
     *     } else {
     *         GeolocationPositionError error = event.getGeoLocationPositionError();
     *         // Handle error
     *     }
     * }
     * </pre>
     * 
     * @throws IllegalStateException if called outside an execution context
     */
    public static void getCurrentPosition() {
        if (Executions.getCurrent().getDesktop().getAttribute(GEOLOCATION_HELPER_KEY)== null) return;
        Clients.evalJavaScript("GeolocationHelper.getCurrentPosition()");
    }

    /**
     * Initialize geolocation helper for the current desktop if not already initialized.
     * This method ensures the AU service and JavaScript are properly set up.
     * You should call this method before you call {@link #getCurrentPosition()}.
     * You should call this method in a Composer's life cycle method e.g. {@link org.zkoss.zk.ui.util.Composer#doAfterCompose(Component)}.
     */
    public static void init() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        
        // Check if already initialized for this desktop
        if (desktop.getAttribute(GEOLOCATION_HELPER_KEY) != null) {
            return;
        }
        // Mark as initialized
        desktop.setAttribute(GEOLOCATION_HELPER_KEY, true);
        
        addAuService(desktop);
        addHelperScript(desktop);
    }

    protected static void addHelperScript(Desktop desktop) {
        Script helperScript = new Script();
        helperScript.setId(GEOLOCATION_HELPER_KEY);
        helperScript.setSrc(GEOLOCATION_HELPER_JS_PATH);
        helperScript.setPage(desktop.getFirstPage());
    }

    protected static void addAuService(Desktop desktop) {
        if (auService == null) {
            auService = new GeoLocationAuService();
        }
        desktop.addListener(auService);
    }

    /**
     * Dispose geolocation helper for the current desktop.
     * Removes the AU service listener and JavaScript helper for this desktop.
     * 
     * @throws IllegalStateException if called outside an execution context
     */
    public static void dispose() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        
        // Mark as not initialized
        desktop.removeAttribute(GEOLOCATION_HELPER_KEY);
        
        // Remove AU service listener for this desktop
        if (auService != null) {
            desktop.removeListener(auService);
        }
        
        // Remove JavaScript helper
        Selectors.find(desktop.getFirstPage(), "#" + GEOLOCATION_HELPER_KEY)
                .forEach(Component::detach);
    }

    private static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }
}
