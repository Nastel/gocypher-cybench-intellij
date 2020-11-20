package com.gocypher.cybench.generate;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

import java.util.Arrays;
import java.util.List;

public class CBGenerateAnActionTest extends LightJavaCodeInsightFixtureTestCase {

        public static final Language JAVA = Language.findLanguageByID("java");



    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testCompletion() {
        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.simple");
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertTrue(strings.containsAll(Arrays.asList("key with spaces", "language", "message", "tab", "website")));
        assertEquals(5, strings.size());
    }

    public void testParseJavaFile() throws Exception {
        myFixture.configureByFiles("CompleteTestData.java", "DefaultTestData.simple");
        PsiFile file = getFile();
        PsiClass[] childrenOfType = PsiTreeUtil.getChildrenOfType(file, PsiClass.class);
CBGenerateAnAction.project=getProject();

        PsiClass psiClass = CBGenerateAnAction.generateClassAndMethods(childrenOfType[0],  file.getParent());
        System.out.println(psiClass);

    }
}



