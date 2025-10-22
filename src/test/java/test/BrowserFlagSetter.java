package test;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Executions;

import javax.servlet.http.HttpServletRequest;

public class BrowserFlagSetter {
    public static void storeBrowserAttribute(){
        Boolean isSafari = Servlets.isBrowser((HttpServletRequest) Executions.getCurrent().getNativeRequest(), "safari");
        Executions.getCurrent().getDesktop().setAttribute("isSafari", isSafari);
    }
}
