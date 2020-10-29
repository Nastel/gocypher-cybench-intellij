package com.gocypher.cybench;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;


public abstract class ConfigurationProducer extends JavaRunConfigurationProducerBase<CyBenchConfiguration> implements Cloneable {

    public ConfigurationProducer(com.intellij.execution.configurations.ConfigurationType configurationType) {
        super(configurationType);
    }

    public ConfigurationProducer() {
        super(ContainerUtil.findInstance(
                Extensions.getExtensions(com.intellij.execution.configurations.ConfigurationType.CONFIGURATION_TYPE_EP), ConfigurationType.class));
    }

    @Override
    protected abstract boolean setupConfigurationFromContext(CyBenchConfiguration configuration, ConfigurationContext context,
                                                             Ref<PsiElement> sourceElement);

    @Override
    public abstract boolean isConfigurationFromContext(CyBenchConfiguration jmhConfiguration, ConfigurationContext configurationContext);

    String createProgramParameters(String generatedParams, String defaultParams) {
        return defaultParams != null ? generatedParams + " " + defaultParams : generatedParams;
    }
}
