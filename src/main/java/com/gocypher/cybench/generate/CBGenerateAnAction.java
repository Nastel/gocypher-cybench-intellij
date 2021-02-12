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

import com.gocypher.cybench.utils.Utils;
import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.codeInsight.daemon.impl.quickfix.OrderEntryFix;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.ex.IdeDocumentHistory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbService;
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
import com.intellij.refactoring.util.classMembers.MemberInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.picocontainer.ComponentAdapter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class CBGenerateAnAction extends DumbAwareAction {

    static Project project = null;
    static PsiFile file = null;
    static Editor editor = null;

    protected static PsiClass generateClassAndMethods(CBGenerateDialog options, PsiDirectory parent) throws Exception {


        String benchmarkFileName = options.getClassName();
        JVMElementFactory factory = JVMElementFactories.getFactory(options.getTargetClass().getLanguage(), project);

        VirtualFile file = parent.getVirtualFile().findChild(benchmarkFileName + ".java");

        PsiClass created;
        if (file == null) {
            created = createPsiClass(parent, benchmarkFileName, factory);
            created.getModifierList().addAnnotation("org.openjdk.jmh.annotations.State(org.openjdk.jmh.annotations.Scope.Benchmark)");
        } else {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            created = PsiTreeUtil.getChildrenOfType(psiFile, PsiClass.class)[0];
        }

        PsiJavaFile createdFile = PsiTreeUtil.getParentOfType(created, PsiJavaFile.class);

        if (options.shouldGenerateSetup()) generateSetUpWithAnnotations(created, factory);
        if (options.shouldGenerateSetup()) generateSetUpWithAnnotationsIteration(created, factory);


        for (MemberInfo m : options.getSelectedMethods()) {
            if (m.getMember() instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) m.getMember();
                generateMethodWithAnnotations(psiMethod.getName() + "Benchmark", options.getMode(), created, factory, options.shouldGenerateBechmarkTag());
            }
        }

        if (options.shouldGenerateTearDown()) generateTearDownWithAnnotations(created, factory);
        if (options.shouldGenerateTearDown()) generateTearDownWithAnnotationsIteration(created, factory);

        JavaCodeStyleManager.getInstance(project).shortenClassReferences(created);


        PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        Document document = manager.getDocument(createdFile);
        manager.doPostponedOperationsAndUnblockDocument(document);

        Editor editor = CodeInsightUtil.positionCursorAtLBrace(project, createdFile, created);

        return created;
    }

    @NotNull
    private static PsiClass createPsiClass(PsiDirectory parent, String benchmarkFileName, JVMElementFactory factory) throws Exception {
        PsiClass created;
        //   FileTemplate codeTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate("Class.java");
        //  created = (PsiClass) FileTemplateUtil.createFromTemplate(codeTemplate, benchmarkFileName, new Properties(), parent);
        created = JavaDirectoryService.getInstance().createClass(parent, benchmarkFileName);
        //  created = factory.createClass(benchmarkFileName);
        parent.getVirtualFile().refresh(false, false);

        PsiUtil.setModifierProperty(created, PsiModifier.PUBLIC, true);


        return created;
    }

    @NotNull
    private static PsiMethod generateMethodWithAnnotations(String name, String mode, PsiClass created, JVMElementFactory factory, boolean addBenchmarkTag) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void " + name + "(org.openjdk.jmh.infra.Blackhole bh){}", created);

        if (addBenchmarkTag) {
            benchmarkMethod.getModifierList().addAnnotation("com.gocypher.cybench.core.annotation.BenchmarkTag(tag=\"" + UUID.randomUUID() + "\")");
        }
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.OutputTimeUnit(java.util.concurrent.TimeUnit.SECONDS)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Fork(1)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Threads(1)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.BenchmarkMode(org.openjdk.jmh.annotations." + mode + ")");
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Benchmark");

        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateSetUpWithAnnotations(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void setup(){}", created);

        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Setup(org.openjdk.jmh.annotations.Level.Trial)");

        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateSetUpWithAnnotationsIteration(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void setupIteration(){}", created);

        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.Setup(org.openjdk.jmh.annotations.Level.Iteration)");

        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateTearDownWithAnnotations(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void tearDown(){}", created);
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.TearDown(org.openjdk.jmh.annotations.Level.Trial)");
        PsiElement add = created.add(benchmarkMethod);

        return benchmarkMethod;
    }

    @NotNull
    private static PsiMethod generateTearDownWithAnnotationsIteration(PsiClass created, JVMElementFactory factory) {
        PsiMethod benchmarkMethod = factory.createMethodFromText("public void tearDownIteration(){}", created);
        benchmarkMethod.getModifierList().addAnnotation("org.openjdk.jmh.annotations.TearDown(org.openjdk.jmh.annotations.Level.Iteration)");
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

        CBGenerateDialog cbGenerateDialog = new CBGenerateDialog(project, psiClass, srcPackage, module);
        if (!cbGenerateDialog.showAndGet()) {
            return;
        }

        PsiJavaFile psiFile = PsiTreeUtil.getParentOfType(element, PsiJavaFile.class, false);

        PsiDirectory psiDirectory = MoveClassesOrPackagesUtil.chooseDestinationPackage(project, psiFile.getPackageName(), getTestRoot(module));

        checkForLibraries(module);


        DumbService.getInstance(project).runWhenSmart(() ->
                PostprocessReformattingAspect.getInstance(project).postponeFormattingInside(
                        () -> WriteCommandAction.runWriteCommandAction(project, (Computable<PsiElement>) () -> {
                            try {
                                return generateClassAndMethods(cbGenerateDialog, psiDirectory);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return null;
                        })));
    }

    private void checkForLibraries(Module module) {
        GlobalSearchScope scope = GlobalSearchScope.moduleRuntimeScope(module, true);
        PsiClass c = JavaPsiFacade.getInstance(module.getProject()).findClass("org.openjdk.jmh.annotations.Benchmark", scope);

        if (c == null) {
            int libraries_not_found = Messages
                    .showOkCancelDialog(module.getProject(), "Libraries not found. Add?", "Add libraries", "Add", "Cancel", Messages.getErrorIcon());
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
            ExternalLibraryDescriptor benchmarkTag = new ExternalLibraryDescriptor("com.gocypher.cybench.client",
                    "gocypher-cybench-annotations", "1.0.0", "1.0.0");

            JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, core, DependencyScope.TEST);
            JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, aProcessor, DependencyScope.TEST);
            JavaProjectModelModificationService.getInstance(module.getProject()).addDependency(module, benchmarkTag, DependencyScope.TEST);
        } else {

            OrderEntryFix.addJarsToRoots(Arrays.asList(Utils.getJMHLibFiles()).stream().map(f -> f.getAbsolutePath()).collect(Collectors.toList()), null, module, null);
        }

    }

    private boolean isMavenizedModule(Module module) {
        try {
            Object componentInstance = module.getPicoContainer().getComponentInstance(
                    "org.jetbrains.idea.maven.project.MavenProjectsManager");
            if (componentInstance != null) {
                ComponentAdapter componentAdapter = module.getPicoContainer().getComponentAdapter(
                        "org.jetbrains.idea.maven.project.MavenProjectsManager");
                Method isMavenizedModuleMethod = componentAdapter.getComponentImplementation().getMethod("isMavenizedModule", Module.class);
                Object result = isMavenizedModuleMethod.invoke(componentInstance, module);
                if (result instanceof Boolean) {
                    return (Boolean) result;
                } else return false;
            }
        } catch (Throwable e) {
            return false;
        }
        return false;
    }
}
