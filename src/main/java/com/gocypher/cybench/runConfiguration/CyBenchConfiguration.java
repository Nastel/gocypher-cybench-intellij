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

package com.gocypher.cybench.runConfiguration;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.*;
import com.intellij.execution.configuration.CompatibilityAwareRunProfile;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.options.SettingsEditorGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

public class CyBenchConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule, Element>
        implements CommonJavaRunConfigurationParameters, CompatibilityAwareRunProfile {

    public static final String ATTR_VM_PARAMETERS = "vm-parameters";
    public static final String ATTR_PROGRAM_PARAMETERS = "program-parameters";
    public static final String ATTR_WORKING_DIR = "working-dir";
    public static final String JMH_START_CLASS = "com.gocypher.cybench.launcher.BenchmarkRunner";
    public static final String JMH_ANNOTATION_NAME = "org.openjdk.jmh.annotations.Benchmark";
    private String vmParameters;
    private boolean isAlternativeJrePathEnabled = false;
    private String alternativeJrePath;
    private String programParameters;
    private String workingDirectory;
    private Map<String, String> envs = new HashMap<>();
    private boolean passParentEnvs;

    private Map<CyBenchConfigurableParameters, Object> valueStore = new EnumMap<>(CyBenchConfigurableParameters.class);

    public CyBenchConfiguration(String name, Project project, ConfigurationFactory configurationFactory) {
        this(name, new JavaRunConfigurationModule(project, false), configurationFactory);
    }

    public CyBenchConfiguration(String name, JavaRunConfigurationModule configurationModule,
            ConfigurationFactory factory) {
        super(name, configurationModule, factory);
    }

    @Override
    public String getVMParameters() {
        return vmParameters;
    }

    @Override
    public void setVMParameters(String s) {
        vmParameters = s;
    }

    @Override
    public boolean isAlternativeJrePathEnabled() {
        return isAlternativeJrePathEnabled;
    }

    @Override
    public void setAlternativeJrePathEnabled(boolean b) {
        isAlternativeJrePathEnabled = b;
    }

    @Override
    public String getAlternativeJrePath() {
        return alternativeJrePath;
    }

    @Override
    public void setAlternativeJrePath(String s) {
        alternativeJrePath = s;
    }

    @Nullable
    @Override
    public String getRunClass() {
        return JMH_START_CLASS;
    }

    @Nullable
    @Override
    public String getPackage() {
        return null;
    }

    @Nullable
    @Override
    public String getProgramParameters() {
        return programParameters;
    }

    @Override
    public void setProgramParameters(@Nullable String s) {
        programParameters = s;
    }

    @Nullable
    @Override
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public void setWorkingDirectory(@Nullable String s) {
        workingDirectory = s;
    }

    @NotNull
    @Override
    public Map<String, String> getEnvs() {
        return new HashMap<>(envs);
    }

    @Override
    public void setEnvs(@NotNull Map<String, String> map) {
        envs = new HashMap<>(map);
    }

    @Override
    public boolean isPassParentEnvs() {
        return passParentEnvs;
    }

    @Override
    public void setPassParentEnvs(boolean b) {
        passParentEnvs = b;
    }

    @Override
    public Collection<Module> getValidModules() {
        return JavaRunConfigurationModule.getModulesForClass(getProject(), getRunClass());
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        SettingsEditorGroup<CyBenchConfiguration> group = new SettingsEditorGroup<>();
        group.addEditor(ExecutionBundle.message("run.configuration.configuration.tab.title"),
                new CyBenchConfigurableEditorView(getProject(), this));
        JavaRunConfigurationExtensionManager.getInstance().appendEditors(this, group);
        group.addEditor(ExecutionBundle.message("logs.tab.title"), new LogConfigurationPanel<>());
        return group;
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment)
            throws ExecutionException {
        return new BenchmarkState(getProject(), this, executionEnvironment);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        if (vmParameters != null) {
            element.setAttribute(ATTR_VM_PARAMETERS, vmParameters);
        }
        if (programParameters != null) {
            element.setAttribute(ATTR_PROGRAM_PARAMETERS, programParameters);
        }
        if (workingDirectory != null) {
            element.setAttribute(ATTR_WORKING_DIR, workingDirectory);
        }

        for (CyBenchConfigurableParameters parameter : CyBenchConfigurableParameters.values()) {
            if (valueStore.containsKey(parameter) && valueStore.get(parameter) != null) {
                element.setAttribute(parameter.key, String.valueOf(valueStore.get(parameter)));
            }
        }
        writeModule(element);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        setVMParameters(element.getAttributeValue(ATTR_VM_PARAMETERS));

        setProgramParameters(element.getAttributeValue(ATTR_PROGRAM_PARAMETERS));
        setWorkingDirectory(element.getAttributeValue(ATTR_WORKING_DIR));

        for (CyBenchConfigurableParameters parameter : CyBenchConfigurableParameters.values()) {
            valueStore.put(parameter, element.getAttributeValue(parameter.key));
        }

        readModule(element);
    }

    @Override
    public boolean mustBeStoppedToRun(@NotNull RunConfiguration runConfiguration) {
        return true;
    }

    public Map<CyBenchConfigurableParameters, Object> getValueStore() {
        return valueStore;
    }

}
