package com.rspsi.editor.tools;

import com.rspsi.editor.controllers.MainController;
import javafx.scene.control.Menu;

public interface ToolbarMenuProvider {
    void setupUI(Menu toolbar);
}
