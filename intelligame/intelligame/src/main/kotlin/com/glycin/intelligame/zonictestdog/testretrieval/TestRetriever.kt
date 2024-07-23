package com.glycin.intelligame.zonictestdog.testretrieval

import com.intellij.codeInsight.TestFrameworks
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AllClassesSearch

class TestRetriever(
    private val project: Project,
) {

    fun getAllTestMethods(): List<PsiMethod> {
        val methods = mutableListOf<PsiMethod>()
        val searchScope = GlobalSearchScope.projectScope(project)
        AllClassesSearch.search(searchScope, project)
            .findAll()
            .filter { psiC ->
                val qn = psiC.qualifiedName ?: return@filter false
                !((qn.startsWith("java")) || qn.startsWith("sun.") ||
                        qn.startsWith("jdk.") || qn.startsWith("com.sun") ||
                        qn.startsWith("net.bytebuddy"))
            }.forEach { psiClass->
                TestFrameworks.detectFramework(psiClass)?.let { framework ->
                    if(framework.isTestClass(psiClass)){
                        psiClass.methods.forEach { method ->
                            if(framework.isTestMethod(method)){
                                methods.add(method)
                            }
                        }
                    }
            }
        }
        return methods
    }
}