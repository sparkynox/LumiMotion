package com.sparkynox.lumimotion.screen

import com.sparkynox.lumimotion.config.LumiConfig
import com.sparkynox.lumimotion.config.TargetScreen
import com.sparkynox.lumimotion.video.PlaybackManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

/**
 * Base for every LumiMotion-owned menu screen (title, settings, credits). Gives them
 * all the same backdrop instead of each one separately calling vanilla's
 * renderBackground(), which does a panorama-blur pass - with no panorama behind our
 * custom screens that blur has nothing coherent to blur, which is what was showing up
 * as a blank/murky screen on Credits. This draws the title screen's video (if one is
 * configured) or a plain dark fill instead, so every menu matches.
 */
abstract class LumiBaseScreen(title: Text) : Screen(title) {

    protected val accentColor = 0xFF8A5CF6.toInt()

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val binding = LumiConfig.binding(TargetScreen.TITLE_SCREEN)

        if (!binding.enabled || binding.videoName.isBlank()) {
            context.fill(0, 0, width, height, 0xFF1A1A1A.toInt())
        } else {
            PlaybackManager.renderFor(TargetScreen.TITLE_SCREEN, context, width, height)
            // slight darken so menu text/buttons stay readable over busy video frames
            context.fill(0, 0, width, height, 0x66000000)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        // intentionally empty - see class doc. We draw our own background in render() above.
    }

    override fun shouldPause(): Boolean = false
}
