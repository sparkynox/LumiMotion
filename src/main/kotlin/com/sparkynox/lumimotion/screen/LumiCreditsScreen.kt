package com.sparkynox.lumimotion.screen

import com.sparkynox.lumimotion.LumiMotion
import com.sparkynox.lumimotion.screen.widget.MenuTextButton
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class LumiCreditsScreen(private val parent: Screen?) : LumiBaseScreen(Text.literal("Credits")) {

    private val avatarTexture = Identifier.of(LumiMotion.MOD_ID, "textures/gui/credits_avatar.png")

    override fun init() {
        addDrawableChild(
            MenuTextButton(
                width / 2 - MenuLayout.BUTTON_WIDTH / 2, height - 40,
                MenuLayout.BUTTON_WIDTH, MenuLayout.BUTTON_HEIGHT,
                Text.literal("Back"),
                accentColor
            ) { client?.setScreen(parent) }
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        val avatarSize = 96
        val avatarX = width / 2 - avatarSize / 2
        val avatarY = 50
        context.drawTexture(
            net.minecraft.client.render.RenderLayer::getGuiTextured,
            avatarTexture, avatarX, avatarY, 0f, 0f, avatarSize, avatarSize, avatarSize, avatarSize
        )

        var textY = avatarY + avatarSize + 16
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("SparkyNox"), width / 2, textY, 0xFFFFFF)
        textY += 14
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Mod author & developer"), width / 2, textY, 0xAAAAAA)
        textY += 20
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("LumiMotion"), width / 2, textY, 0xFFFFFF)
        textY += 14
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("modrinth.com/user/sparkynox"), width / 2, textY, 0x8A5CF6)
    }
}
