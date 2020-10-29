package com.gocypher.cybench;

import com.github.JmhConfiguration;
import com.intellij.execution.ui.CommonJavaParametersPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class CyBenchConfigurable extends SettingsEditor<JmhConfiguration> {

    private JPanel editor = new JPanel();
    private JTextField forks = new JTextField();
    private JTextField measurementIterations = new JTextField();
    private JTextField warmUpIterations = new JTextField();
    private JTextField warmUpSeconds  = new JTextField();
    private JTextField threads = new JTextField();


    private final CommonJavaParametersPanel commonProgramParameters;

    public CyBenchConfigurable() {
        editor.setLayout(new BoxLayout(editor, BoxLayout.X_AXIS));
        commonProgramParameters = new CommonJavaParametersPanel();
        commonProgramParameters.add(LabeledComponent.create(forks, "Number of forks", "West"));
        commonProgramParameters.add(LabeledComponent.create(measurementIterations, "Measurement iterations", "West"));
        commonProgramParameters.add(LabeledComponent.create(warmUpIterations, "Warm up iterations", "West"));
        commonProgramParameters.add(LabeledComponent.create(warmUpSeconds, "Warm up seconds", "West"));
        commonProgramParameters.add(LabeledComponent.create(threads, "Number of forks", "West"));
        editor.add(commonProgramParameters);
    }

    @Override
    protected void resetEditorFrom(JmhConfiguration jmhConfiguration) {
        commonProgramParameters.reset(jmhConfiguration);
    }

    @Override
    protected void applyEditorTo(JmhConfiguration jmhConfiguration) throws ConfigurationException {
        commonProgramParameters.applyTo(jmhConfiguration);
    }

    @NotNull
    @Override
    protected JComponent createEditor() {

        return editor;
    }
}
