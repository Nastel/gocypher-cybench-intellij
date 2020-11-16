package com.gocypher.cybench.toolWindow.factories;

import com.gocypher.cybench.toolWindow.CyBenchExplorerToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class BrowseReportsToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    public static CyBenchExplorerToolWindow myToolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindow = new CyBenchExplorerToolWindow(toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

}
