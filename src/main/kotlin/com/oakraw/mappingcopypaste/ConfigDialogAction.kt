package com.oakraw.mappingcopypaste

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ConfigDialogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dialog = ConfigDialog()
        dialog.show()
    }
}