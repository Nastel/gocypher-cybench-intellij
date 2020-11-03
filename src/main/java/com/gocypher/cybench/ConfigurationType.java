package com.gocypher.cybench;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;


public class ConfigurationType extends ConfigurationTypeBase {

    public static final String TYPE_ID = "cybench-id";

    public ConfigurationFactory getFactory() {
        return myFactory;
    }

    private final ConfigurationFactory myFactory;

    public ConfigurationType() {
        super(TYPE_ID, "CyBench benchmark", "", CyBenchIcons.cyBenchSmall);
        myFactory = new ConfigurationFactory(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {

                CyBenchConfiguration configuration = new CyBenchConfiguration("cybench-configuration", project, this);
                configuration.setPassParentEnvs(true);
                return configuration;
            }
        };
        addFactory(myFactory);
    }
}
