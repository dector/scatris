package ua.org.dector.lwsge

import org.newdawn.slick.{RemixedAngelCodeFont, Color, AngelCodeFont}


/**
 * @author dector (dector9@gmail.com)
 */

object GraphicsToolkit {
    val FONTS_DIR = "res/fonts/"
    val MEDIUM_FONT_NAME = "Sans-Light24"
    val BIG_FONT_NAME = "Sans-Light48"
    val FONT_FILE_EXT = ".fnt"
    val FONT_IMAGE_EXT = ".png"

    val DEFAULT_FOREGROUND_COLOR = Color.white
    val DEFAULT_BACKGROUND_COLOR = Color.black

//    val DEFAULT_FONT = new AngelCodeFont(FONTS_DIR + DEFAULT_FONT_NAME + FONT_FILE_EXT,
//        FONTS_DIR + DEFAULT_FONT_NAME + FONT_IMAGE_EXT, true)
    val MEDIUM_FONT = new RemixedAngelCodeFont(FONTS_DIR + MEDIUM_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + MEDIUM_FONT_NAME + FONT_IMAGE_EXT, true)
    val BIG_FONT = new RemixedAngelCodeFont(FONTS_DIR + BIG_FONT_NAME + FONT_FILE_EXT,
        FONTS_DIR + BIG_FONT_NAME + FONT_IMAGE_EXT, true)
}
