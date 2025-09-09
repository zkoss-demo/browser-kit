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
    private static final String GEOLOCATION_HELPER_KEY = "browserkit.geolocation.helper";
    private static final String GEOLOCATION_HELPER_JS_PATH = "~./js/GeolocationHelper.js";
    public static final String ON_GET_LOCATION = "onGetLocation";

    private final Consumer<GeoLocationPosition> positionCallback;
    private final Consumer<GeolocationPositionError> errorCallback;

    public GeolocationHelper(Consumer<GeoLocationPosition> positionCallback,
                           Consumer<GeolocationPositionError> errorCallback) {
        ensureExecutionAvailable();
        ensureDesktopScopeSingleton();

        this.positionCallback =  Objects.requireNonNull(positionCallback, "positionCallback cannot be null");
        this.errorCallback = Objects.requireNonNull(errorCallback, "errorCallback cannot be null");

        initResultHandling();
        initHelperJavaScript(GEOLOCATION_HELPER_JS_PATH);
    }

    protected void ensureDesktopScopeSingleton() {
        Desktop desktop = Executions.getCurrent().getDesktop();
        // Enforce singleton constraint
        if (desktop.getAttribute(GEOLOCATION_HELPER_KEY) != null) {
            throw new IllegalStateException(
                "Only one GeolocationHelper allowed per page. " +
                "Existing instance found.");
        }
        desktop.setAttribute(GEOLOCATION_HELPER_KEY, this);
    }

    protected static void initHelperJavaScript(String jsPath) {
        Script geolocationHelperScript = new Script();
        geolocationHelperScript.setSrc(jsPath);
        geolocationHelperScript.setPage(Executions.getCurrent().getDesktop().getFirstPage());
    }

    private void initResultHandling() {

        GeolocationResultListener listener = new GeolocationResultListener();
        listener.setCallbacks(positionCallback, errorCallback);
        Executions.getCurrent().getDesktop().getFirstPage().addEventListener(ON_GET_LOCATION, listener);
    }

    public void getCurrentPosition() {
        Clients.evalJavaScript("GeolocationHelper.getCurrentPosition()");
    }

    public void dispose() {
        Desktop desktop = Executions.getCurrent().getDesktop();
        desktop.removeAttribute(GEOLOCATION_HELPER_KEY);
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

}
