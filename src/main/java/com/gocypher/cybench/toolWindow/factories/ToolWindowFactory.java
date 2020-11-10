package com.gocypher.cybench.toolWindow.factories;

import com.gocypher.cybench.toolWindow.CyBenchToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    public static HashMap<File, Content> loaded= new HashMap<>();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CyBenchToolWindow myToolWindow = new CyBenchToolWindow(toolWindow, null);
        addReportView(toolWindow, myToolWindow);
    }

    public static void addReportView(@NotNull ToolWindow toolWindow, CyBenchToolWindow myToolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), myToolWindow.getFile().getName(), false);
        loaded.put(myToolWindow.getFile(),content );

        contentManager.addContent(content);
        contentManager.setSelectedContent(content);

    }

}
