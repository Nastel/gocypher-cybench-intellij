package com.gocypher.cybench.runConfiguration;

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
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.compiler.ProcessorConfigProfile;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;


public class BenchmarkState extends CommandLineState {

    public static final FileFilter PLUGINS_JAR_FILTER = new WildcardFileFilter("*.jar", IOCase.INSENSITIVE) {
        @Override
        public boolean accept(File file) {
            if (file.getName().startsWith("CyBench-Intellij")) return false;
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
            CompilerConfigurationImpl compilerConfiguration =
                    (CompilerConfigurationImpl) CompilerConfiguration.getInstance(module.getProject());
            ProcessorConfigProfile processorConfigProfile = compilerConfiguration.getAnnotationProcessingConfiguration(module);
            processorConfigProfile.setEnabled(true);
            compilerConfiguration.getState();
        }
        cyBenchResultTreeConsoleView = new CyBenchResultTreeConsoleView(project);

    }

    protected JavaParameters createJavaParameters() throws ExecutionException {

        JavaParameters parameters = new JavaParameters();
        JavaParametersUtil.configureConfiguration(parameters, configuration);

        parameters.setMainClass(CyBenchConfiguration.JMH_START_CLASS);

        JavaParametersUtil.configureModule(configuration.getConfigurationModule(), parameters, JavaParameters.JDK_AND_CLASSES_AND_TESTS, null);


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
    public ExecutionResult execute(@NotNull Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
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

        javaParameters.setUseDynamicClasspath(CommonDataKeys.PROJECT
                .getData(DataManager.getInstance().getDataContext()));
        for (Map.Entry<CyBenchConfigurableParameters, Object> confEntry : this.configuration.getValueStore().entrySet()) {
            javaParameters.getVMParametersList().add("-D" + confEntry.getKey().key + "=" + String.valueOf(confEntry.getValue()));

        }
        String reportFileName = getReportFileName(String.valueOf(configuration.getValueStore().get(CyBenchConfigurableParameters.REPORT_NAME)));
        javaParameters.getVMParametersList().add(getReportFNameParameter(reportFileName));
        javaParameters.getVMParametersList().add("-D" + Constants.APPEND_SCORE_TO_FNAME + "=" + Boolean.TRUE);

        cyBenchResultTreeConsoleView.setReportFile(reportFileName);
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
        String key = "report.json";
        return "-D" + key + "=" + reportFileName;

    }

}
