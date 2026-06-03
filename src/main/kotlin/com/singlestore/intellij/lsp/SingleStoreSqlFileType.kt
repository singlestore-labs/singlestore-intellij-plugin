package com.singlestore.intellij.lsp

import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object SingleStoreSqlLanguage : Language("SingleStore SQL")

class SingleStoreSqlFileType private constructor() : LanguageFileType(SingleStoreSqlLanguage) {

    override fun getName(): String = "SingleStore SQL"
    override fun getDescription(): String = "SingleStore SQL file"
    override fun getDefaultExtension(): String = "sql"
    override fun getIcon(): Icon = ICON

    companion object {
        @JvmField
        val INSTANCE = SingleStoreSqlFileType()

        // IconLoader auto-picks singlestore_dark.png in dark theme (no suffix needed for light).
        // @2x variants (singlestore@2x.png, singlestore_dark@2x.png) are used on HiDPI displays.
        val ICON: Icon = IconLoader.getIcon("/icons/singlestore.png", SingleStoreSqlFileType::class.java)
    }
}
