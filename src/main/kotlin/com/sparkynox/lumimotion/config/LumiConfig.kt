package com.sparkynox.lumimotion.config

import com.google.gson.GsonBuilder
import com.sparkynox.lumimotion.LumiMotion
import java.io.File
import kotlin.io.path.div

enum class TargetScreen {
    TITLE_SCREEN,
    PAUSE_SCREEN
}

/**
 * Settings for one screen (title screen, pause screen, etc).
 * Each screen can independently play a different video, or none.
 */
data class ScreenBinding(
    var enabled: Boolean = false,
    var videoName: String = "",
    var fps: Int = 24,
    var loop: Boolean = true,
    // position/size as fraction of screen (0.0 - 1.0), so it scales with resolution
    var x: Float = 0f,
    var y: Float = 0f,
    var width: Float = 1f,
    var height: Float = 1f,
    // if true, video fills the whole screen and ignores x/y/width/height
    var fullscreen: Boolean = true
)

data class LumiConfigData(
    var bindings: MutableMap<String, ScreenBinding> = mutableMapOf(
        TargetScreen.TITLE_SCREEN.name to ScreenBinding(fullscreen = true),
        TargetScreen.PAUSE_SCREEN.name to ScreenBinding(fullscreen = true)
    )
)

object LumiConfig {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File
        get() = (LumiMotion.configDir / "settings.json").toFile()

    var data: LumiConfigData = LumiConfigData()
        private set

    fun load() {
        try {
            if (configFile.exists()) {
                configFile.reader().use {
                    data = gson.fromJson(it, LumiConfigData::class.java) ?: LumiConfigData()
                }
            } else {
                data = LumiConfigData()
                save()
            }
        } catch (e: Exception) {
            LumiMotion.log.error("Failed to load config, using defaults", e)
            data = LumiConfigData()
        }
    }

    fun save() {
        try {
            configFile.parentFile.mkdirs()
            configFile.writer().use { gson.toJson(data, it) }
        } catch (e: Exception) {
            LumiMotion.log.error("Failed to save config", e)
        }
    }

    fun binding(screen: TargetScreen): ScreenBinding =
        data.bindings.getOrPut(screen.name) { ScreenBinding() }
}
