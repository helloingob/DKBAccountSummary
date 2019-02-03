package com.helloingob.accsum.handler;

import java.util.Map;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

public class WindowHandler {

    public static Window createWindow(String title, String zulSourceFilePath, Map<String, ? extends Object> parameter, Boolean maximize) {
        Window win = new Window();
        win = (Window) Executions.createComponents(zulSourceFilePath, null, parameter);
        win.setPosition("center");
        win.setTitle(title);
        win.setClosable(true);
        win.setBorder("normal");
        win.setMaximizable(maximize);
        win.setSizable(maximize);
        win.doModal();
        return win;
    }

}
