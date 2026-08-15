package com.sparkynox.lumimotion.video

import com.sparkynox.lumimotion.LumiMotion
import java.io.File
import java.util.regex.Pattern

/**
 * One video = one folder inside config/lumimotion/videos/
 * Frames can be named anything as long as they end in a number before the extension.
 * frame_0001.png, 1.png, pic (12).png, shot-7.png -> all fine, we just sort by the trailing number.
 */
data class VideoEntry(
    val name: String,
    val folder: File,
    val frames: List<File>
) {
    val frameCount get() = frames.size
}

object VideoLibrary {

    // matches the last run of digits in a filename, e.g. "frame_0042" -> 42
    private val numberPattern: Pattern = Pattern.compile("(\\d+)(?!.*\\d)")

    val videos = mutableListOf<VideoEntry>()

    fun scan() {
        videos.clear()

        val root = LumiMotion.videosDir.toFile()
        val subfolders = root.listFiles { f -> f.isDirectory } ?: return

        for (folder in subfolders) {
            val pngs = folder.listFiles { f -> f.isFile && f.extension.equals("png", ignoreCase = true) }
                ?: continue

            if (pngs.isEmpty()) continue

            val sorted = pngs.sortedBy { extractFrameNumber(it.nameWithoutExtension) }

            videos.add(VideoEntry(name = folder.name, folder = folder, frames = sorted))
            LumiMotion.log.info("Loaded video '${folder.name}' with ${sorted.size} frames")
        }
    }

    fun refresh() = scan()

    fun findByName(name: String): VideoEntry? = videos.find { it.name == name }

    private fun extractFrameNumber(filename: String): Int {
        val matcher = numberPattern.matcher(filename)
        return if (matcher.find()) {
            matcher.group(1).toIntOrNull() ?: 0
        } else {
            0
        }
    }
}
