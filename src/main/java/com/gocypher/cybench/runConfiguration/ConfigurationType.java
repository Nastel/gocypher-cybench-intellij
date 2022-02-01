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

import com.gocypher.cybench.utils.CyBenchIcons;
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
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {

                CyBenchConfiguration configuration = new CyBenchConfiguration("cybench-configuration", project, this);
                configuration.setPassParentEnvs(true);
                return configuration;
            }
        };
        addFactory(myFactory);
    }
}
