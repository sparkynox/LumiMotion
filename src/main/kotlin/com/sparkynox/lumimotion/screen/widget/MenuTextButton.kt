package com.sparkynox.lumimotion.screen.widget

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Drawable
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.screen.narration.NarrationPart
import net.minecraft.text.Text

/**
 * Menu entry styled like the reference title screens: plain text label,
 * hover-only rectangular outline, uniform width regardless of label length.
 *
 * Implements Drawable/Element/Selectable directly instead of extending
 * ClickableWidget/PressableWidget. On 1.21.11, Mojang made ClickableWidget's
 * renderWidget() final, which crashed this widget with IncompatibleClassChangeError
 * since it used to override that method. Implementing the interfaces ourselves
 * means there's nothing of Mojang's left to override, so a future version sealing
 * another method in ClickableWidget can't break this again the same way.
 */
class MenuTextButton(
    var x: Int,
    var y: Int,
    val width: Int,
    val height: Int,
    private val text: Text,
    private val accentColor: Int,
    private val onPress: (MenuTextButton) -> Unit
) : Drawable, Element, Selectable {

    private var hovered = false
    private var focused = false

    fun isMouseOver(mouseX: Double, mouseY: Double): Boolean =
        mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        hovered = isMouseOver(mouseX.toDouble(), mouseY.toDouble())

        val client = MinecraftClient.getInstance()
        val textRenderer = client.textRenderer

        val textColor = if (hovered) 0xFFFFFF else 0xE0E0E0
        context.drawTextWithShadow(
            textRenderer,
            text,
            x,
            y + (height - textRenderer.fontHeight) / 2,
            textColor
        )

        if (hovered) {
            val boxPad = 6
            val left = x - boxPad
            val top = y - 2
            val right = x + width + boxPad
            val bottom = y + height + 2
            context.drawHorizontalLine(left, right, top, accentColor)
            context.drawHorizontalLine(left, right, bottom, accentColor)
            context.drawVerticalLine(left, top, bottom, accentColor)
            context.drawVerticalLine(right, top, bottom, accentColor)
        }
    }

    // -- Element (click/keyboard handling) --

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            onPress(this)
            return true
        }
        return false
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean = false

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        // Enter/Space activates when focused, matches vanilla button keyboard nav
        if (focused && (keyCode == 257 || keyCode == 32)) {
            onPress(this)
            return true
        }
        return false
    }

    override fun setFocused(focused: Boolean) {
        this.focused = focused
    }

    override fun isFocused(): Boolean = focused

    // -- Selectable (tab navigation / accessibility) --

    override fun getType(): Selectable.SelectionType =
        if (focused) Selectable.SelectionType.FOCUSED
        else if (hovered) Selectable.SelectionType.HOVERED
        else Selectable.SelectionType.NONE

    override fun appendNarrations(builder: NarrationMessageBuilder) {
        builder.put(NarrationPart.TITLE, text)
    }
}
