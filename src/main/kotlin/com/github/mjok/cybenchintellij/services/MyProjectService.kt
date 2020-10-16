package com.github.mjok.cybenchintellij.services

import com.intellij.openapi.project.Project
import com.github.mjok.cybenchintellij.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
