package com.sparkynox.lumimotion.screen.widget

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.text.Text
import net.minecraft.util.math.MathHelper

/**
 * A menu entry styled like the reference title screens: plain text label,
 * left-aligned, with a rectangular outline that only shows up while hovered.
 * No vanilla button background/texture - just text + conditional box, so every
 * entry in the list lines up at the same width and height regardless of label length.
 */
class MenuTextButton(
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    text: Text,
    private val accentColor: Int,
    private val onPress: (MenuTextButton) -> Unit
) : PressableWidget(x, y, width, height, text) {

    override fun onPress() = onPress(this)

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        val client = net.minecraft.client.MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        val textColor = if (isHovered) 0xFFFFFF else 0xE0E0E0
        context.drawTextWithShadow(
            textRenderer,
            message,
            x,
            y + (height - textRenderer.fontHeight) / 2,
            textColor
        )

        // hover box - only drawn while the mouse is actually over this entry
        if (isHovered) {
            val boxPad = 6
            val left = x - boxPad
            val top = y - 2
            val right = x + width + boxPad
            val bottom = y + height + 2
            drawBoxOutline(context, left, top, right, bottom, accentColor)
        }
    }

    private fun drawBoxOutline(context: DrawContext, left: Int, top: Int, right: Int, bottom: Int, color: Int) {
        context.drawHorizontalLine(left, right, top, color)
        context.drawHorizontalLine(left, right, bottom, color)
        context.drawVerticalLine(left, top, bottom, color)
        context.drawVerticalLine(right, top, bottom, color)
    }

    override fun appendClickableNarrations(builder: net.minecraft.client.gui.screen.narration.NarrationMessageBuilder) {
        appendDefaultNarrations(builder)
    }
}
