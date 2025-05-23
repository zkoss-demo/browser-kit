package zkforge.clipboard;

import com.google.gson.Gson;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.function.Consumer;

/**
 * https://developer.mozilla.org/en-US/docs/Web/API/Clipboard_API
 */
public class ClipboardHelper {
    public static final String WIDGET_NAME = ClipboardHelper.class.getSimpleName();
    public static final String CSS_CLASS = "z-" + WIDGET_NAME.toLowerCase();
    public static final String CLIPBOARD_HELPER_JS_PATH = "~./js/" + WIDGET_NAME + ".js";
    private static Div anchor;
    private final Consumer<ClipboardResult> callback;

    public ClipboardHelper(Consumer<ClipboardResult> callback) {
        ensureExecutionAvailable();
        this.callback = callback;
        initAnchorComponent();
        initHelperJavaScript(CLIPBOARD_HELPER_JS_PATH);
    }

    protected void initHelperJavaScript(String jsPath) {
        Script clipboardHelperScript = new Script();
        clipboardHelperScript.setSrc(jsPath);
        clipboardHelperScript.setPage(Executions.getCurrent().getDesktop().getFirstPage());
    }

    public void writeText(String text) {
        Clients.evalJavaScript(WIDGET_NAME + ".writeText('" + text + "')");
    }

    public void readText() {
        Clients.evalJavaScript(WIDGET_NAME + ".readText()");
    }

    protected void initAnchorComponent() {
        anchor = new Div();
        anchor.setSclass(CSS_CLASS);
        anchor.setPage(Executions.getCurrent().getDesktop().getFirstPage());
        anchor.addEventListener("onClipboardData", event -> {
            callback.accept(parseResponse((JSONObject) event.getData()));
        });
    }

    private Gson gson = new Gson();

    protected ClipboardResult parseResponse(JSONObject data){
        return gson.fromJson(data.toString(), ClipboardResult.class);
    }

    protected static void ensureExecutionAvailable() {
        if (Executions.getCurrent() == null) {
            throw new IllegalStateException("This method can only be called when an Execution is available");
        }
    }
}