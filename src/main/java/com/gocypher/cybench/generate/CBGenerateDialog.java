/*
 * Copyright (C) 2020-2022, K2N.IO.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

package com.gocypher.cybench.generate;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.ui.MemberSelectionTable;
import com.intellij.refactoring.ui.PackageNameReferenceEditorCombo;
import com.intellij.refactoring.util.RefactoringMessageUtil;
import com.intellij.refactoring.util.classMembers.MemberInfo;
import com.intellij.testIntegration.TestIntegrationUtils;
import com.intellij.ui.*;
import com.intellij.util.ui.JBUI;

public class CBGenerateDialog extends DialogWrapper {
    private static final String BENCHMARK_MODE_KEY = "benchmarkMode";
    private static final String RECENTS_KEY = "CreateJmhDialog.RecentsKey";
    private final Module myTargetModule;
    private final Project myProject;
    private final PsiClass myTargetClass;
    private final PsiPackage myTargetPackage;
    private final ComboBox<String> benchmarkModeCombo = new ComboBox<>(new DefaultComboBoxModel<>());

    private final JCheckBox myGenerateBeforeBox = new JCheckBox("@Setup");
    private final JCheckBox myGenerateAfterBox = new JCheckBox("@TearDown");
    private final JCheckBox myGenerateBenchmarkTag = new JCheckBox("@BenchmarkTag");
    private final MemberSelectionTable myMethodsTable = new MemberSelectionTable(Collections.emptyList(), null);
    protected PsiDirectory myTargetDirectory;
    private HashMap<String, String> externalProperties;
    private EditorTextField myTargetClassNameField;
    private ReferenceEditorComboWithBrowseButton myTargetPackageField;
    private String defaultMode = "Mode.Throughput";
    private String mode;

    protected CBGenerateDialog(@Nullable Project project, PsiClass psiClass, PsiPackage srcPackage, Module module) {
        super(project);
        myProject = project;
        myTargetClass = psiClass;
        myTargetPackage = srcPackage;
        myTargetModule = module;
        setTitle("Generate CyBench benchmark class");
        init();

    }

    private static Insets insets(int top) {
        return insets(top, 0);
    }

    private static Insets insets(int top, int bottom) {
        return JBUI.insets(top, 8, bottom, 8);
    }

    protected String suggestTestClassName(PsiClass targetClass) {
        return targetClass.getName() + "Benchmark";
    }

    private void updateMethodsTable() {
        List<MemberInfo> methods = TestIntegrationUtils.extractClassMethods(myTargetClass, false);

        Set<PsiMember> selectedMethods = new HashSet<>();
        for (MemberInfo each : myMethodsTable.getSelectedMemberInfos()) {
            selectedMethods.add(each.getMember());
        }
        for (MemberInfo each : methods) {
            each.setChecked(true);
        }

        myMethodsTable.setMemberInfos(methods);
    }

    private PropertiesComponent getProperties() {
        return PropertiesComponent.getInstance(myProject);
    }

    @Override
    protected String getDimensionServiceKey() {
        return getClass().getName();
    }

    @Override
    protected String getHelpId() {
        return "CyBench";
    }

    @Override
    protected void doHelpAction() {
        BrowserUtil.browse("https://cybench.io/");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return myTargetClassNameField;
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constr = new GridBagConstraints();

        constr.fill = GridBagConstraints.HORIZONTAL;
        constr.anchor = GridBagConstraints.WEST;

        int gridy = 1;

        constr.insets = insets(4);
        constr.gridy = gridy++;
        constr.gridx = 0;
        constr.weightx = 0;

        constr.gridx = 1;
        constr.weightx = 1;
        constr.gridwidth = GridBagConstraints.REMAINDER;
        constr.gridheight = 1;

        constr.insets = insets(6);
        constr.gridy = gridy++;
        constr.gridx = 0;
        constr.weightx = 0;
        constr.gridwidth = 1;
        panel.add(new JLabel("Benchmark class name"), constr);

        myTargetClassNameField = new EditorTextField(suggestTestClassName(myTargetClass));
        myTargetClassNameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                getOKAction().setEnabled(PsiNameHelper.getInstance(myProject).isIdentifier(getClassName()));
            }
        });

        constr.gridx = 1;
        constr.weightx = 1;
        panel.add(myTargetClassNameField, constr);

        constr.insets = insets(1);
        constr.gridy = gridy++;
        constr.gridx = 0;
        constr.weightx = 0;
        panel.add(new JLabel("Package"), constr);

        constr.gridx = 1;
        constr.weightx = 1;

        String targetPackageName = myTargetPackage != null ? myTargetPackage.getQualifiedName() : "";
        myTargetPackageField = new PackageNameReferenceEditorCombo(targetPackageName, myProject, RECENTS_KEY,
                "Package");

        new AnAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                myTargetPackageField.getButton().doClick();
            }
        }.registerCustomShortcutSet(
                new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK)),
                myTargetPackageField.getChildComponent());

        JPanel targetPackagePanel = new JPanel(new BorderLayout());
        targetPackagePanel.add(myTargetPackageField, BorderLayout.CENTER);
        panel.add(targetPackagePanel, constr);

        constr.gridx = 0;
        constr.gridy = gridy++;
        constr.weightx = 0;

        panel.add(new JLabel("Benchmark Mode"), constr);
        constr.gridx = 1;
        constr.weightx = 1;
        panel.add(benchmarkModeCombo, constr);
        prepareBenchmarkModeComboModel();

        constr.insets = insets(6);
        constr.gridy = gridy++;
        constr.gridx = 0;
        constr.weightx = 0;
        panel.add(new JLabel("Generate"), constr);

        constr.gridx = 1;
        myGenerateBeforeBox.setSelected(true);
        panel.add(myGenerateBeforeBox, constr);

        constr.insets = insets(1);
        constr.gridy = gridy++;
        myGenerateAfterBox.setSelected(true);
        panel.add(myGenerateAfterBox, constr);

        constr.insets = insets(1);
        constr.gridy = gridy++;
        myGenerateBenchmarkTag.setSelected(true);
        panel.add(myGenerateBenchmarkTag, constr);

        JLabel membersLabel = new JLabel("Select methods");
        membersLabel.setLabelFor(myMethodsTable);
        panel.add(membersLabel, constr);

        constr.insets = insets(1, 8);
        constr.gridy = gridy++;
        constr.gridx = 0;
        constr.gridwidth = GridBagConstraints.REMAINDER;
        constr.fill = GridBagConstraints.BOTH;
        constr.weighty = 1;
        panel.add(ScrollPaneFactory.createScrollPane(myMethodsTable), constr);

        updateMethodsTable();
        return panel;
    }

    private void generateNumField(JPanel panel, GridBagConstraints constr, EditorTextField numField) {
        numField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                if (numField.getText() != null && !numField.getText().trim().isEmpty()) {
                    getOKAction().setEnabled(numField.getText().matches("\\d+"));
                }
            }
        });
        constr.gridx = 1;
        constr.weightx = 1;
        panel.add(numField, constr);
    }

    private void prepareBenchmarkModeComboModel() {
        benchmarkModeCombo.setRenderer(SimpleListCellRenderer.create((label, value, index) -> {
            if (value != null) {
                label.setText(value);
            }
        }));
        DefaultComboBoxModel<String> benchmarkModeComboModel = (DefaultComboBoxModel<String>) benchmarkModeCombo
                .getModel();
        benchmarkModeComboModel.addElement("Mode.Throughput");
        benchmarkModeComboModel.addElement("Mode.AverageTime");
        benchmarkModeComboModel.addElement("Mode.SampleTime");
        benchmarkModeComboModel.addElement("Mode.SingleShotTime");
        benchmarkModeComboModel.addElement("Mode.All");
        if (defaultMode != null) {
            benchmarkModeCombo.setSelectedItem(defaultMode);
        } else {
            benchmarkModeCombo.setSelectedIndex(0);
        }
    }

    public String getClassName() {
        return myTargetClassNameField.getText();
    }

    public PsiClass getTargetClass() {
        return myTargetClass;
    }

    public PsiDirectory getTargetDirectory() {
        return myTargetDirectory;
    }

    public Collection<MemberInfo> getSelectedMethods() {
        return myMethodsTable.getSelectedMemberInfos().stream()
                .collect(Collectors.toMap(memberInfo -> memberInfo.getMember().getName(), memberInfo -> memberInfo,
                        (memberInfo, memberInfo2) -> memberInfo))
                .values();
    }

    public boolean shouldGenerateTearDown() {
        return myGenerateAfterBox.isSelected();
    }

    public boolean shouldGenerateSetup() {
        return myGenerateBeforeBox.isSelected();
    }

    public boolean shouldGenerateBenchmarkTag() {
        return myGenerateBeforeBox.isSelected();
    }

    public String getMode() {
        return mode;
    }

    /*
     * ok action
     *
     */
    @Override
    protected void doOKAction() {
        RecentsManager.getInstance(myProject).registerRecentEntry(RECENTS_KEY, myTargetPackageField.getText());
        mode = String.valueOf(benchmarkModeCombo.getSelectedItem());
        super.doOKAction();
    }

    protected String checkCanCreateClass() {
        return RefactoringMessageUtil.checkCanCreateClass(myTargetDirectory, getClassName());
    }

    @Nullable
    private PsiDirectory chooseDefaultDirectory(PsiDirectory[] directories, List<VirtualFile> roots) {
        List<PsiDirectory> dirs = new ArrayList<>();
        PsiManager psiManager = PsiManager.getInstance(myProject);
        for (VirtualFile file : ModuleRootManager.getInstance(myTargetModule)
                .getSourceRoots(JavaSourceRootType.TEST_SOURCE)) {
            PsiDirectory dir = psiManager.findDirectory(file);
            if (dir != null) {
                dirs.add(dir);
            }
        }
        if (!dirs.isEmpty()) {
            for (PsiDirectory dir : dirs) {
                String dirName = dir.getVirtualFile().getPath();
                if (dirName.contains("generated")) {
                    continue;
                }
                return dir;
            }
            return dirs.get(0);
        }
        for (PsiDirectory dir : directories) {
            VirtualFile file = dir.getVirtualFile();
            for (VirtualFile root : roots) {
                if (VfsUtilCore.isAncestor(root, file, false)) {
                    PsiDirectory rootDir = psiManager.findDirectory(root);
                    if (rootDir != null) {
                        return rootDir;
                    }
                }
            }
        }
        return ModuleManager.getInstance(myProject).getModuleDependentModules(myTargetModule).stream().flatMap(
                module -> ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.TEST_SOURCE).stream())
                .map(root -> psiManager.findDirectory(root)).findFirst().orElse(null);
    }

    private String getPackageName() {
        String name = myTargetPackageField.getText();
        return name != null ? name.trim() : "";
    }

}
