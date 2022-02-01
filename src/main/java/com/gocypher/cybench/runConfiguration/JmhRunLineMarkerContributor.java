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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gocypher.cybench.utils.CyBenchIcons;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;

public class JmhRunLineMarkerContributor extends RunLineMarkerContributor {
    @Nullable
    @Override
    public RunLineMarkerContributor.Info getInfo(@NotNull PsiElement psiElement) {
        boolean isBenchmarkMethod = ConfigurationUtils.isBenchmarkMethod(psiElement);
        if (isBenchmarkMethod) {
            AnAction[] actions = ExecutorAction.getActions(0);
            return new Info(CyBenchIcons.cyBenchRun, new TooltipProvider(actions), actions);
        }

        boolean isBenchmarkClass = ConfigurationUtils.isBenchmarkClass(psiElement);
        if (isBenchmarkClass) {
            AnAction[] actions = ExecutorAction.getActions(0);
            return new Info(CyBenchIcons.cyBenchRun, new TooltipProvider(actions), actions);
        }

        return null;
    }

    private static class TooltipProvider implements com.intellij.util.Function<PsiElement, String> {
        private final AnAction[] actions;

        private TooltipProvider(AnAction[] actions) {
            this.actions = actions;
        }

        @Override
        public String fun(PsiElement element) {
            return StringUtil.join(ContainerUtil.mapNotNull(actions, new Function<AnAction, String>() {
                @Override
                public String fun(AnAction action) {
                    return "cybench" + getText(action, element);
                }
            }), "\n");
        }
    }
}
