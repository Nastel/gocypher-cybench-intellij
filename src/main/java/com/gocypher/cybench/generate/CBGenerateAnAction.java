package com.gocypher.cybench.generate;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.CreateFromUsageUtils;
import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.codeInsight.generation.GenerateMembersUtil;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.PackageUtil;
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
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import com.intellij.testIntegration.TestIntegrationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.concurrency.Promise;
import org.jetbrains.concurrency.Promises;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;


public class CBGenerateAnAction extends AnAction {

    static Project project = null;
    static PsiFile file = null;
    static Editor editor = null;

    private static PsiClass generateClassAndMethods(PsiClass psiClass, PsiDirectory parent) throws Exception {


        FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Class.java");


        PsiClass created = (PsiClass) FileTemplateUtil.createFromTemplate(codeTemplate, psiClass.getName() + "Benchmark", new Properties(), parent);


        PsiJavaFile createdFile = PsiTreeUtil.getParentOfType(created, PsiJavaFile.class);

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
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.BenchmarkMode(org.openjdk.jmh.annotations.Mode.Throughput)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)");
        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    public static PsiDirectory getTestRoot(Module module) {
        List<SourceFolder> sourceFolders = ModuleRootManager.getInstance(module).getContentEntries()[0].getSourceFolders(JavaSourceRootType.TEST_SOURCE);
        try {
            VirtualFile directories = VfsUtil.createDirectories(VfsUtilCore.urlToPath(sourceFolders.get(0).getUrl()));
            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(directories);
            return directory;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

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

        // FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Method.java");

        PsiJavaFile psiFile = PsiTreeUtil.getParentOfType(element, PsiJavaFile.class, false);

        PsiDirectory psiDirectory = MoveClassesOrPackagesUtil.chooseDestinationPackage(project, psiFile.getPackageName(), getTestRoot(module));

        checkForLibraries(module);

        PostprocessReformattingAspect.getInstance(project).postponeFormattingInside(
                () -> WriteCommandAction.runWriteCommandAction(project, (Computable<PsiElement>) () -> {
                    try {
                        return generateClassAndMethods(psiClass, psiDirectory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }));
    }

    private void checkForLibraries(Module module) {
        GlobalSearchScope scope = GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module);
        PsiClass c = JavaPsiFacade.getInstance(module.getProject()).findClass("org.openjdk.jmh.annotations.Benchmark", scope);

        if (c == null) {
            int libraries_not_found = Messages
                    .showOkCancelDialog(module.getProject(), "Libraries not found", CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            if (libraries_not_found == Messages.OK) {
                setupLibrary(module);
            }
        }
    }

    public void setupLibrary(Module module) {
        ExternalLibraryDescriptor core = new ExternalLibraryDescriptor("org.openjdk.jmh",
                "jmh-core", "1.21", "1.21");
        ExternalLibraryDescriptor aProcessor = new ExternalLibraryDescriptor("org.openjdk.jmh",
                "jmh-generator-annprocess", "1.21", "1.21");

        JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, core, DependencyScope.TEST);
        JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, aProcessor, DependencyScope.TEST);
    }
}
