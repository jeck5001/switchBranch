package com.cascade.switchbranch

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.vcs.VcsNotifier
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JLabel
import java.awt.GridBagLayout
import java.awt.GridBagConstraints
import java.awt.Insets

class SwitchBranchAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        
        // 获取所有Git仓库
        val repositories = GitUtil.getRepositoryManager(project).repositories
        if (repositories.isEmpty()) {
            Messages.showErrorDialog(project, "没有找到Git仓库", "错误")
            return
        }
        
        // 获取所有分支
        val branches = getBranches(project, repositories.first())
        
        // 显示分支选择对话框
        val dialog = BranchSelectionDialog(project, branches)
        if (dialog.showAndGet()) {
            val selectedBranch = dialog.getSelectedBranch()
            // 在后台执行分支切换
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "正在切换分支", false) {
                override fun run(indicator: ProgressIndicator) {
                    indicator.isIndeterminate = false
                    val repoCount = repositories.size
                    
                    val errors = mutableListOf<String>()
                    repositories.forEachIndexed { index, repository ->
                        indicator.text = "正在切换仓库: ${repository.root.name}"
                        indicator.fraction = index.toDouble() / repoCount
                        
                        try {
                            val handler = GitLineHandler(project, repository.root, GitCommand.CHECKOUT)
                            handler.addParameters(selectedBranch)
                            
                            val result = Git.getInstance().runCommand(handler)
                            if (!result.success()) {
                                errors.add("${repository.root.name}: ${result.errorOutputAsJoinedString}")
                            }
                        } catch (e: Exception) {
                            errors.add("${repository.root.name}: ${e.message ?: "未知错误"}")
                        }
                    }
                    
                    if (errors.isEmpty()) {
                        VcsNotifier.getInstance(project).notifyInfo("分支切换", "成功切换到分支: $selectedBranch")
                    } else {
                        VcsNotifier.getInstance(project).notifyError("分支切换失败", errors.joinToString("\n"))
                    }
                }
            })
        }
    }
    
    private fun getBranches(project: Project, repository: GitRepository): List<String> {
        val handler = GitLineHandler(project, repository.root, GitCommand.BRANCH)
        handler.addParameters("-a")
        
        val result = Git.getInstance().runCommand(handler)
        return result.output
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { 
                if (it.startsWith("*")) it.substring(2) else it 
            }
            .filter { !it.contains("HEAD") && !it.contains("->") }
            .map {
                if (it.startsWith("remotes/origin/")) it.substring("remotes/origin/".length) else it
            }
            .distinct()
            .sorted()
    }
    
    override fun update(e: AnActionEvent) {
        val project = e.project
        e.presentation.isEnabled = project != null
    }
    
    private inner class BranchSelectionDialog(
        private val project: Project,
        branches: List<String>
    ) : DialogWrapper(project) {
        
        private val branchComboBox = ComboBox(branches.toTypedArray())
        
        init {
            title = "选择分支"
            init()
        }
        
        override fun createCenterPanel(): JComponent {
            val panel = JPanel(GridBagLayout())
            val gbc = GridBagConstraints()
            
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.anchor = GridBagConstraints.WEST
            gbc.insets = Insets(5, 5, 5, 5)
            panel.add(JLabel("请选择要切换的分支:"), gbc)
            
            gbc.gridx = 0
            gbc.gridy = 1
            gbc.fill = GridBagConstraints.HORIZONTAL
            gbc.weightx = 1.0
            panel.add(branchComboBox, gbc)
            
            return panel
        }
        
        fun getSelectedBranch(): String {
            return branchComboBox.selectedItem as String
        }
    }
} 