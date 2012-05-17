package org.newdawn.slick;

import org.newdawn.slick.opengl.InternalTextureLoader;
import org.lwjgl.opengl.GL11;

public class RemixedImage extends Image {
    protected RemixedImage() {
    }

    public RemixedImage(String ref) throws SlickException  {
        this(ref, false);
    }

    public RemixedImage(String ref, boolean flipped) throws SlickException {
        this(ref, flipped, FILTER_LINEAR);
    }

    public RemixedImage(String ref, boolean flipped, int filter) throws SlickException {
        this(ref, flipped, filter, null);
    }

    public RemixedImage(String ref, boolean flipped, int filter, Color transparent) throws SlickException {
        try {
            this.ref = ref;
            int[] trans = null;
            if (transparent != null) {
                trans = new int[3];
                trans[0] = (int) (transparent.r * 255);
                trans[1] = (int) (transparent.g * 255);
                trans[2] = (int) (transparent.b * 255);
            }
            InternalTextureLoader loader = InternalTextureLoader.get();
            if(loader==null){
                throw new SlickException("Loader is null,Failed to load image from: "+ref);
            }else{
                texture = loader.getTexture(ref, flipped,
                        filter == FILTER_LINEAR ? GL11.GL_LINEAR : GL11.GL_NEAREST, trans);
            }
        } catch (Exception e) {
            //System.out.println(e);
            //Log.error(e);
            throw new SlickException("Failed to load image from: "+ref);
        }
    }

    @Override
    public Image getSubImage(int x, int y, int width, int height) {
        init();

        float newTextureOffsetX = ((x / (float) this.width) * textureWidth) + textureOffsetX;
        float newTextureOffsetY = ((y / (float) this.height) * textureHeight) + textureOffsetY;
        float newTextureWidth = ((width / (float) this.width) * textureWidth);
        float newTextureHeight = ((height / (float) this.height) * textureHeight);

        RemixedImage sub = new RemixedImage();
        sub.inited = true;
        sub.texture = this.texture;
        sub.textureOffsetX = newTextureOffsetX;
        sub.textureOffsetY = newTextureOffsetY;
        sub.textureWidth = newTextureWidth;
        sub.textureHeight = newTextureHeight;

        sub.width = width;
        sub.height = height;
        sub.ref = ref;
        sub.centerX = width / 2;
        sub.centerY = height / 2;

        return sub;
    }

    @Override
    public void drawEmbedded(float x, float y, float width, float height) {
        init();

        y += height;

        GL.glTexCoord2f(textureOffsetX, textureOffsetY + textureHeight);
        GL.glVertex3f(x, y - textureOffsetY, 0);
        GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY
                + textureHeight);
        GL.glVertex3f(x + width, y - textureOffsetY, 0);
        GL.glTexCoord2f(textureOffsetX + textureWidth, textureOffsetY);
        GL.glVertex3f(x + width, y + height - textureOffsetY, 0);
        GL.glTexCoord2f(textureOffsetX, textureOffsetY);
        GL.glVertex3f(x, y + height - textureOffsetY, 0);
    }
}