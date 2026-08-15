package com.sparkynox.lumimotion.mixin

import com.sparkynox.lumimotion.config.TargetScreen
import com.sparkynox.lumimotion.video.PlaybackManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.GameMenuScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(GameMenuScreen::class)
abstract class GameMenuScreenMixin {

    @Inject(
        method = ["render"],
        at = [At("HEAD")]
    )
    private fun onRenderBackground(
        context: DrawContext,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
        ci: CallbackInfo
    ) {
        val self = this as GameMenuScreen
        PlaybackManager.renderFor(TargetScreen.PAUSE_SCREEN, context, self.width, self.height)
    }
}
