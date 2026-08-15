package com.sparkynox.lumimotion.screen

import com.sparkynox.lumimotion.screen.widget.MenuTextButton
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/**
 * Full replacement for vanilla's TitleScreen. Swapped in on client tick once vanilla's
 * TitleScreen appears - see LumiMotion.kt.
 */
class LumiTitleScreen : LumiBaseScreen(Text.literal("LumiMotion")) {

    companion object {
        private const val MOD_VERSION = "1.0.0"
    }

    override fun init() {
        val startX = width / 8
        var y = height / 3

        for ((label, action) in menuEntries()) {
            addDrawableChild(
                MenuTextButton(
                    startX, y, MenuLayout.BUTTON_WIDTH, MenuLayout.BUTTON_HEIGHT,
                    Text.literal(label),
                    accentColor
                ) { action() }
            )
            y += MenuLayout.BUTTON_SPACING
        }
    }

    private fun menuEntries(): List<Pair<String, () -> Unit>> = listOf(
        "New Game" to { client?.setScreen(SelectWorldScreen(this)) },
        "Multiplayer" to { client?.setScreen(MultiplayerScreen(this)) },
        "Settings" to { client?.setScreen(LumiConfigScreen(this)) },
        "Credits" to { client?.setScreen(LumiCreditsScreen(this)) },
        "Quit Game" to { client?.scheduleStop() }
    )

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val startX = width / 8
        val titleY = height / 3 - 60
        context.drawText(
            textRenderer,
            Text.literal("LumiMotion").formatted(Formatting.BOLD),
            startX, titleY, 0xFFFFFF, true
        )

        val versionText = "Version $MOD_VERSION"
        val versionWidth = textRenderer.getWidth(versionText)
        context.drawTextWithShadow(textRenderer, versionText, width - versionWidth - 12, height - 20, 0xAAAAAA)
    }
}
