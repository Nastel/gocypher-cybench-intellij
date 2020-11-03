package com.gocypher.cybench;

import com.intellij.execution.JavaExecutionUtil;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.Iterator;

public class ClassConfigurationProducer extends JavaRunConfigurationProducerBase<CyBenchConfiguration> implements Cloneable {


    public ClassConfigurationProducer(com.intellij.execution.configurations.ConfigurationType configurationType) {
        super(configurationType);
    }

    public ClassConfigurationProducer() {
        super(ContainerUtil.findInstance(
                Extensions.getExtensions(com.intellij.execution.configurations.ConfigurationType.CONFIGURATION_TYPE_EP), ConfigurationType.class));
    }

    @Override
    protected boolean setupConfigurationFromContext(CyBenchConfiguration configuration, ConfigurationContext context,
                                                    Ref<PsiElement> sourceElement) {
        PsiClass benchmarkClass = getBenchmarkClass(context);
        if (benchmarkClass == null) {
            return false;
        }
        configuration.getValueStore().put(CyBenchConfigurableParameters.BENCHMARK_CLASS, benchmarkClass.getQualifiedName());

        setupDefaultValues(configuration);

        sourceElement.set(benchmarkClass);
        setupConfigurationModule(context, configuration);
        final Module originalModule = configuration.getConfigurationModule().getModule();
        configuration.restoreOriginalModule(originalModule);

        configuration.setWorkingDirectory(PathUtil.getLocalPath(context.getProject().getBaseDir()));
        configuration.setName(benchmarkClass.getName());

        return true;
    }

    public static void setupDefaultValues(CyBenchConfiguration configuration) {
        for (CyBenchConfigurableParameters parameter : CyBenchConfigurableParameters.values()) {
            if (parameter.equals(CyBenchConfigurableParameters.BENCHMARK_CLASS)) continue;
            configuration.getValueStore().put(parameter, parameter.defaultValue);
        }
    }

    @Override
    public boolean isConfigurationFromContext(CyBenchConfiguration configuration, ConfigurationContext context) {
        if (ConfigurationUtils.getAnnotatedMethod(context) != null) {
            return false;
        }

        PsiClass benchmarkClass = getBenchmarkClass(context);
        if (benchmarkClass == null || benchmarkClass.getQualifiedName() == null ||
                !benchmarkClass.getQualifiedName().equals(configuration.getValueStore().get(CyBenchConfigurableParameters.BENCHMARK_CLASS))) {
            return false;
        }
        String nameFromContext = benchmarkClass.getName();
        if (configuration.getName() == null || !configuration.getName().equals(nameFromContext)) {
            return false;
        }
        Location locationFromContext = context.getLocation();
        if (locationFromContext == null) {
            return false;
        }
        Location location = JavaExecutionUtil.stepIntoSingleClass(locationFromContext);
        final Module originalModule = configuration.getConfigurationModule().getModule();
        if (location.getModule() == null || !location.getModule().equals(originalModule)) {
            return false;
        }
        setupConfigurationModule(context, configuration);
        configuration.restoreOriginalModule(originalModule);

        return true;
    }

    private PsiClass getBenchmarkClass(ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location == null) {
            return null;
        }
        for (Iterator<Location<PsiClass>> iterator = location.getAncestors(PsiClass.class, false); iterator.hasNext(); ) {
            final Location<PsiClass> classLocation = iterator.next();
            if (hasBenchmarks(classLocation.getPsiElement())) {
                return classLocation.getPsiElement();
            }
        }
        return null;
    }

    private boolean hasBenchmarks(PsiClass psiClass) {
        for (PsiMethod method : psiClass.getMethods()) {
            if (ConfigurationUtils.hasBenchmarkAnnotation(method)) {
                return true;
            }
        }
        return false;
    }
}
