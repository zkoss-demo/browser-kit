package org.zkoss.zkforge.geolocation;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import java.util.Optional;

/**
 * A helper class to facilitate using the Geolocation API in ZK applications.
 * Only one helper of this type is allowed per desktop.
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API
 * This is a prototype addon, we keep most fields and methods protected to allow extension for future evolution.
 */
public class GeolocationHelper {
    protected static final String GEOLOCATION_HELPER_KEY = "browserkit.geolocationhelper";
    protected static String GEOLOCATION_HELPER_JS_PATH = "~./js/GeolocationHelper.js";
    protected Script helperScript;
    protected Desktop desktop;
    protected GeoLocationAuService auService;

    /**
     * Get the singleton instance of GeolocationHelper for the current desktop.
     * If not existing, a new instance will be created and initialized.
     * This method can only be called when an Execution is available (e.g., during a request).
     */
    public static GeolocationHelper getInstance() {
        ensureExecutionAvailable();
        Desktop desktop = Executions.getCurrent().getDesktop();
        return Optional.ofNullable((GeolocationHelper) desktop.getAttribute(GEOLOCATION_HELPER_KEY))
                .orElseGet(GeolocationHelper::new);
    }

    private GeolocationHelper() {
        ensureDesktopScopeSingleton();
        addAuService();
        addHelperJavaScript(GEOLOCATION_HELPER_JS_PATH);
    }


    protected void ensureDesktopScopeSingleton() {
        desktop = Executions.getCurrent().getDesktop();
        desktop.setAttribute(GEOLOCATION_HELPER_KEY, this);
    }

    protected void addHelperJavaScript(String jsPath) {
        helperScript = new Script();
        helperScript.setSrc(jsPath);
        helperScript.setPage(desktop.getFirstPage());
    }

    protected void removeHelperJavaScript() {
        helperScript.detach();
    }

    protected void addAuService() {
        auService = new GeoLocationAuService();
        desktop.addListener(auService);
    }

    /**
     * Request the current position from the browser.
     * The result will be sent back asynchronously via a GeolocationEvent.
     * Make sure to register an event listener for GeolocationEvent to handle the result.
     * It will send events to all root components, so you can listen to it from any component in the page.
     * <code>
     *     @Listen(GeolocationEvent.EVENT_NAME + "= #root")
     *     public void handle(GeolocationEvent event){
     *         if (event.isSuccess()){
     *             handleLocationSuccess(event.getGeoLocationPosition());
     *         }else{
     *             handleLocationError(event.getGeoLocationPositionError());
     *         }
     *     }
     * </code>
     * Since it doesn't unload the helper JavaScript, it doesn't getCurrentPosition() after being disposed to avoid errors.
     */
    public void getCurrentPosition() {
        if (auService == null)  return; // disposed
        Clients.evalJavaScript("GeolocationHelper.getCurrentPosition()");
    }

    /**
     * Dispose this helper, removing the page listener and the helper JavaScript.
     * After calling this method, this instance should not be used anymore.
     */
    public void dispose() {
        desktop.removeAttribute(GEOLOCATION_HELPER_KEY);
        removeAuService();
        removeHelperJavaScript();
    }

    protected void removeAuService() {
        desktop.removeListener(auService);
        auService = null;
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

}
