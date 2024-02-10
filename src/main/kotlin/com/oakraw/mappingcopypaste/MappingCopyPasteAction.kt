
package com.oakraw.mappingcopypaste

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.awt.datatransfer.DataFlavor

class MappingCopyPasteAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val settings = PluginSettings.getInstance()
        val mappings = parseMappings(settings.mappings)

        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val clipboard = CopyPasteManager.getInstance().contents
        val data = clipboard?.getTransferData(DataFlavor.stringFlavor) as? String ?: return

        val modifiedText =  applyMappings(data, mappings)

        WriteCommandAction.runWriteCommandAction(project) {
            val document = editor.document
            val selectionModel = editor.selectionModel
            val start = selectionModel.selectionStart
            val end = selectionModel.selectionEnd

            if (start == end) {
                document.insertString(start, modifiedText)
            } else {
                document.replaceString(start, end, modifiedText)
            }
            selectionModel.removeSelection()
        }
    }

    private fun parseMappings(jsonString: String): Map<String, String> {
        val jsonElement = Json.parseToJsonElement(jsonString)
        val jsonObject = jsonElement.jsonObject

        return jsonObject.mapValues { (_, value) ->
            value.jsonPrimitive.content
        }
    }

    private fun applyMappings(text: String, mappings: Map<String, String>): String {
        var modifiedText = text
        mappings.forEach { (original, converted) ->
            modifiedText = modifiedText.replace(original, converted)
        }
        return modifiedText
    }
}
