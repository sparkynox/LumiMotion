package com.sparkynox.lumimotion.video

import com.sparkynox.lumimotion.config.LumiConfig
import com.sparkynox.lumimotion.config.TargetScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Identifier

object PlaybackManager {

    private val players = mapOf(
        TargetScreen.TITLE_SCREEN to FramePlayer("title"),
        TargetScreen.PAUSE_SCREEN to FramePlayer("pause")
    )

    private val loadedFor = mutableMapOf<TargetScreen, String>()

    /**
     * Call every render frame for the given screen. Loads/reloads the video if the
     * bound video name changed since last time (e.g. user edited config mid-session),
     * ticks playback, and draws the current frame.
     */
    fun renderFor(screen: TargetScreen, context: DrawContext, screenWidth: Int, screenHeight: Int) {
        val binding = LumiConfig.binding(screen)
        if (!binding.enabled || binding.videoName.isBlank()) return

        val player = players[screen] ?: return

        if (loadedFor[screen] != binding.videoName) {
            val entry = VideoLibrary.findByName(binding.videoName)
            if (entry != null) {
                player.load(entry, binding.fps, binding.loop)
                loadedFor[screen] = binding.videoName
            } else {
                return
            }
        }

        player.tick()
        val texture = player.currentTexture() ?: return

        val (x, y, w, h) = if (binding.fullscreen) {
            Quad(0, 0, screenWidth, screenHeight)
        } else {
            Quad(
                (binding.x * screenWidth).toInt(),
                (binding.y * screenHeight).toInt(),
                (binding.width * screenWidth).toInt(),
                (binding.height * screenHeight).toInt()
            )
        }

        context.drawTexture(net.minecraft.client.render.RenderLayer::getGuiTextured, texture, x, y, 0f, 0f, w, h, w, h)
    }

    /** Force a reload next render pass, e.g. after the config UI changes settings live. */
    fun invalidate(screen: TargetScreen) {
        loadedFor.remove(screen)
        players[screen]?.unload()
    }

    private data class Quad(val x: Int, val y: Int, val w: Int, val h: Int)
}
