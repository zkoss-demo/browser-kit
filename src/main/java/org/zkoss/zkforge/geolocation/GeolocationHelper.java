package org.zkoss.zkforge.geolocation;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * GeolocationHelper with singleton desktop-level result handling pattern.
 * Only one helper of this type is allowed per page.
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API
 */
public class GeolocationHelper {
    protected static final String GEOLOCATION_HELPER_KEY = "browserkit.geolocationhelper";
    protected static String GEOLOCATION_HELPER_JS_PATH = "~./js/GeolocationHelper.js";
    public static final String EVENT_NAME = "onGetLocation";
    protected Script geolocationHelperScript;

    protected Consumer<GeoLocationPosition> positionCallback;
    protected Consumer<GeolocationPositionError> errorCallback;
    protected GeolocationResultListener listener;
    protected Desktop desktop;

    public GeolocationHelper(Consumer<GeoLocationPosition> positionCallback,
                           Consumer<GeolocationPositionError> errorCallback) {
        ensureExecutionAvailable();
        ensureDesktopScopeSingleton();
        setCallbacks(positionCallback, errorCallback);
        addPageListener();
        addHelperJavaScript(GEOLOCATION_HELPER_JS_PATH);
    }

    protected void setCallbacks(Consumer<GeoLocationPosition> positionCallback, Consumer<GeolocationPositionError> errorCallback) {
        this.positionCallback =  Objects.requireNonNull(positionCallback, "positionCallback cannot be null");
        this.errorCallback = Objects.requireNonNull(errorCallback, "errorCallback cannot be null");
    }

    protected void ensureDesktopScopeSingleton() {
        desktop = Executions.getCurrent().getDesktop();
        // Enforce singleton constraint
        if (desktop.getAttribute(GEOLOCATION_HELPER_KEY) != null) {
            throw new IllegalStateException(
                "Only one GeolocationHelper allowed per page. " +
                "Existing instance found.");
        }
        desktop.setAttribute(GEOLOCATION_HELPER_KEY, this);
    }

    protected void addHelperJavaScript(String jsPath) {
        geolocationHelperScript = new Script();
        geolocationHelperScript.setSrc(jsPath);
        geolocationHelperScript.setPage(desktop.getFirstPage());
    }

    protected void removeHelperJavaScript() {
        geolocationHelperScript.detach();
    }

    protected void addPageListener() {
        listener = new GeolocationResultListener();
        listener.setCallbacks(positionCallback, errorCallback);
        desktop.getFirstPage().addEventListener(EVENT_NAME, listener);
    }

    protected void removePageListener() {
        desktop.getFirstPage().removeEventListener(EVENT_NAME, listener);
    }

    public void getCurrentPosition() {
        Clients.evalJavaScript("GeolocationHelper.getCurrentPosition()");
    }

    /**
     * Dispose this helper, removing the page listener and the helper JavaScript.
     * After calling this method, this instance should not be used anymore.
     */
    public void dispose() {
        desktop.removeAttribute(GEOLOCATION_HELPER_KEY);
        removePageListener();
        removeHelperJavaScript();
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

}
