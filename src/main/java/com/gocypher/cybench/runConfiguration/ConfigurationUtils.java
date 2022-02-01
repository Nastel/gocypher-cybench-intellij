
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

import java.util.Iterator;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;

public class ConfigurationUtils {

    public static final String SETUP_ANNOTATION = "org.openjdk.jmh.annotations.Setup";
    public static final String TEAR_DOWN_ANNOTATION = "org.openjdk.jmh.annotations.TearDown";

    public static boolean hasBenchmarkAnnotation(PsiMethod method) {
        return method.getModifierList().findAnnotation(CyBenchConfiguration.JMH_ANNOTATION_NAME) != null;
    }

    public static boolean hasSetupOrTearDownAnnotation(PsiMethod method) {
        return method.getModifierList().findAnnotation(SETUP_ANNOTATION) != null
                || method.getModifierList().findAnnotation(TEAR_DOWN_ANNOTATION) != null;
    }

    public static PsiMethod getAnnotatedMethod(ConfigurationContext context) {
        Location<?> location = context.getLocation();
        if (location == null) {
            return null;
        }
        Iterator<Location<PsiMethod>> iterator = location.getAncestors(PsiMethod.class, false);
        Location<PsiMethod> methodLocation = null;
        if (iterator.hasNext()) {
            methodLocation = iterator.next();
        }
        if (methodLocation == null) {
            return null;
        }
        PsiMethod method = methodLocation.getPsiElement();
        if (hasBenchmarkAnnotation(method)) {
            return method;
        }
        return null;
    }

    public static boolean isBenchmarkMethod(PsiElement element) {
        if (!(element instanceof PsiIdentifier)) {
            return false;
        }

        element = element.getParent();
        if (!(element instanceof PsiMethod)) {
            return false;
        }

        return isBenchmarkMethod((PsiMethod) element);
    }

    private static boolean isBenchmarkMethod(PsiMethod method) {
        return method.getContainingClass() != null && method.hasModifierProperty("public")
                && hasBenchmarkAnnotation(method);
    }

    public static boolean isBenchmarkClass(PsiElement psiElement) {
        if (!(psiElement instanceof PsiIdentifier)) {
            return false;
        }

        PsiElement element = psiElement.getParent();

        return element instanceof PsiClass && containsBenchmarkMethod((PsiClass) element);
    }

    private static boolean containsBenchmarkMethod(PsiClass aClass) {
        PsiMethod[] methods = aClass.getMethods();
        for (PsiMethod method : methods) {
            if (isBenchmarkMethod(method)) {
                return true;
            }
        }
        return false;
    }
}
