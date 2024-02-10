package com.oakraw.mappingcopypaste

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ide.CopyPasteManager
import kotlinx.serialization.json.*
import java.awt.datatransfer.DataFlavor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup

class MappingCopyPasteAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val settings = PluginSettings.getInstance()
        val mappings = parseMappings(settings.mappings)

        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val clipboard = CopyPasteManager.getInstance().contents
        val data = clipboard?.getTransferData(DataFlavor.stringFlavor) as? String ?: return

        applyMappings(e, data, mappings) { modifiedText ->
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
    }

    private fun parseMappings(jsonString: String): Map<String, Any> {
        val jsonElement = Json.parseToJsonElement(jsonString)
        val jsonObject = jsonElement.jsonObject

        return jsonObject.mapValues { (_, value) ->
            when (value) {
                is JsonPrimitive -> value.content
                is JsonArray -> value.jsonArray.map { it.jsonPrimitive.content }
                else -> throw IllegalArgumentException("Unsupported JSON element type for mapping value: ${value::class}")
            }
        }
    }

    private fun applyMappings(e: AnActionEvent, text: String, mappings: Map<String, Any>, onSuccess: (String) -> Unit) {
        var modifiedText = text
        val converted = mappings[text]

        when (converted) {
            is String -> {
                onSuccess.invoke(converted)
            }

            is List<*> -> {
                val actions = createSelectableActions(converted.map { it.toString() }) { selectedOption ->
                    onSuccess.invoke(selectedOption)
                }
                showDropdown(e, text, actions)
            }

            else -> {
                onSuccess.invoke(modifiedText)
            }
        }
    }

    private fun createSelectableActions(actions: List<String>, onOptionSelected: (String) -> Unit): List<AnAction> {
        return actions.map {
            object : AnAction(it) {
                override fun actionPerformed(e: AnActionEvent) {
                    onOptionSelected.invoke(it)
                }
            }
        }
    }

    private fun showDropdown(e: AnActionEvent, text: String, actions: List<AnAction>) {
        val popupFactory = JBPopupFactory.getInstance()
        val listPopup: ListPopup = popupFactory.createActionGroupPopup(
                text, // Title
                DefaultActionGroup(actions), // Actions to display
                e.dataContext, // Context
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, // Enable speed search
                true // Show numbers for mnemonic navigation
        )

        // Show the popup. For example, in the center of the current window.
//        listPopup.showCenteredInCurrentWindow(e.project!!)
        listPopup.showInBestPositionFor(e.dataContext)
    }
}
