/*
 * Copyright (C) 2020, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301  USA
 */

package com.gocypher.cybench.toolWindow.factories;

import java.io.File;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;

import com.gocypher.cybench.toolWindow.CyBenchToolWindow;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

public class ToolWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {
    public static HashMap<File, Content> loaded = new HashMap<>();
    public static HashMap<Content, File> loadedContents = new HashMap<>();
    public static HashMap<File, CyBenchToolWindow> loadedWindows = new HashMap<>();

    public static void addReportView(@NotNull ToolWindow toolWindow, CyBenchToolWindow myToolWindow) {

        ContentManager contentManager = toolWindow.getContentManager();

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindow.getContent(), myToolWindow.getFile().getName(),
                false);
        loaded.put(myToolWindow.getFile(), content);
        loadedWindows.put(myToolWindow.getFile(), myToolWindow);
        loadedContents.put(content, myToolWindow.getFile());
        content.setDisposer(() -> {
            File remove = loadedContents.remove(content);
            loaded.remove(remove);
            loadedWindows.remove(remove);
        });

        ApplicationManager.getApplication().invokeLater(() -> {
            contentManager.addContent(content);
            contentManager.setSelectedContent(content);
        });

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CyBenchToolWindow myToolWindow = new CyBenchToolWindow(toolWindow, null);
        addReportView(toolWindow, myToolWindow);
    }

}
