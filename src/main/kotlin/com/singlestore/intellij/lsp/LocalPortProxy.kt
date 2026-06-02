package com.singlestore.intellij.lsp

import java.io.Closeable

internal interface LocalPortProxy : Closeable {
    val localPort: Int
}
