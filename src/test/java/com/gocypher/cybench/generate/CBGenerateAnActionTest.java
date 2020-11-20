package com.gocypher.cybench.generate;

import com.intellij.lang.java.JavaParserDefinition;
import com.intellij.testFramework.ParsingTestCase;

public class CBGenerateAnActionTest extends ParsingTestCase {

    //    public static final Language JAVA = Language.findLanguageByID("java");
//
//    @Test
//    public void testParseJavaFile() {
//        VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath("CBGenerateAnActionTest.java");
//        FileViewProvider fileViewProvider = (new ClassFileViewProviderFactory()).createFileViewProvider(fileByPath, JAVA, PsiManager.getInstance(getProject()), false);
//        PsiFile file = LanguageParserDefinitions.INSTANCE.forLanguage(JAVA).createFile(fileViewProvider);
//        System.out.println(file);
//
//    }
    public CBGenerateAnActionTest() {
        super("", "simple", new JavaParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }


}

