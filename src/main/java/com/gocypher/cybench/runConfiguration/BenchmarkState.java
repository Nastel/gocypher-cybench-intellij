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

import static com.gocypher.cybench.launcher.utils.Constants.CYB_REPORT_CYB_FILE;
import static com.gocypher.cybench.launcher.utils.Constants.CYB_REPORT_JSON_FILE;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.compiler.ProcessorConfigProfile;

import com.gocypher.cybench.launcher.utils.Constants;
import com.gocypher.cybench.utils.Utils;
import com.intellij.compiler.CompilerConfiguration;
import com.intellij.compiler.CompilerConfigurationImpl;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaCommandLineStateUtil;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

public class BenchmarkState extends CommandLineState {

    public static final FileFilter PLUGINS_JAR_FILTER = new WildcardFileFilter("*.jar", IOCase.INSENSITIVE) {
        private static final long serialVersionUID = 1404356016052964390L;

        @Override
        public boolean accept(File file) {
            if (file.getName().startsWith("gocypher-cybench-intellij")) {
                return false;
            }
            return super.accept(file);
        }
    };
    private final Project project;
    private final CyBenchConfiguration configuration;
    private CyBenchResultTreeConsoleView cyBenchResultTreeConsoleView;

    public BenchmarkState(Project project, CyBenchConfiguration configuration, ExecutionEnvironment environment) {
        super(environment);
        this.project = project;
        this.configuration = configuration;
        Module module = configuration.getConfigurationModule().getModule();
        if (module != null) {
            CompilerConfigurationImpl compilerConfiguration = (CompilerConfigurationImpl) CompilerConfiguration
                    .getInstance(module.getProject());
            ProcessorConfigProfile processorConfigProfile = compilerConfiguration
                    .getAnnotationProcessingConfiguration(module);
            processorConfigProfile.setEnabled(true);
            compilerConfiguration.getState();
        }
        cyBenchResultTreeConsoleView = new CyBenchResultTreeConsoleView(project);

    }

    protected JavaParameters createJavaParameters() throws ExecutionException {

        JavaParameters parameters = new JavaParameters();
        JavaParametersUtil.configureConfiguration(parameters, configuration);

        parameters.setMainClass(CyBenchConfiguration.JMH_START_CLASS);

        JavaParametersUtil.configureModule(configuration.getConfigurationModule(), parameters,
                JavaParameters.JDK_AND_CLASSES_AND_TESTS, null);

        File[] pluginJars = null;
        pluginJars = Utils.getJMHLibFiles();

        parameters.getClassPath().addAllFiles(pluginJars);

        return parameters;
    }

    @Override
    public TextConsoleBuilder getConsoleBuilder() {
        return super.getConsoleBuilder();
    }

    @Override
    public void setConsoleBuilder(TextConsoleBuilder consoleBuilder) {
        super.setConsoleBuilder(consoleBuilder);
    }

    @NotNull
    @Override
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner)
            throws ExecutionException {
        return super.execute(executor, runner);
    }

    @Nullable
    @Override
    protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {

        ConsoleView console = cyBenchResultTreeConsoleView;
        return console;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        OSProcessHandler osProcessHandler = JavaCommandLineStateUtil.startProcess(createCommandLine(), false);
        osProcessHandler.addProcessListener(new CyBenchMessageHandler(cyBenchResultTreeConsoleView));
        return osProcessHandler;
    }

    private GeneralCommandLine createCommandLine() throws ExecutionException {
        JavaParameters javaParameters = createJavaParameters();

        javaParameters
                .setUseDynamicClasspath(CommonDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext()));
        for (Map.Entry<CyBenchConfigurableParameters, Object> confEntry : configuration.getValueStore().entrySet()) {
            javaParameters.getVMParametersList().add("-D" + confEntry.getKey().key + "=" + confEntry.getValue());

        }
        String reportFileName = getReportFileName(
                String.valueOf(configuration.getValueStore().get(CyBenchConfigurableParameters.REPORT_NAME)));
        javaParameters.getVMParametersList().add(getReportFNameParameter(reportFileName));
        cyBenchResultTreeConsoleView.setReportFile(reportFileName);
        javaParameters.getVMParametersList().add(getReportCybNameParameter(reportFileName.replace(".cybench", ".cyb")));
        javaParameters.getVMParametersList().add("-D" + Constants.APPEND_SCORE_TO_FNAME + "=" + Boolean.TRUE);

        GeneralCommandLine fromJavaParameters = javaParameters.toCommandLine();

        return fromJavaParameters;
    }

    @NotNull
    private String getReportFileName(String benchmarkName) {
        benchmarkName = benchmarkName.replaceAll(" ", "_");
        String reportFileName = benchmarkName + "-" + System.currentTimeMillis() + ".cybench";
        return reportFileName;
    }

    private String getReportFNameParameter(String reportFileName) {
        String key = CYB_REPORT_JSON_FILE;
        return "-D" + key + "=" + reportFileName;

    }

    private String getReportCybNameParameter(String reportFileName) {
        String key = CYB_REPORT_CYB_FILE;
        return "-D" + key + "=" + reportFileName;

    }

}
