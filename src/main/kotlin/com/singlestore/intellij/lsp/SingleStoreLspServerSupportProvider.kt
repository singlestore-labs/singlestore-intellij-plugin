package com.singlestore.intellij.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider

class SingleStoreLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (!file.name.endsWith(".s2db.sql", ignoreCase = true)) {
            return
        }

        serverStarter.ensureServerStarted(SingleStoreLspServerDescriptor(project))
    }
}
