package com.gocypher.cybench;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            JavaRunConfigurationModule configurationModule = ((CyBenchConfiguration) RunManager.getInstance(project).getConfigurationTemplate(all_project_benchmarks.getFactory()).getConfiguration()).getConfigurationModule();
            configuration11.setModule((com.intellij.openapi.module.Module) configurationModule);
        }

        ProgramRunnerUtil.executeConfiguration(all_project_benchmarks,DefaultRunExecutor.getRunExecutorInstance());


    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
    }
}
