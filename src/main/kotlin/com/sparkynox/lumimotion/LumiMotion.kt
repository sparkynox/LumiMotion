package com.sparkynox.lumimotion

import com.sparkynox.lumimotion.config.LumiConfig
import com.sparkynox.lumimotion.screen.LumiTitleScreen
import com.sparkynox.lumimotion.video.VideoLibrary
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.TitleScreen
import org.slf4j.LoggerFactory
import kotlin.io.path.createDirectories
import kotlin.io.path.div

object LumiMotion : ClientModInitializer {

    const val MOD_ID = "lumimotion"
    val log = LoggerFactory.getLogger("LumiMotion")

    val configDir = FabricLoader.getInstance().configDir / MOD_ID
    val videosDir = configDir / "videos"

    override fun onInitializeClient() {
        log.info("LumiMotion booting up...")

        videosDir.createDirectories()

        LumiConfig.load()
        VideoLibrary.scan()

        log.info("Found ${VideoLibrary.videos.size} video(s) in config/$MOD_ID/videos")

        // Full title screen replacement without a mixin: checked once per client tick,
        // after vanilla's TitleScreen is already fully set as the current screen (not
        // from inside its own init callback, which would risk a re-entrant setScreen
        // call). Swaps it for ours the first tick it appears.
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (client.currentScreen is TitleScreen && client.currentScreen !is LumiTitleScreen) {
                client.setScreen(LumiTitleScreen())
            }
        }
    }
}
