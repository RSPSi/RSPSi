package com.jagex.draw.font;

import com.jagex.draw.raster.GameRasterizer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @name RuneScape 2 Font Rendering
 * @author Joseph Melsha (joe.melsha@live.com)
 * @version 1.2, 7/16/2011
 */
public final class RSFont
{
    public RSFont(Graphics context, String name, int style, int size, boolean smooth)
    {
        this(context, new Font(name, style, size), smooth);
    }

    public RSFont(Graphics context, Font font, boolean smooth)
    {
        if (context == null || font == null)
            throw new IllegalArgumentException();

        this.font = font;
        fontMetrics = context.getFontMetrics(font);
        if (fontMetrics == null)
            throw new RuntimeException();

        ascent = fontMetrics.getAscent();
        Rectangle bounds = font.getMaxCharBounds(fontMetrics.getFontRenderContext()).getBounds();
        bufferWidth = bounds.width;
        bufferHeight = bounds.height;
        if (bufferWidth < 1 || bufferHeight < 1)
            throw new RuntimeException();

        bufferSize = bufferWidth * bufferHeight;
        bufferPixels = new int[bufferSize];
        buffer = wrapPixels(bufferPixels, bufferWidth, bufferHeight);
        bufferGraphics = buffer.createGraphics();
        if (bufferGraphics == null)
            throw new RuntimeException();

        bufferGraphics.setFont(font);
        if (smooth)
            bufferGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, smooth ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON:RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        bufferGraphics.setColor(new Color(FOREGROUND, true));
        charCache = new RSFontChar[256][];
    }

    public RSFont(URL url) throws IOException
    {
        this(url.openStream());
    }

    public RSFont(String file) throws IOException
    {
        this(new File(file));
    }

    public RSFont(File file) throws IOException
    {
        this.font = null;
        FileInputStream in = new FileInputStream(file);
        try
        {
            load(in);
        }
        finally
        {
            in.close();
        }
    }

    public RSFont(byte[] data) throws IOException
    {
        this(new ByteArrayInputStream(data, 0, data.length));
    }

    public RSFont(byte[] data, int offset, int length) throws IOException
    {
        this(new ByteArrayInputStream(data, offset, length));
    }

    public RSFont(InputStream in) throws IOException
    {
        this.font = null;
        load(in);
    }

    private void load(InputStream in0) throws IOException
    {
        DataInputStream in = new DataInputStream(new GZIPInputStream(in0));
        int bufferWidth = readInt(in);
        int bufferHeight = readInt(in);
        int ascent = readInt(in);
        if (bufferWidth == 0 || bufferHeight == 0)
            bufferWidth = bufferHeight = 0;

        this.bufferWidth = bufferWidth;
        this.bufferHeight = bufferHeight;
        this.ascent = ascent;
        charCache = new RSFontChar[256][];
        for (int i = 0; i != 0x10000; ++i)
            provideFontChar((char) i, new RSFontChar(in));

    }

