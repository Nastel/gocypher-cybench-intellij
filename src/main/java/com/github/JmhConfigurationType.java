package com.github;

import com.gocypher.cybench.CyBenchIcons;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;

/**
 * User: nikart
 * Date: 01/05/14
 * Time: 13:46
 */
public class JmhConfigurationType extends ConfigurationTypeBase {

    public static final String TYPE_ID = "cybench-id";

    public JmhConfigurationType() {
        super(TYPE_ID, "CyBench benchmark", "", CyBenchIcons.cyBenchSmall);
        ConfigurationFactory myFactory = new ConfigurationFactory(this) {
            public RunConfiguration createTemplateConfiguration(Project project) {

                JmhConfiguration configuration = new JmhConfiguration("cybench-configuration", project, this);
                configuration.setPassParentEnvs(true);
                return configuration;
            }
        };
        addFactory(myFactory);
    }
}
