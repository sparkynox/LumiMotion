package com.sparkynox.lumimotion.screen

import com.sparkynox.lumimotion.config.LumiConfig
import com.sparkynox.lumimotion.config.TargetScreen
import com.sparkynox.lumimotion.video.PlaybackManager
import com.sparkynox.lumimotion.video.VideoLibrary
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.CyclingButtonWidget
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.text.Text
import kotlin.math.roundToInt

class LumiConfigScreen(private val parent: Screen?) : LumiBaseScreen(Text.literal("LumiMotion Settings")) {

    private var activeTarget = TargetScreen.TITLE_SCREEN

    override fun init() {
        rebuild()
    }

    private fun rebuild() {
        clearChildren()
        val binding = LumiConfig.binding(activeTarget)
        val centerX = width / 2
        var y = 30

        addDrawableChild(
            CyclingButtonWidget.builder<TargetScreen> {
                when (it) {
                    TargetScreen.TITLE_SCREEN -> Text.literal("Title Screen")
                    TargetScreen.PAUSE_SCREEN -> Text.literal("Pause Menu")
                }
            }
                .values(TargetScreen.TITLE_SCREEN, TargetScreen.PAUSE_SCREEN)
                .initially(activeTarget)
                .build(centerX - 100, y, 200, 20, Text.literal("Editing")) { _, value ->
                    activeTarget = value
                    rebuild()
                }
        )
        y += 28

        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(binding.enabled)
                .build(centerX - 100, y, 200, 20, Text.literal("Enabled")) { _, value ->
                    binding.enabled = value
                    LumiConfig.save()
                    PlaybackManager.invalidate(activeTarget)
                }
        )
        y += 24

        val videoNames = VideoLibrary.videos.map { it.name }
        if (videoNames.isEmpty()) {
            addDrawableChild(
                net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("No videos found - refresh")) {
                    VideoLibrary.refresh()
                    rebuild()
                }.dimensions(centerX - 100, y, 200, 20).build()
            )
        } else {
            val currentIndex = videoNames.indexOf(binding.videoName).coerceAtLeast(0)
            addDrawableChild(
                CyclingButtonWidget.builder<String> { Text.literal(it) }
                    .values(videoNames)
                    .initially(videoNames.getOrElse(currentIndex) { videoNames[0] })
                    .build(centerX - 100, y, 200, 20, Text.literal("Video")) { _, value ->
                        binding.videoName = value
                        LumiConfig.save()
                        PlaybackManager.invalidate(activeTarget)
                    }
            )
        }
        y += 24

        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(binding.loop)
                .build(centerX - 100, y, 200, 20, Text.literal("Loop")) { _, value ->
                    binding.loop = value
                    LumiConfig.save()
                    PlaybackManager.invalidate(activeTarget)
                }
        )
        y += 24

        addDrawableChild(object : SliderWidget(
            centerX - 100, y, 200, 20,
            Text.literal("FPS: ${binding.fps}"),
            (binding.fps - 1) / 59.0
        ) {
            override fun updateMessage() {
                message = Text.literal("FPS: ${binding.fps}")
            }

            override fun applyValue() {
                binding.fps = (1 + value * 59).roundToInt()
                LumiConfig.save()
                PlaybackManager.invalidate(activeTarget)
            }
        })
        y += 24

        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(binding.fullscreen)
                .build(centerX - 100, y, 200, 20, Text.literal("Fullscreen")) { _, value ->
                    binding.fullscreen = value
                    LumiConfig.save()
                    PlaybackManager.invalidate(activeTarget)
                    rebuild()
                }
        )
        y += 24

        if (!binding.fullscreen) {
            addDrawableChild(fractionSlider("X", centerX, y, binding.x) {
                binding.x = it; LumiConfig.save(); PlaybackManager.invalidate(activeTarget)
            })
            y += 24
            addDrawableChild(fractionSlider("Y", centerX, y, binding.y) {
                binding.y = it; LumiConfig.save(); PlaybackManager.invalidate(activeTarget)
            })
            y += 24
            addDrawableChild(fractionSlider("Width", centerX, y, binding.width) {
                binding.width = it; LumiConfig.save(); PlaybackManager.invalidate(activeTarget)
            })
            y += 24
            addDrawableChild(fractionSlider("Height", centerX, y, binding.height) {
                binding.height = it; LumiConfig.save(); PlaybackManager.invalidate(activeTarget)
            })
            y += 24
        }

        y += 8
        addDrawableChild(
            net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Refresh video list")) {
                VideoLibrary.refresh()
                rebuild()
            }.dimensions(centerX - 100, y, 200, 20).build()
        )
        y += 24

        // Credits, positioned above Done the same way it sits above Quit Game in the reference layout
        addDrawableChild(
            net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Credits")) {
                client?.setScreen(LumiCreditsScreen(this))
            }.dimensions(centerX - 100, y, 200, 20).build()
        )
        y += 24

        addDrawableChild(
            net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Done")) {
                client?.setScreen(parent)
            }.dimensions(centerX - 100, y, 200, 20).build()
        )
    }

    private fun fractionSlider(
        label: String,
        centerX: Int,
        y: Int,
        initial: Float,
        onChange: (Float) -> Unit
    ): SliderWidget {
        return object : SliderWidget(
            centerX - 100, y, 200, 20,
            Text.literal("$label: ${(initial * 100).roundToInt()}%"),
            initial.toDouble()
        ) {
            override fun updateMessage() {
                message = Text.literal("$label: ${(value * 100).roundToInt()}%")
            }

            override fun applyValue() {
                onChange(value.toFloat())
            }
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 12, 0xFFFFFF)
    }
}