    public void writeTo(OutputStream out0) throws IOException
    {
        GZIPOutputStream gzipOut = new GZIPOutputStream(out0);
        DataOutputStream out = new DataOutputStream(gzipOut);
        writeInt(out, bufferWidth);
        writeInt(out, bufferHeight);
        writeInt(out, ascent);
        for (int i = 0; i != 0x10000; ++i)
        {
            RSFontChar chr = getFontChar((char) i, false);
            if (chr == null)
            {
                out.writeByte(0);
                continue;
            }
            chr.writeTo(out);
        }

        gzipOut.finish();
    }

    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeTo(out);
        return out.toByteArray();
    }

    public void writeTo(String file) throws IOException
    {
        writeTo(new File(file));
    }

    public void writeTo(File file) throws IOException
    {
        FileOutputStream out = new FileOutputStream(file);
        try
        {
            writeTo(out);
        }
        finally
        {
            out.close();
        }
    }

    public int drawStringCenter(String str, int x, int y, int color)
    {
        return drawStringCenter(str, x, y, color, false);
    }

    public int drawStringCenter(String str, int x, int y, int color, boolean validAlpha)
    {
        return drawString(str, x - stringWidth(str) / 2, y, color, validAlpha);
    }

    public int drawStringLeft(String str, int x, int y, int color)
    {
        return drawStringLeft(str, x, y, color, false);
    }

    public int drawStringLeft(String str, int x, int y, int color, boolean validAlpha)
    {
        return drawString(str, x - stringWidth(str), y, color, validAlpha);
    }

    public int drawString(String str, int x, int y, int color)
    {
        return drawString(str, x, y, color, false);
    }

    public int drawString(String str, int x, int y, int color, boolean validAlpha)
    {
        if (str == null)
            return 0;

        if (!validAlpha)
            color |= 0xff000000;

        int length = str.length();
        int width = 0;
        for (int i = 0; i != length; ++i)
            width += drawChar(str.charAt(i), width + x, y, color, true);

        return width;
    }

    public int drawChar(char chr, int x, int y, int color)
    {
        return drawChar(chr, x, y, color, false);
    }

    public int drawChar(char chr, int x, int y, int color, boolean validAlpha)
    {
        RSFontChar fontChar = getFontChar(chr);
        drawPixels(fontChar.pixels, fontChar.width, fontChar.height, fontChar.xOffset + x, fontChar.yOffset + y - ascent, validAlpha ? color:color | 0xff000000);
        return fontChar.widthOffset;
    }

    public int stringWidth(String str)
    {
        if (str == null)
            return 0;

        int width = 0;
        int length = str.length();
        for (int i = 0; i != length; ++i)
            width += charWidth(str.charAt(i));

        return width;
    }

    public int stringHeight(String str)
    {
        if (str == null)
            return 0;

        int height = 0;
        int length = str.length();
        for (int i = 0; i != length; ++i)
        {
            int h = charHeight(str.charAt(i));
            if (height < h)
                height = h;

        }

        return height;
    }

    public int stringX(String str)
    {
        if (str == null || str.length() == 0)
            return 0;

        return charX(str.charAt(0));
    }

    public int stringY(String str)
    {
        if (str == null || str.length() == 0)
            return 0;

        int y = 0;
        int length = str.length();
        for (int i = 0; i != length; ++i)
        {
            int y0 = charY(str.charAt(i));
            if (y > y0)
                y = y0;

        }

        return y;
    }

    public Rectangle stringBounds(String str)
    {
        return new Rectangle(stringX(str), stringY(str), stringWidth(str), stringHeight(str));
    }

    public int charX(char chr)
    {
        return getFontChar(chr).xOffset;
    }

    public int charY(char chr)
    {
        return ascent + getFontChar(chr).yOffset;
    }

    public int charWidth(char chr)
    {
        return getFontChar(chr).widthOffset;
    }

    public int charHeight(char chr)
    {
        return getFontChar(chr).heightOffset;
    }

    public Rectangle charBounds(char chr)
    {
        return getFontChar(chr).getBounds();
    }

    public int getMaxCharWidth()
    {
        return bufferWidth;
    }

    public int getMaxCharHeight()
    {
        return bufferHeight;
    }

    public int getDescent()
    {
        return ascent;
    }

    public void provideFontChar(char chr, RSFontChar fontChar)
    {
        if (disposed)
            return;

        if (fontChar == null)
            fontChar = DUMMY_FONT_CHAR;

        int sector = chr >>> 8;
        int index = chr & 0xff;
        RSFontChar[][] charCache = this.charCache;
        RSFontChar[] cacheSector = charCache[sector];
        if (cacheSector != null)
        {
            cacheSector[index] = fontChar;
            return;
        }
        cacheSector = charCache[sector] = new RSFontChar[256];
        cacheSector[index] = fontChar;
    }

    public RSFontChar getFontChar(char chr)
    {
        return getFontChar(chr, true);
    }

    public RSFontChar getFontChar(char chr, boolean giveDummy)
    {
        if (disposed)
            return giveDummy ? DUMMY_FONT_CHAR:null;

        int sector = chr >>> 8;
        int index = chr & 0xff;
        RSFontChar[][] charCache = this.charCache;
        RSFontChar[] cacheSector = charCache[sector];
        if (cacheSector != null)
        {
            RSFontChar fontChar = cacheSector[index];
            if (fontChar != null)
                return fontChar;

        }
        RSFontChar fontChar = getFontChar0(chr);
        if (fontChar == null)
        {
            if (!giveDummy)
                return null;

            fontChar = DUMMY_FONT_CHAR;
        }
        if (cacheSector == null)
            cacheSector = charCache[sector] = new RSFontChar[256];

        cacheSector[index] = fontChar;
        return fontChar;
    }

    public boolean isDisposed()
    {
        return disposed;
    }

    public void dispose()
    {
        if (disposed)
            return;

        disposed = true;
        charCache = null;
        bufferPixels = null;
        fontMetrics = null;
        Graphics2D gfx = bufferGraphics;
        if (gfx != null)
        {
            bufferGraphics = null;
            try
            {
                gfx.dispose();
            }
            catch (Exception ex) { }
        }
        BufferedImage img = buffer;
        if (img != null)
        {
            buffer = null;
            try
            {
                img.flush();
            }
            catch (Exception ex) { }
        }
    }

    private static BufferedImage wrapPixels(int[] pixels, int width, int height)
    {
        DirectColorModel model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
        return new BufferedImage(model, Raster.createWritableRaster(model.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, width * height), null), false, new Hashtable());
    }

    private static int bind(int a, int b)
    {
        return (a * (b + 1)) >>> 8;
    }

    private void drawPixels(final byte[] pixels, final int width, final int height, int xPos, int yPos, final int color)
    {
        GameRasterizer rasterizer = GameRasterizer.getInstance();
        if (width < 1 || height < 1)
            return;

        int x = 0;
        int y = 0;
        int w = width;
        int h = height;
        int tmp = 0;
        if (xPos < tmp)
        {
            x += tmp - xPos;
            w -= tmp - xPos;
            xPos = tmp;
        }
        tmp = 0;
        if (yPos < tmp)
        {
            y += tmp - yPos;
            h -= tmp - yPos;
            yPos = tmp;
        }
        tmp = rasterizer.getWidth();
        if (xPos + w > tmp)
            w = tmp - xPos;

        tmp = rasterizer.getHeight();
        if (yPos + h > tmp)
            h = tmp - yPos;

        if (w < 1 || h < 1)
            return;

        tmp = rasterizer.getWidth();
        int localIndex = x + y * width;
        int globalIndex = xPos + yPos * tmp;
        int[] dst = rasterizer.getRaster();
        for (int i = 0; i != h; ++i)
        {
            for (int i1 = 0; i1 != w; ++i1)
                dst[globalIndex + i1] = blend(dst[globalIndex + i1], color, bind(pixels[localIndex + i1] & 0xff, color >>> 24)) & 0xffffff;

            localIndex += width;
            globalIndex += tmp;
        }

    }

    private RSFontChar getFontChar0(char chr)
    {
        if (!font.canDisplay(chr))
            return null;

        int bufferSize = this.bufferSize;
        int bufferWidth = this.bufferWidth;
        int bufferHeight = this.bufferHeight;
        int[] bufferPixels = this.bufferPixels;
        for (int i = 0; i != bufferSize; ++i)
            bufferPixels[i] = BACKGROUND;

        bufferGraphics.drawString(String.valueOf(chr), 0, fontMetrics.getMaxAscent());
        int startX = bufferWidth;
        int startY = 0;
        int endX = 0;
        int endY = 0;
        int offset = 0;
        for (int i = 0; i != bufferHeight; ++i)
        {
            int startX0 = bufferWidth;
            for (int i1 = 0; i1 != startX0; ++i1)
                if ((bufferPixels[offset + i1] & 0xff000000) != 0)
                {
                    startX0 = i1;
                    break;
                }

            offset += bufferWidth;
            if (startX0 == bufferWidth)
            {
                if (startY == i)
                    startY = 1 + i;

                continue;
            }
            endY = 1 + i;
            int endX0 = bufferWidth - startX0 - 1;
            if (endX0 < endX)
                endX0 = endX;

            for (int i1 = 0; i1 != endX0; ++i1)
                if ((bufferPixels[offset - i1 - 1] & 0xff000000) != 0)
                {
                    endX0 = bufferWidth - i1;
                    break;
                }

            if (startX > startX0)
                startX = startX0;

            if (endX < endX0)
                endX = endX0;

        }

        if (startX < 0)
        {
            endX += startX;
            startX = 0;
        }
        if (startY < 0)
        {
            endY += startY;
            startY = 0;
        }
        if (startX > endX)
            endX = startX;

        if (startY > endY)
            endY = startY;

        int x = startX;
        int y = startY;
        int width = endX - x;
        int height = endY - y;
        byte[] fontPixels = new byte[width * height];
        int localIndex = 0;
        int globalIndex = x + y * bufferWidth;
        for (int i = 0; i != height; ++i)
        {
            for (int i1 = 0; i1 != width; ++i1)
                fontPixels[localIndex + i1] = (byte) (bufferPixels[globalIndex + i1] >>> 24);

            localIndex += width;
            globalIndex += bufferWidth;
        }

        return new RSFontChar(fontPixels, width, height, x, y, fontMetrics.charWidth(chr), bufferHeight);
    }

    private static int blend(int dst, int src, int alpha)
    {
        if (alpha == 0)
            return dst;

        if (alpha == 255)
            return src;

        int delta = 255 - alpha;
        return (src & 0xff000000 | ((src & 0xff00ff) * alpha + (dst & 0xff00ff) * delta & 0xff00ff00 | (src & 0xff00) * alpha + (dst & 0xff00) * delta & 0xff0000) >>> 8);
    }

    protected void finalize() throws Throwable
    {
        try
        {
            dispose();
        }
        finally
        {
            super.finalize();
        }
    }

    private static int readInt(DataInputStream in) throws IOException
    {
        int b = in.readByte() & 0xff;
        if ((b & 0x80) == 0)
            return b;

        return b | ((in.readByte() & 0xff) << 7) | ((in.readByte() & 0xff) << 15) | ((in.readByte() & 0xff) << 23);
    }

    private static void writeInt(DataOutputStream out, int val) throws IOException
    {
        if ((val & 0x7f) == val)
        {
            out.writeByte((byte) val);
            return;
        }
        out.writeByte((byte) (val | 0x80));
        out.writeByte((byte) (val >>> 7));
        out.writeByte((byte) (val >>> 15));
        out.writeByte((byte) (val >>> 23));
    }

    public static class RSFontChar
    {
        public RSFontChar(byte[] pixels, int width, int height, int xOffset, int yOffset, int widthOffset, int heightOffset)
        {
            if (width < 0 || height < 0 || pixels == null || width * height > pixels.length)
                width = height = 0;

            if (pixels == null)
                pixels = new byte[0];

            if (xOffset < 0)
                xOffset = 0;

            if (yOffset < 0)
                yOffset = 0;

            if (widthOffset < 0)
                widthOffset = 0;

            if (heightOffset < 0)
                heightOffset = 0;

            this.pixels = pixels;
            this.width = width;
            this.height = height;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;
        }

        public RSFontChar(InputStream in0) throws IOException
        {
            DataInputStream in = in0 instanceof DataInputStream ? (DataInputStream) in0:new DataInputStream(in0);
            int flags = in.readByte() & 0xff;
            int width;
            int height;
            if ((flags & 0x1) != 0)
            {
                width = readInt(in);
                height = readInt(in);
                if (width == 0 || height == 0)
                    width = height = 0;

            }
            else
                width = height = 0;

            int count = width * height;
            byte[] pixels = new byte[count];
            for (int i = 0; i != count; ++i)
                pixels[i] = in.readByte();

            int xOffset = (flags & 0x3) == 0x3 ? readInt(in):0;
            int yOffset = (flags & 0x5) == 0x5 ? readInt(in):0;
            int widthOffset = (flags & 0x8) != 0 ? readInt(in):0;
            int heightOffset = (flags & 0x10) != 0 ? readInt(in):0;
            this.width = width;
            this.height = height;
            this.pixels = pixels;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;
        }

        public void writeTo(OutputStream out0) throws IOException
        {
            DataOutputStream out = out0 instanceof DataOutputStream ? (DataOutputStream) out0:new DataOutputStream(out0);
            int flags = 0;
            int width = this.width;
            int height = this.height;
            int xOffset = this.xOffset;
            int yOffset = this.yOffset;
            int widthOffset = this.widthOffset;
            int heightOffset = this.heightOffset;
            if (width > 0 && height > 0)
            {
                flags |= 0x1;
                if (xOffset != 0)
                    flags |= 0x2;

                if (yOffset != 0)
                    flags |= 0x4;

            }
            if (widthOffset != 0)
                flags |= 0x8;

            if (heightOffset != 0)
                flags |= 0x10;

            out.writeByte(flags);
            if ((flags & 0x1) != 0)
            {
                byte[] pixels = this.pixels;
                writeInt(out, width);
                writeInt(out, height);
                int count = width * height;
                for (int i = 0; i != count; ++i)
                    out.writeByte(pixels[i]);

            }
            if ((flags & 0x3) == 0x3)
                writeInt(out, xOffset);

            if ((flags & 0x5) == 0x5)
                writeInt(out, yOffset);

            if ((flags & 0x8) != 0)
                writeInt(out, widthOffset);

            if ((flags & 0x10) != 0)
                writeInt(out, heightOffset);

        }

        public Rectangle getBounds()
        {
            return new Rectangle(xOffset, yOffset, widthOffset, heightOffset);
        }

        public final byte[] pixels;
        public final int width;
        public final int height;
        public final int xOffset;
        public final int yOffset;
        public final int widthOffset;
        public final int heightOffset;
    }

    private static final RSFontChar DUMMY_FONT_CHAR = new RSFontChar(new byte[0], 0, 0, 0, 0, 0, 0);
    private static final int FOREGROUND = 0xffffffff;
    private static final int BACKGROUND = 0x00000000;
    private boolean disposed;
    private RSFontChar[][] charCache;
    private int bufferWidth;
    private int bufferHeight;
    private int[] bufferPixels;
    private int bufferSize;
    private int ascent;
    private Graphics2D bufferGraphics;
    private BufferedImage buffer;
    private FontMetrics fontMetrics;
    public final Font font;
}