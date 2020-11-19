package com.gocypher.cybench.generate;

import com.gocypher.cybench.utils.Utils;
import com.intellij.CommonBundle;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.module.Module;
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
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.refactoring.move.moveClassesOrPackages.MoveClassesOrPackagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class CBGenerateAnAction extends AnAction {

    static Project project = null;
    static PsiFile file = null;
    static Editor editor = null;

    private static PsiClass generateClassAndMethods(PsiClass psiClass, PsiDirectory parent) throws Exception {


        String benchmarkFileName = psiClass.getName() + "Benchmark";

        VirtualFile file = parent.getVirtualFile().findChild(benchmarkFileName + ".java");

        PsiClass created;
        if (file == null) {
            FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Class.java");
            created = (PsiClass) FileTemplateUtil.createFromTemplate(codeTemplate, benchmarkFileName, new Properties(), parent);
            PsiUtil.setModifierProperty(created, PsiModifier.PUBLIC, true);
            created.getModifierList().addAnnotation("org.openjdk.jmh.annotations.State(org.openjdk.jmh.annotations.Scope.Benchmark)");
        } else {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            created = PsiTreeUtil.getChildrenOfType(psiFile, PsiClass.class)[0];
        }


        PsiJavaFile createdFile = PsiTreeUtil.getParentOfType(created, PsiJavaFile.class);

        Editor editor = CodeInsightUtil.positionCursorAtLBrace(project, createdFile, created);

        JVMElementFactory factory = JVMElementFactories.getFactory(psiClass.getLanguage(), project);


        generateSetUpWithAnnotations(created, factory);
        generateTearDownWithAnnotations(created, factory);


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
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void " + name + "(org.openjdk.jmh.infra.Blackhole bh){}", created);
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Benchmark");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.BenchmarkMode(org.openjdk.jmh.annotations.Mode.Throughput)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)");
        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateSetUpWithAnnotations(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void setup(){}", created);

        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Setup");

        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateTearDownWithAnnotations(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void teardown(){}", created);
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.TearDown");
        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    public static PsiDirectory getTestRoot(Module module) {
        List<SourceFolder> sourceFolders = ModuleRootManager.getInstance(module).getContentEntries()[0].getSourceFolders(JavaSourceRootType.TEST_SOURCE);
        if (sourceFolders.size() == 0) {
            sourceFolders = ModuleRootManager.getInstance(module).getContentEntries()[0].getSourceFolders(JavaSourceRootType.SOURCE);
        }
        try {
            SourceFolder sourceFolder = sourceFolders.get(0);
            VirtualFile directories = VfsUtil.createDirectories(VfsUtilCore.urlToPath(sourceFolder.getUrl()));
            PsiDirectory directory = PsiManager.getInstance(project).findDirectory(directories);
            return directory;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiElement data = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (data instanceof PsiJavaFile) {
            e.getPresentation().setEnabled(true);
        } else {
            e.getPresentation().setEnabled(false);
        }

    }

    @Override
    public boolean isDumbAware() {
        return true;
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
                    .showOkCancelDialog(module.getProject(), "Libraries not found. Add?", CommonBundle.getErrorTitle(), Messages.getErrorIcon());
            if (libraries_not_found == Messages.OK) {
                setupLibrary(module);
            }
        }
    }

    public void setupLibrary(Module module) {

        if (isMavenizedModule(module)) {
            ExternalLibraryDescriptor core = new ExternalLibraryDescriptor("org.openjdk.jmh",
                    "jmh-core", "1.26", "1.26");
            ExternalLibraryDescriptor aProcessor = new ExternalLibraryDescriptor("org.openjdk.jmh",
                    "jmh-generator-annprocess", "1.26", "1.26");

            JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, core, DependencyScope.TEST);
            JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, aProcessor, DependencyScope.TEST);
        } else {

            OrderEntryFix.addJarsToRoots(Arrays.asList(Utils.getJMHLibFiles()).stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()), null, module, null);
        }

    }

    private boolean isMavenizedModule(Module module) {
        try {
            Class.forName("org.jetbrains.idea.maven.project.MavenProjectsManager");
            return org.jetbrains.idea.maven.project.MavenProjectsManager.getInstance(project).isMavenizedModule(module);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
