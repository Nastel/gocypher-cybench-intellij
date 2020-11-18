package com.gocypher.cybench.generate;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateFromUsageUtils;
import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testIntegration.TestIntegrationUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Properties;


public class CBGenerateAnAction extends AnAction {

    static Project project = null;
    static PsiFile file = null;
    static Editor editor = null;

    private static PsiClass generateClassAndMethods(PsiClass psiClass, FileTemplate codeTemplate, PsiDirectory parent) throws Exception {
        PsiClass created = (PsiClass) FileTemplateUtil.createFromTemplate(codeTemplate, psiClass.getName() + "Benchmark", new Properties(), parent);
        PsiFile createdFile = PsiTreeUtil.getParentOfType(created, PsiFile.class);
        Editor editor = CodeInsightUtil.positionCursorAtLBrace(project, createdFile, created);

        JVMElementFactory factory = JVMElementFactories.getFactory(psiClass.getLanguage(), project);


        Arrays.asList(psiClass.getMethods()).stream()
                .filter(m -> m.hasModifier(JvmModifier.PUBLIC))
                .forEach(m -> generateMethodWithAnnotations(m.getName() + "Benchmark", created, factory));


        JavaCodeStyleManager.getInstance(project).shortenClassReferences(created);


        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(createdFile);
        manager.doPostponedOperationsAndUnblockDocument(document);
        return created;
    }

    @NotNull
    private static PsiMethod generateMethodWithAnnotations(String name, PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void " + name + "(){}", created);
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Benchmark");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.BenchmarkMode(org.openjdk.jmh.annotations.Mode.SingleShotTime)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)");
        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        project = anActionEvent.getData(CommonDataKeys.PROJECT);
        file = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        editor = anActionEvent.getData(CommonDataKeys.EDITOR);


        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        final Module module = ModuleUtilCore.findModuleForPsiElement(element); //module info
        PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
        PsiDirectory srcDir = element.getContainingFile().getContainingDirectory();
        PsiPackage srcPackage = JavaDirectoryService.getInstance().getPackage(srcDir); //package info
        IdeDocumentHistory.getInstance(project).includeCurrentPlaceAsChangePlace();


        FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Class.java");
        // FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Method.java");

        PsiFile psiFile = PsiTreeUtil.getParentOfType(element, PsiFile.class, false);


        PsiDirectory parent = psiFile.getParent();
        PostprocessReformattingAspect.getInstance(project).postponeFormattingInside(
                () -> WriteCommandAction.runWriteCommandAction(project, (Computable<PsiElement>) () -> {
                    try {
                        return generateClassAndMethods(psiClass, codeTemplate, parent);


                        // created.add()

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }));
    }
}
