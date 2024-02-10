package com.oakraw.mappingcopypaste

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "CustomPastePluginSettings",
    storages = [Storage("CustomPastePluginSettings.xml")]
)
class PluginSettings : PersistentStateComponent<PluginSettings> {
    var mappings: String = """
        {
            "original_text": "converted_text",
            "selectable_option_text": [
                "option1",
                "option2"
            ]
        }
    """.trimIndent() // Default to empty JSON object

    override fun getState(): PluginSettings = this

    override fun loadState(state: PluginSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
    
    companion object {
        fun getInstance(): PluginSettings {
            return ServiceManager.getService(PluginSettings::class.java)
        }
    }
}
