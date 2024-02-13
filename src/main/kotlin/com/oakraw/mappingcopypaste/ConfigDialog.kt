package com.oakraw.mappingcopypaste

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.ui.components.JBScrollPane
import javax.swing.JTextArea

class ConfigDialog : DialogWrapper(true) {
    private val textArea = JTextArea(20, 50)

    init {
        init()
        title = "Mapping Copy Paste Configure"
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel()
        textArea.text = PluginSettings.getInstance().mappings

        val scrollPane = JBScrollPane(textArea)
        panel.add(scrollPane)

        return panel
    }

    override fun doOKAction() {
        val settings = PluginSettings.getInstance()
        settings.mappings = textArea.text
        super.doOKAction()
    }
}
