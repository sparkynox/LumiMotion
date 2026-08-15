package com.sparkynox.lumimotion.video

import com.sparkynox.lumimotion.LumiMotion
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import net.minecraft.util.Identifier
import java.io.File

/**
 * Plays back a VideoEntry by streaming frames from disk on demand instead of preloading
 * every frame as a resident GPU texture. Preloading everything (the old approach) meant a
 * few seconds of footage held hundreds of full-res textures in VRAM simultaneously, which
 * is what was causing the lag - phone GPUs choke on that texture count/bandwidth. This
 * version keeps only two textures alive at once (current + next, double-buffered) and
 * decodes the next frame from disk just before it's needed instead of all at load time.
 */
class FramePlayer(private val screenTag: String) {

    private var video: VideoEntry? = null
    private var frameFiles: List<File> = emptyList()

    private var currentFrame = 0
    private var fps = 24
    private var loop = true
    private var finished = false
    private var lastFrameTimeMs = 0L

    // two texture slots, alternated each frame advance so we're never decoding
    // and displaying the same slot at once
    private val slotIds = arrayOf(
        Identifier.of("lumimotion_runtime", "$screenTag/slot0"),
        Identifier.of("lumimotion_runtime", "$screenTag/slot1")
    )
    private var activeSlot = 0
    private var slotLoadedFrame = intArrayOf(-1, -1)

    val isFinished get() = finished
    val hasFrames get() = frameFiles.isNotEmpty()

    fun load(entry: VideoEntry, fps: Int, loop: Boolean) {
        unload()
        video = entry
        frameFiles = entry.frames
        this.fps = fps.coerceAtLeast(1)
        this.loop = loop
        currentFrame = 0
        finished = false
        lastFrameTimeMs = System.currentTimeMillis()
        activeSlot = 0
        slotLoadedFrame = intArrayOf(-1, -1)

        uploadFrame(currentFrame, activeSlot)

        LumiMotion.log.info("Streaming '${entry.name}' on $screenTag (${frameFiles.size} frames on disk, 2 held in VRAM)")
    }

    fun tick() {
        if (finished || frameFiles.isEmpty()) return

        val now = System.currentTimeMillis()
        val frameDurationMs = 1000L / fps

        if (now - lastFrameTimeMs >= frameDurationMs) {
            lastFrameTimeMs = now
            val nextFrame = currentFrame + 1

            if (nextFrame >= frameFiles.size) {
                if (loop) {
                    advanceTo(0)
                } else {
                    finished = true
                }
            } else {
                advanceTo(nextFrame)
            }
        }
    }

    private fun advanceTo(frameIndex: Int) {
        currentFrame = frameIndex
        val targetSlot = (activeSlot + 1) % 2

        // only re-decode if this slot isn't already holding the frame we need
        if (slotLoadedFrame[targetSlot] != frameIndex) {
            uploadFrame(frameIndex, targetSlot)
        }
        activeSlot = targetSlot
    }

    private fun uploadFrame(frameIndex: Int, slot: Int) {
        val file = frameFiles.getOrNull(frameIndex) ?: return
        try {
            val client = MinecraftClient.getInstance()
            val image = file.inputStream().use { NativeImage.read(it) }
            val texture = NativeImageBackedTexture(image)
            client.textureManager.registerTexture(slotIds[slot], texture)
            slotLoadedFrame[slot] = frameIndex
        } catch (e: Exception) {
            LumiMotion.log.error("Failed to load frame ${file.name}", e)
        }
    }

    fun currentTexture(): Identifier? {
        if (frameFiles.isEmpty()) return null
        return slotIds[activeSlot]
    }

    fun unload() {
        val client = MinecraftClient.getInstance()
        for (id in slotIds) {
            client.textureManager.destroyTexture(id)
        }
        slotLoadedFrame = intArrayOf(-1, -1)
        frameFiles = emptyList()
        video = null
        currentFrame = 0
        finished = false
    }
}
