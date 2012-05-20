package ua.org.dector.lwsge

import org.newdawn.slick.{RemixedAngelCodeFont, Color, AngelCodeFont}


/**
 * @author dector (dector9@gmail.com)
 */

object GraphicsToolkit {
    val FONTS_DIR = "res/fonts/"

    val CONSOLE_FONT_NAME = "Sans-Mono14"
    val SMALL_FONT_NAME = "Sans-Light14"
    val MEDIUM_FONT_NAME = "Sans-Light24"
    val BIG_FONT_NAME = "Sans-Light48"

    val FONT_FILE_EXT = ".fnt"
    val FONT_IMAGE_EXT = ".png"

    val DEFAULT_FOREGROUND_COLOR = Color.white
    val DEFAULT_BACKGROUND_COLOR = Color.black

    val CONSOLE_FONT = new RemixedAngelCodeFont(FONTS_DIR + CONSOLE_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + CONSOLE_FONT_NAME + FONT_IMAGE_EXT, true)
    val SMALL_FONT = new RemixedAngelCodeFont(FONTS_DIR + SMALL_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + SMALL_FONT_NAME + FONT_IMAGE_EXT, true)
    val MEDIUM_FONT = new RemixedAngelCodeFont(FONTS_DIR + MEDIUM_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + MEDIUM_FONT_NAME + FONT_IMAGE_EXT, true)
    val BIG_FONT = new RemixedAngelCodeFont(FONTS_DIR + BIG_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + BIG_FONT_NAME + FONT_IMAGE_EXT, true)
}
