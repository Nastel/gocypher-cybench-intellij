package com.github;

import com.intellij.compiler.CompilerConfiguration;
import com.intellij.compiler.CompilerConfigurationImpl;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.util.JavaParametersUtil;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.compiler.ProcessorConfigProfile;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: nikart
 * Date: 14/07/14
 * Time: 21:36
 */
public class BenchmarkState extends CommandLineState {

    public static final FileFilter PLUGINS_JAR_FILTER = new WildcardFileFilter("*.jar", IOCase.INSENSITIVE) {
        @Override
        public boolean accept(File file) {
            if ( file.getName().startsWith("CyBench-Intellij") ) return false;
            return super.accept(file);
        }
    };
    private final Project project;
    private final JmhConfiguration configuration;

    public BenchmarkState(Project project, JmhConfiguration configuration, ExecutionEnvironment environment) {
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
    }

    protected JavaParameters createJavaParameters() throws ExecutionException {

        JavaParameters parameters = new JavaParameters();
        JavaParametersUtil.configureConfiguration(parameters, configuration);

        parameters.setMainClass(JmhConfiguration.JMH_START_CLASS);

        JavaParametersUtil.configureModule(configuration.getConfigurationModule(), parameters, JavaParameters.JDK_AND_CLASSES_AND_TESTS, null);

        File pluginHome = PluginPathManager.getPluginHome("Cybench-Intellij");
        File lib = new File(pluginHome, "lib");

        File[] pluginJars = null;
        if (lib.exists()) {
            pluginJars = lib.listFiles(PLUGINS_JAR_FILTER);
        } else {
            pluginJars = getPluginClasspathHackyWay();
        }


        parameters.getClassPath().addAllFiles(pluginJars);


        return parameters;
    }

    private File[] getPluginClasspathHackyWay() {
        try {

            PluginClassLoader pluginClassLoader = (PluginClassLoader) this.getClass().getClassLoader();
            Field myLibDirectories = PluginClassLoader.class.getDeclaredField("myLibDirectories");
            myLibDirectories.setAccessible(true);
            List<String> libDirs = (List<String>) myLibDirectories.get(pluginClassLoader);
            List<File> result = new ArrayList<>();
            for (String libDir : libDirs) {
                File file = new File(libDir);
                if (file.exists()) {
                    File[] files = file.listFiles(PLUGINS_JAR_FILTER);
                    List<File> c = Arrays.asList(files);
                    result.addAll(c);
                }
            }
            return result.toArray(new File[result.size()]);

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public TextConsoleBuilder getConsoleBuilder() {
        return super.getConsoleBuilder();
    }

    @Nullable
    @Override
    protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {
        ConsoleView console = super.createConsole(executor);
        return console;
    }

    @NotNull
    @Override
    protected ProcessHandler startProcess() throws ExecutionException {
        OSProcessHandler osProcessHandler = JavaCommandLineStateUtil.startProcess(createCommandLine(), false);

        return osProcessHandler;
    }

    private GeneralCommandLine createCommandLine() throws ExecutionException {
        JavaParameters javaParameters = createJavaParameters();

        javaParameters.setUseDynamicClasspath(CommonDataKeys.PROJECT
                .getData(DataManager.getInstance().getDataContext()));
        GeneralCommandLine fromJavaParameters = javaParameters.toCommandLine();

        final NotificationGroup NOTIFICATION_GROUP =
                new NotificationGroup("Groovy DSL errors", NotificationDisplayType.BALLOON, true);
        Notification notification = NOTIFICATION_GROUP.createNotification(fromJavaParameters.getCommandLineString(), NotificationType.ERROR);
        notification.notify(project);

        return fromJavaParameters;
    }

}
