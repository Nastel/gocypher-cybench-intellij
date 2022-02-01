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

package com.gocypher.cybench.runConfiguration;

import static com.gocypher.cybench.runConfiguration.CyBenchConfigurableParameters.COLLECT_HW;
import static com.gocypher.cybench.runConfiguration.CyBenchConfigurableParameters.SHOULD_SEND_REPORT;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

import org.jetbrains.annotations.NotNull;

import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;

public class CyBenchConfigurableEditorView extends SettingsEditor<CyBenchConfiguration> {

    private final CommonJavaParametersPanel commonProgramParameters;
    private JPanel editor = new JPanel();
    private Map<CyBenchConfigurableParameters, JComponent> configurableStore = new EnumMap<>(
            CyBenchConfigurableParameters.class);

    public CyBenchConfigurableEditorView(Project project, CyBenchConfiguration cyBenchConfiguration) {
        editor.setLayout(new BoxLayout(editor, BoxLayout.X_AXIS));
        commonProgramParameters = new CommonJavaParametersPanel();

        // Setup for CyBench configurable fields defined in CyBenchConfigurableParameters
        for (CyBenchConfigurableParameters parameter : CyBenchConfigurableParameters.values()) {
            JComponent comp;
            if (parameter.type == CyBenchConfigurableParameters.TYPE.BOOLEAN) {
                comp = new JCheckBox();
                JCheckBox jCheckBox = (JCheckBox) comp;
                jCheckBox.setSelected(
                        Boolean.parseBoolean(String.valueOf(cyBenchConfiguration.getValueStore().containsKey(parameter)
                                ? cyBenchConfiguration.getValueStore().get(parameter) : parameter.defaultValue)));
                jCheckBox.addChangeListener(e -> {
                    if (parameter == SHOULD_SEND_REPORT && jCheckBox.isSelected()) {
                        cyBenchConfiguration.getValueStore().put(SHOULD_SEND_REPORT, true);
                        cyBenchConfiguration.getValueStore().put(COLLECT_HW, true);
                        ((JCheckBox) configurableStore.get(SHOULD_SEND_REPORT)).setSelected(true);
                        ((JCheckBox) configurableStore.get(COLLECT_HW)).setSelected(true);
                    } else {
                        cyBenchConfiguration.getValueStore().put(parameter, jCheckBox.isSelected());
                    }
                });
            } else {
                comp = new JTextField();
                JTextField jTextField = (JTextField) comp;
                jTextField.setText(String.valueOf(cyBenchConfiguration.getValueStore().containsKey(parameter)
                        ? cyBenchConfiguration.getValueStore().get(parameter) : parameter.defaultValue));
                installValidator(project, jTextField, parameter.validator, parameter.error);
                jTextField.getDocument().addDocumentListener(new DocumentAdapter() {
                    @Override
                    protected void textChanged(@NotNull DocumentEvent e) {
                        cyBenchConfiguration.getValueStore().put(parameter, jTextField.getText());
                    }
                });

            }
            configurableStore.put(parameter, comp);
            commonProgramParameters.add(LabeledComponent.create(comp, parameter.readableName, "West"));

        }

        editor.add(commonProgramParameters);

    }

    private static void installValidator(Project project, JTextField jTextField, Predicate<String> validator,
            String errorMessage) {
        (new ComponentValidator(project)).withValidator(() -> {
            String pt = jTextField.getText();
            if (StringUtil.isNotEmpty(pt)) {
                try {
                    return validator.test(pt) ? null : new ValidationInfo(errorMessage, jTextField);
                } catch (NumberFormatException var3) {
                    return new ValidationInfo(errorMessage, jTextField);
                }
            } else {
                return null;
            }
        }).installOn(jTextField);
        jTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                ComponentValidator.getInstance(jTextField).ifPresent((v) -> v.revalidate());
            }
        });
    }

    @Override
    protected void resetEditorFrom(CyBenchConfiguration jmhConfiguration) {
        commonProgramParameters.reset(jmhConfiguration);
    }

    @Override
    protected void applyEditorTo(CyBenchConfiguration jmhConfiguration) throws ConfigurationException {
        commonProgramParameters.applyTo(jmhConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        return editor;
    }
}
