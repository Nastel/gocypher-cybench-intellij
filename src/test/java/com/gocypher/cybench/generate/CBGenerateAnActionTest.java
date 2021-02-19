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

package com.gocypher.cybench.generate;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.Language;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class CBGenerateAnActionTest extends LightJavaCodeInsightFixtureTestCase {

        public static final Language JAVA = Language.findLanguageByID("java");



    protected String getTestDataPath() {
        return "src/test/testData";
    }

//    public void testCompletion() {
//        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.simple");
//        myFixture.complete(CompletionType.BASIC, 1);
//        List<String> strings = myFixture.getLookupElementStrings();
//        assertTrue(strings.containsAll(Arrays.asList("key with spaces", "language", "message", "tab", "website")));
//        assertEquals(5, strings.size());
//    }

//    public void testParseJavaFile() throws Exception {
//        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.simple");
//        PsiFile file = getFile();
//        PsiClass[] psiClass = PsiTreeUtil.getChildrenOfType(file, PsiClass.class);
//        PsiPackage[] psiPackage = PsiTreeUtil.getChildrenOfType(file, PsiPackage.class);
//        CBGenerateAnAction.project=getProject();
//
//
//        PsiElement psiElement = WriteCommandAction.runWriteCommandAction(getProject(), (Computable<PsiClass>) () -> {
//            try {
//                return CBGenerateAnAction.generateClassAndMethods(new CBGenerateDialog(myFixture.getProject(), psiClass[0], psiPackage[0], myFixture.getModule()),  file.getParent());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        });
//        CodeStyleManager.getInstance(getProject()).reformatText(psiElement.getContainingFile(), 0, 100);
//        System.out.println(psiElement);
//
//    }

    @Test
    public void testAddGradleDependency() throws IOException {
        InputStream resourceAsStream = CBGenerateAnActionTest.class.getResourceAsStream("build.gradle");

        //File tempDir = Files.createTempDir();
        File tempFile = File.createTempFile("", "");

        Files.copy(resourceAsStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        CBGenerateAnAction.addGradleDependency(tempFile.getParent());
        tempFile.delete();

    }
}



