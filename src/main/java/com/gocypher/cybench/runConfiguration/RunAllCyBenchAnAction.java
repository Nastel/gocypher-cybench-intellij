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

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class RunAllCyBenchAnAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        ConfigurationType configurationType = new ConfigurationType();
        Project project = anActionEvent.getProject();
        CyBenchConfiguration configuration = new CyBenchConfiguration("All CyBench Bechmarks", project, configurationType.getFactory() );
        //List<RunnerAndConfigurationSettings> configurationSettings = RunManager.getInstance(anActionEvent.getProject()).getConfigurationSettingsList().stream().filter();

        //RunnerAndConfigurationSettings conf = configurationSettings.get(0);

        RunnerAndConfigurationSettings all_project_benchmarks = RunManager.getInstance(project).createConfiguration("All project benchmarks", ConfigurationType.class);
        RunConfiguration configuration1 = all_project_benchmarks.getConfiguration();
        if (configuration1 instanceof CyBenchConfiguration) {
            CyBenchConfiguration configuration11 = (CyBenchConfiguration) configuration1;
            configuration11.getValueStore().put(CyBenchConfigurableParameters.BENCHMARK_CLASS, "");
            ClassConfigurationProducer.setupDefaultValues(configuration11);
            configuration11.setWorkingDirectory(project.getBasePath());
            Module[] modules = ModuleManager.getInstance(anActionEvent.getProject()).getModules(); //TODO select module from context
            configuration11.setModule(modules[0]);
            boolean edit_configuration = RunDialog.editConfiguration(project, all_project_benchmarks, "Edit configuration");
            if (edit_configuration) {
                ProgramRunnerUtil.executeConfiguration(all_project_benchmarks,DefaultRunExecutor.getRunExecutorInstance());

            }

        }



    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }
}
