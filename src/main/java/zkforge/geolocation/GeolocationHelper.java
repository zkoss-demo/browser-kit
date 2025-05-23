package zkforge.geolocation;

import com.google.gson.Gson;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.function.Consumer;

/**
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API/Using_the_Geolocation_API
 * simplify the usage of JavaScript geolocation api
 * - create an anchor component to fire events to a server
 */
public class GeolocationHelper {
    public static final String EVENT_NAME = "onGetLocation";
    public static final String WIDGET_NAME = GeolocationHelper.class.getSimpleName();
    public static final String CSS_CLASS = "z-" + WIDGET_NAME.toLowerCase();
    public static final String GEOLOCATION_HELPER_JS_PATH = "~./js/" + WIDGET_NAME + ".js";
    private static Div anchor; /* used to fire events to the server */
    private final Consumer<GeolocationPositionResult> callback;
    private Gson gson = new Gson();
    /**
     * @param callback     the callback to handle the result
     * @param helperJsPath the path of the helper js file
     */
    public GeolocationHelper(Consumer<GeolocationPositionResult> callback, String helperJsPath) {
        ensureExecutionAvailable();
        this.callback = callback;
        initAnchorComponent(callback);
        initHelperJavaScript(helperJsPath);
    }

    public GeolocationHelper(Consumer<GeolocationPositionResult> callback) {
        ensureExecutionAvailable();
        this.callback = callback;
        initAnchorComponent(callback);
        initHelperJavaScript(GEOLOCATION_HELPER_JS_PATH);
    }

    protected static void initHelperJavaScript(String jsPath) {
        Script geolocatlHelperScript = new Script();
        geolocatlHelperScript.setSrc(jsPath);
        geolocatlHelperScript.setPage(Executions.getCurrent().getDesktop().getFirstPage());
    }


    public void requestLocation() {
        Clients.evalJavaScript(WIDGET_NAME + ".requestLocation()");
    }

    /**
     * client side fire events via anchor component
     */
    protected Component initAnchorComponent(Consumer<GeolocationPositionResult> callback) {
        anchor = new Div();
        anchor.setSclass(CSS_CLASS);
        anchor.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        anchor.addEventListener(EVENT_NAME, event -> {
            GeolocationPositionResult result = parseResult(event.getData());
            callback.accept(result);
        });
        return anchor;
    }

    private GeolocationPositionResult parseResult(Object data) {
        if (isGeoLocationPosition(data)) {
            GeoLocationPosition geoLocationPosition = gson.fromJson(data.toString(), GeoLocationPosition.class);
            return geoLocationPosition;
        } else {
            return gson.fromJson(data.toString(), GeolocationPositionError.class);
        }
    }

    protected static boolean isGeoLocationPosition(Object data) {
        return ((JSONObject) data).containsKey("coords");
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

}
