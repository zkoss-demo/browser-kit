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
 * - create an anchor component to listen to events
 * - need to put geolocation-helper.js in the corresponding path, default is under the same folder as the zul
 */
public class GeolocationHelper {
    public static final String CSS_CLASS = "geolocation-helper";
    public static final String EVENT_NAME = "onGetLocation";
    public static final String GEOLOCATION_HELPER_JS_PATH = "~./js/geolocation-helper.js";
    private static Div anchor;
    private final Consumer<GeolocationPositionResult> callback;
    private Gson gson = new Gson();
    public static final String WIDGET_NAME = GeolocationHelper.class.getSimpleName();
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

    private static void initHelperJavaScript(String jsPath) {
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

    private static boolean isGeoLocationPosition(Object data) {
        return ((JSONObject) data).containsKey("coords");
    }

    private static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }

    public static class GeoLocationCoordinates {
        public double latitude;
        public double longitude;

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public static class GeoLocationPosition implements GeolocationPositionResult {
        private GeoLocationCoordinates coords = new GeoLocationCoordinates();
        private long timestamp;

        public GeoLocationCoordinates getCoords() {
            return coords;
        }

        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public GeoLocationPosition getPosition() {
            return this;
        }

        @Override
        public GeolocationPositionError getError() {
            throw new RuntimeException("not an " + GeolocationPositionError.class);
        }
    }

    public static class GeolocationPositionError implements GeolocationPositionResult {
        private final int code; // Error code
        private final String message; // Human-readable error message

        // Constructor
        public GeolocationPositionError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        // Getter for code
        public int getCode() {
            return code;
        }

        // Getter for message
        public String getMessage() {
            return message;
        }

        @Override
        public GeoLocationPosition getPosition() {
            throw new RuntimeException("not an " + GeoLocationPosition.class);
        }

        @Override
        public GeolocationPositionError getError() {
            return this;
        }
    }

    // Enum to represent error codes and descriptions
    static enum ErrorType {
        PERMISSION_DENIED(1, "The acquisition of the geolocation information failed because the page didn't have the necessary permissions, for example because it is blocked by a Permissions Policy."),
        POSITION_UNAVAILABLE(2, "The acquisition of the geolocation failed because at least one internal source of position returned an internal error."),
        TIMEOUT(3, "The time allowed to acquire the geolocation was reached before the information was obtained.");

        private final int code;
        private final String description;

        // Constructor for enum
        ErrorType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        // Getter for code
        public int getCode() {
            return code;
        }

        // Getter for description
        public String getDescription() {
            return description;
        }

        // Method to convert enum to GeolocationPositionError
        public GeolocationPositionError toGeolocationPositionError() {
            return new GeolocationPositionError(this.code, this.description);
        }

        // Static method to get ErrorType by code
        public static ErrorType fromCode(int code) {
            for (ErrorType error : values()) {
                if (error.getCode() == code) {
                    return error;
                }
            }
            throw new IllegalArgumentException("Invalid error code: " + code);
        }
    }

}
