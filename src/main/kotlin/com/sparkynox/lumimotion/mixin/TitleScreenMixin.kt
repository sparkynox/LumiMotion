package com.sparkynox.lumimotion.mixin

import com.sparkynox.lumimotion.config.TargetScreen
import com.sparkynox.lumimotion.video.PlaybackManager
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.TitleScreen
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
abstract class TitleScreenMixin {

    // renderBackground isn't a real method on TitleScreen in 1.21.4 (it was split into
    // renderPanoramaBackground/renderInGameBackground/renderDarkening). Injecting at the
    // HEAD of render() instead draws our frames first, so vanilla buttons/logo still end
    // up layered on top since they render afterward in the same call.
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
        val self = this as TitleScreen
        PlaybackManager.renderFor(TargetScreen.TITLE_SCREEN, context, self.width, self.height)
    }
}
