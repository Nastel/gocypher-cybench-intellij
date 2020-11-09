package com.gocypher.cybench.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CyBenchToolWindow myToolWindow = new CyBenchToolWindow(toolWindow, null);
        addReportView(toolWindow, myToolWindow);
    }

    public static void addReportView(@NotNull ToolWindow toolWindow, CyBenchToolWindow myToolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), myToolWindow.getFile().getName(), false);
        toolWindow.getContentManager().addContent(content);
    }

}