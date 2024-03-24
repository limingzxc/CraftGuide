package craftguide;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.GLAllocation;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static net.xiaoyu233.fml.util.LogProxy.logger;

@Environment(EnvType.CLIENT)
public class RenderEngine {
    private HashMap<String, Integer> textureMap = new HashMap<>();
    private BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);

    /** Stores the image data for the texture. */
    private IntBuffer imageData = GLAllocation.createDirectIntBuffer(4194304);
    private int boundTexture;

    public int getTexture(String par1Str)
    {
        Integer var2 = this.textureMap.get(par1Str);

        if (var2 != null)
        {
            return var2;
        }
        else
        {
            try
            {
                int var3 = GL11.glGenTextures();

                InputStream var6 =  RenderEngine.class.getResourceAsStream(par1Str);
                setupTexture(this, this.readTextureImage(var6), var3, par1Str);

                this.textureMap.put(par1Str, var3);
                return var3;
            }
            catch (Exception var7)
            {
                var7.printStackTrace();
                int var4 = GL11.glGenTextures();
                this.setupTexture(this.missingTextureImage, var4);
                this.textureMap.put(par1Str, var4);
                return var4;
            }
        }
    }

    /**
     * Copy the supplied image onto the specified OpenGL texture
     */
    public void setupTexture(BufferedImage par1BufferedImage, int par2)
    {
        this.setupTextureExt(par1BufferedImage, par2, false, false);
    }

    public void setupTextureExt(BufferedImage par1BufferedImage, int par2, boolean par3, boolean par4)
    {
        if (par1BufferedImage != null)
        {
            this.bindTexture(par2);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            if (par3)
            {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            }

            if (par4)
            {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            }
            else
            {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
            }

            int var5 = par1BufferedImage.getWidth();
            int var6 = par1BufferedImage.getHeight();
            int[] var7 = new int[var5 * var6];
            par1BufferedImage.getRGB(0, 0, var5, var6, var7, 0, var5);

            if (Minecraft.theMinecraft.gameSettings != null && Minecraft.theMinecraft.gameSettings.anaglyph)
            {
                var7 = this.colorToAnaglyph(var7);
            }

            this.imageData = getIntBuffer(this.imageData, var7);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, var5, var6, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, this.imageData);
        }
    }

    public void bindTexture(int par1)
    {
        if (par1 != this.boundTexture)
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, par1);
            this.boundTexture = par1;
        }
    }

    public void resetBoundTexture()
    {
        this.boundTexture = -1;
    }

    private int[] colorToAnaglyph(int[] par1ArrayOfInteger)
    {
        int[] var2 = new int[par1ArrayOfInteger.length];

        for (int var3 = 0; var3 < par1ArrayOfInteger.length; ++var3)
        {
            int var4 = par1ArrayOfInteger[var3] >> 24 & 255;
            int var5 = par1ArrayOfInteger[var3] >> 16 & 255;
            int var6 = par1ArrayOfInteger[var3] >> 8 & 255;
            int var7 = par1ArrayOfInteger[var3] & 255;
            int var8 = (var5 * 30 + var6 * 59 + var7 * 11) / 100;
            int var9 = (var5 * 30 + var6 * 70) / 100;
            int var10 = (var5 * 30 + var7 * 70) / 100;
            var2[var3] = var4 << 24 | var8 << 16 | var9 << 8 | var10;
        }

        return var2;
    }

    public static IntBuffer getIntBuffer(IntBuffer buffer, int[] data) {
        buffer.clear();
        final int have = buffer.capacity();
        final int needed = data.length;
        if (needed > have) {
            logger.debug("resizing gl buffer from 0x%x to 0x%x", have, needed);
            buffer = ByteBuffer.allocateDirect(4 * needed).order(buffer.order()).asIntBuffer();
        }
        buffer.put(data);
        buffer.position(0).limit(needed);
        return buffer;
    }

    public static int[] getImageRGB(BufferedImage image) {
        if (image == null) {
            return null;
        } else {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] rgb = new int[width * height];
            image.getRGB(0, 0, width, height, rgb, 0, width);
            return rgb;
        }
    }

    /**
     * Returns a BufferedImage read off the provided input stream.  Args: inputStream
     */
    private BufferedImage readTextureImage(InputStream par1InputStream) throws IOException
    {
        if (par1InputStream == null)
        {
            return null;
        }
        else
        {
            BufferedImage var2 = ImageIO.read(par1InputStream);
            par1InputStream.close();
            return var2;
        }
    }

    public static void setupTexture(RenderEngine renderEngine, BufferedImage image, int glTextureId, String textureName) {
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] rgb = getImageRGB(image);
            if (glTextureId >= 0) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
            }
            setupTexture(rgb, width, height, 0, 0, textureName);
        }
    }

    public static void setupTexture(int[] rgb, int width, int height, int x, int y, String textureName) {
        setupTexture(width, height, textureName);
        copySubTexture(rgb, width, height, x, y, textureName);
    }

    public static void setupTexture(int width, int height, String textureName) {
        int mipmaps = 0;
        logger.debug("setupTexture(%s) %dx%d %d mipmaps", textureName, width, height, mipmaps);
        int magFilter = GL11.GL_NEAREST;
        int wrap = GL11.GL_REPEAT;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, magFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrap);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrap);
        for (int level = 0; level <= mipmaps; level++) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, GL11.GL_RGBA, width, height, 0, TEX_FORMAT, TEX_DATA_TYPE, (IntBuffer) null);
            checkGLError("%s: glTexImage2D %dx%d level %d", textureName, width, height, level);
            width >>= 1;
            height >>= 1;
        }
    }

    public static void copySubTexture(int[] rgb, int width, int height, int x, int y, String textureName) {
        if (rgb == null) {
            logger.error("copySubTexture %s %d,%d %dx%d: rgb data is null", textureName, x, y, width, height);
            return;
        }
        IntBuffer buffer = getPooledBuffer(width * height * 4).asIntBuffer();
        buffer.put(rgb).position(0);
        int mipmaps = getMipmapLevelsForCurrentTexture();
        IntBuffer newBuffer;
        logger.debug("copySubTexture %s %d,%d %dx%d %d mipmaps", textureName, x, y, width, height, mipmaps);
        for (int level = 0; ; ) {
            if (width <= 0 || height <= 0) {
                break;
            }
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, x, y, width, height, TEX_FORMAT, TEX_DATA_TYPE, buffer);
            checkGLError("%s: glTexSubImage2D(%d, %d, %d, %d, %d)", textureName, level, x, y, width, height);
            if (level >= mipmaps) {
                break;
            }
            newBuffer = getPooledBuffer(width * height).asIntBuffer();
            scaleHalf(buffer, width, height, newBuffer, 0);
            buffer = newBuffer;
            level++;
            x >>= 1;
            y >>= 1;
            width >>= 1;
            height >>= 1;
        }
    }

    private static ByteBuffer getPooledBuffer(int size) {
        Reference<ByteBuffer> ref = bufferPool.get(size);
        ByteBuffer buffer = (ref == null ? null : ref.get());
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(size);
            bufferPool.put(size, new SoftReference<>(buffer));
        }
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(0);
        return buffer;
    }

    private static void checkGLError(String format, Object... params) {
        int error = GL11.glGetError();
        if (error != 0) {
            String message = GLU.gluErrorString(error) + ": " + String.format(format, params);
            new RuntimeException(message).printStackTrace();
        }
    }

    static void scaleHalf(IntBuffer in, int w, int h, IntBuffer out, int rotate) {
        for (int i = 0; i < w / 2; i++) {
            for (int j = 0; j < h / 2; j++) {
                int k = w * 2 * j + 2 * i;
                int pixel00 = in.get(k);
                int pixel01 = in.get(k + 1);
                int pixel10 = in.get(k + w);
                int pixel11 = in.get(k + w + 1);
                if (rotate != 0) {
                    pixel00 = Integer.rotateLeft(pixel00, rotate);
                    pixel01 = Integer.rotateLeft(pixel01, rotate);
                    pixel10 = Integer.rotateLeft(pixel10, rotate);
                    pixel11 = Integer.rotateLeft(pixel11, rotate);
                }
                int pixel = average4RGBA(pixel00, pixel01, pixel10, pixel11);
                if (rotate != 0) {
                    pixel = Integer.rotateRight(pixel, rotate);
                }
                out.put(w / 2 * j + i, pixel);
            }
        }
    }

    private static int average4RGBA(int pixel00, int pixel01, int pixel10, int pixel11) {
        int a00 = pixel00 & 0xff;
        int a01 = pixel01 & 0xff;
        int a10 = pixel10 & 0xff;
        int a11 = pixel11 & 0xff;
        switch ((a00 << 24) | (a01 << 16) | (a10 << 8) | a11) {
            case 0xff000000:
                return pixel00;

            case 0x00ff0000:
                return pixel01;

            case 0x0000ff00:
                return pixel10;

            case 0x000000ff:
                return pixel11;

            case 0xffff0000:
                return average2RGBA(pixel00, pixel01);

            case 0xff00ff00:
                return average2RGBA(pixel00, pixel10);

            case 0xff0000ff:
                return average2RGBA(pixel00, pixel11);

            case 0x00ffff00:
                return average2RGBA(pixel01, pixel10);

            case 0x00ff00ff:
                return average2RGBA(pixel01, pixel11);

            case 0x0000ffff:
                return average2RGBA(pixel10, pixel11);

            case 0x00000000:
            case 0xffffffff:
                return average2RGBA(average2RGBA(pixel00, pixel11), average2RGBA(pixel01, pixel10));

            default:
                int a = a00 + a01 + a10 + a11;
                int pixel = a >> 2;
                for (int i = 8; i < 32; i += 8) {
                    int average = (a00 * ((pixel00 >> i) & 0xff) + a01 * ((pixel01 >> i) & 0xff) +
                            a10 * ((pixel10 >> i) & 0xff) + a11 * ((pixel11 >> i) & 0xff)) / a;
                    pixel |= (average << i);
                }
                return pixel;
        }
    }

    private static int average2RGBA(int a, int b) {
        return (((a & 0xfefefefe) >>> 1) + ((b & 0xfefefefe) >>> 1)) | (a & b & 0x01010101);
    }

    static int getMipmapLevelsForCurrentTexture() {
        int filter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        if (filter != GL11.GL_NEAREST_MIPMAP_LINEAR && filter != GL11.GL_NEAREST_MIPMAP_NEAREST) {
            return 0;
        }
        return Math.min(maxMipmapLevel, GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL));
    }

    static final int maxMipmapLevel = 3;
    private static final Map<Integer, Reference<ByteBuffer>> bufferPool = new HashMap<>();
    static final int TEX_FORMAT = GL12.GL_BGRA;
    static final int TEX_DATA_TYPE = GL12.GL_UNSIGNED_INT_8_8_8_8_REV;
}
