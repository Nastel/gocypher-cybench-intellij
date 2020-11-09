package com.gocypher.cybench;


import com.gocypher.cybench.runConfiguration.CyBenchConfigurableEditorView;
import com.gocypher.cybench.runConfiguration.CyBenchConfiguration;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.gocypher.cybench.CyBechResultTreeConsoleViewTest.createAndShowGUI;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PathMacros.class, FileChooserFactory.class})

@PowerMockIgnore({"sun.*", "javax.*"})
public class CyBenchConfigurableEditorViewTest {

    @Test
    public  void main() {
        PowerMockito.mockStatic(PathMacros.class);
        PowerMockito.mockStatic(FileChooserFactory.class);
        PowerMockito.when(FileChooserFactory.getInstance()).thenReturn(new FileChooserFactory() {
            @NotNull
            @Override
            public FileChooserDialog createFileChooser(@NotNull FileChooserDescriptor fileChooserDescriptor, @Nullable Project project, @Nullable Component component) {
                return null;
            }

            @NotNull
            @Override
            public PathChooserDialog createPathChooser(@NotNull FileChooserDescriptor fileChooserDescriptor, @Nullable Project project, @Nullable Component component) {
                return null;
            }

            @NotNull
            @Override
            public FileSaverDialog createSaveFileDialog(@NotNull FileSaverDescriptor fileSaverDescriptor, @Nullable Project project) {
                return null;
            }

            @NotNull
            @Override
            public FileSaverDialog createSaveFileDialog(@NotNull FileSaverDescriptor fileSaverDescriptor, @NotNull Component component) {
                return null;
            }

            @NotNull
            @Override
            public FileTextField createFileTextField(@NotNull FileChooserDescriptor fileChooserDescriptor, boolean b, @Nullable Disposable disposable) {
                return null;
            }

            @Override
            public void installFileCompletion(@NotNull JTextField jTextField, @NotNull FileChooserDescriptor fileChooserDescriptor, boolean b, @Nullable Disposable disposable) {

            }
        });
        PowerMockito.when(PathMacros.getInstance()).thenReturn(new PathMacros() {
            @NotNull
            @Override
            public Set<String> getAllMacroNames() {
                return null;
            }

            @Nullable
            @Override
            public String getValue(@NotNull String s) {
                return null;
            }

            @Override
            public void setMacro(@NotNull String s, @Nullable String s1) {

            }

            @Override
            public void addLegacyMacro(@NotNull String s, @NotNull String s1) {

            }

            @Override
            public void removeMacro(@NotNull String s) {

            }

            @NotNull
            @Override
            public Set<String> getUserMacroNames() {
                return Collections.emptySet();
            }

            @NotNull
            @Override
            public Map<String, String> getUserMacros() {
                return Collections.emptyMap();
            }

            @NotNull
            @Override
            public Set<String> getSystemMacroNames() {
                return Collections.emptySet();
            }

            @NotNull
            @Override
            public Collection<String> getIgnoredMacroNames() {
                return Collections.emptySet();
            }

            @Override
            public void setIgnoredMacroNames(@NotNull Collection<String> collection) {

            }

            @Override
            public void addIgnoredMacro(@NotNull String s) {

            }

            @Override
            public boolean isIgnoredMacroName(@NotNull String s) {
                return false;
            }

            @Override
            public void removeAllMacros() {

            }

            @NotNull
            @Override
            public Collection<String> getLegacyMacroNames() {
                return Collections.emptySet();
            }
        });
        CyBenchConfigurableEditorView c = new CyBenchConfigurableEditorView(mock(Project.class), mock(CyBenchConfiguration.class));
        createAndShowGUI(c.getComponent());
        while
        (true);


    }

}
