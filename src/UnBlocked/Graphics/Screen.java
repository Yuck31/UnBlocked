package UnBlocked.Graphics;
/**
 * Screen object to be utilized by each Graphics API.
 * 
 * Author: Luke Sullivan
 * Last Edit: 8/11/2022
 */
import org.joml.Vector4f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

//import JettersR.Entities.Components.Lights.Light;

public abstract class Screen
{
    //Width and Height of in-game screen.
    protected int WIDTH, HEIGHT;

    //Dimensions of GL Viewport.
    protected int VIEWPORT_X, VIEWPORT_Y, VIEWPORT_WIDTH, VIEWPORT_HEIGHT;
    protected boolean maintainAspectRatio = true, fitToScreen = true;

    //Default Vector3f for color blending.
    public static final Vector4f DEFAULT_BLEND = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    //Color bit values.
    public static final int RED_PORTION = 0x00FF0000, RED_OFFSET = 16,
    GREEN_PORTION = 0x0000FF00, GREEN_OFFSET = 8,
    BLUE_PORTION = 0x000000FF, BLUE_OFFSET = 0,
    ALPHA_PORTION = 0xFF000000, ALPHA_OFFSET = 24;

    public abstract void setDimensions(int w, int h, boolean API_Initialized);
    public final void setDimensions(int w, int h){setDimensions(w, h, true);}
    public abstract void init();

    public final int getWidth(){return WIDTH;}
    public final int getHeight(){return HEIGHT;}

    public final void setViewportDimensions(int x, int y, int width, int height)
    {
        this.VIEWPORT_X = x;
        this.VIEWPORT_Y = y;
        this.VIEWPORT_WIDTH = width;
        this.VIEWPORT_HEIGHT = height;
    }

    public final int getViewportX(){return VIEWPORT_X;}
    public final int getViewportY(){return VIEWPORT_Y;}
    public final int getViewportWidth(){return VIEWPORT_WIDTH;}
    public final int getViewportHeight(){return VIEWPORT_HEIGHT;}

    public final boolean maintainAspectRatio(){return maintainAspectRatio;}
    public final void maintainAspectRatio(boolean m){this.maintainAspectRatio = m;}
    
    public final boolean fitToScreen(){return fitToScreen;}
    public final void fitToScreen(boolean f){this.fitToScreen = f;}

    public abstract void render(long windowAddress);

    public abstract void clear();

    /*
     * EVERYTHING below this threshold will be applying Sprites to the Pixel Array
     */

    //Camera Offsets
    protected int xOffset = 0, yOffset = 0, zOffset = 0;
    protected float scaleOffset = 1.0f;

    public final int getXOffset(){return xOffset;}
    public final int getYOffset(){return yOffset;}
    public final int getZOffset(){return zOffset;}
    public final float getScaleOffset(){return scaleOffset;}

    public final void setXOffset(int xOffset){this.xOffset = xOffset;}
    public final void setYOffset(int yOffset){this.yOffset = yOffset;}
    public final void setZOffset(int zOffset)
    {
        //this.zOffset = zOffset;
        this.zOffset = (zOffset % 2 != 0) ? zOffset-1 : zOffset;
    }
    public final void setScaleOffset(float scaleOffset){this.scaleOffset = scaleOffset;}
    public final void setCameraOffsets(int xOffset, int yOffset, int zOffset)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        //this.zOffset = zOffset;
        this.zOffset = (zOffset % 2 != 0) ? zOffset-1 : zOffset;
    }
    public final void setCameraOffsets(int xOffset, int yOffset, int zOffset, float scaleOffset)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        //this.zOffset = zOffset;
        this.zOffset = (zOffset % 2 != 0) ? zOffset-1 : zOffset;
        this.scaleOffset = scaleOffset;
    }

    /**Resets renderBatches to clean out any unused SpriteSheets.*/
    public abstract void reset_RenderBatches();

    /**
     * SpriteSheet
     */
    public abstract void renderSheet(int xPos, int yPos, int zPos, SpriteSheet sheet, boolean fixed);
    public abstract void renderSheet(int xPos, int yPos, int zPos, SpriteSheet sheet, Vector4f blendingColor, boolean fixed);
    

    /**
     * Sprite
     */
    public abstract void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    //Same functions but with default crop values.
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, boolean fixed){renderSprite(xPos, yPos, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, boolean fixed){renderSprite(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    //
    //Same functions but with default wrap values.
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed){renderSprite(xPos, yPos, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, fixed);}
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed){renderSprite(xPos, yPos, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, fixed);}
    //
    //Same functions but with default wrap and crop values.
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, boolean fixed){renderSprite(xPos, yPos, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed){renderSprite(xPos, yPos, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed){renderSprite(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}


    /**
     * Scale
     */
    public abstract void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed);
    public abstract void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed);
    public abstract void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed);
    public abstract void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed);
    //
    //Default crop.
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    //
    //Default wrap.
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, xScale, yScale, fixed);}
    //
    //Default wrap and crop.
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}
    public final void renderSprite_Sc(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, float xScale, float yScale, boolean fixed){renderSprite_Sc(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, xScale, yScale, fixed);}

    /**
     * Stretch (scaled sprite but it takes dimensions as parameters instead of scales)
     */
    public abstract void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed);
    public abstract void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed);
    public abstract void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed);
    public abstract void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed);
    //
    //Default crop.
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    //
    //Default wrap.
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, 0, 0, cropX0, cropY0, cropX1, cropY1, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, cropX0, cropY0, cropX1, cropY1, resultWidth, resultHeight, fixed);}
    //
    //Default wrap and crop.
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, 0, 0, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}
    public final void renderSprite_St(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, int resultWidth, int resultHeight, boolean fixed){renderSprite_St(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, resultWidth, resultHeight, fixed);}


    /**
     * Shear
     */
    public abstract void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, boolean fixed);
    public abstract void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, boolean fixed);
    public abstract void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, float zxShear, float zyShear, boolean fixed);
    public abstract void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float xShear, float yShear, float zxShear, float zyShear, boolean fixed);
    //
    //Default Crop.
    public final void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, float xShear, float yShear, boolean fixed){renderSprite_Sh(xPos, yPos, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, xShear, yShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float xShear, float yShear, boolean fixed){renderSprite_Sh(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, xShear, yShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, float xShear, float yShear, float zxShear, float zyShear, boolean fixed){renderSprite_Sh(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, xShear, yShear, zxShear, zyShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float xShear, float yShear, float zxShear, float zyShear, boolean fixed){renderSprite_Sh(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, xShear, yShear, zxShear, zyShear, fixed);}
    //
    //Default Wrap and Crop.
    public final void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, float xShear, float yShear, boolean fixed){renderSprite_Sh(xPos, yPos, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, xShear, yShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, float xShear, float yShear, boolean fixed){renderSprite_Sh(xPos, yPos, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, xShear, yShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, float xShear, float yShear, float zxShear, float zyShear, boolean fixed){renderSprite_Sh(xPos, yPos, zPos, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, xShear, yShear, zxShear, zyShear, fixed);}
    public final void renderSprite_Sh(int xPos, int yPos, int zPos, Sprite sprite, byte flip, Vector4f blendingColor, float xShear, float yShear, float zxShear, float zyShear, boolean fixed){renderSprite_Sh(xPos, yPos, zPos, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, xShear, yShear, zxShear, zyShear, fixed);}


    /**
     * Rotate
     */
    public abstract void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed);
    public abstract void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed);
    public abstract void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed);
    public abstract void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float rads, int originX, int originY, boolean fixed);
    //
    //Default Crop.
    public final void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, zPos, depth, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    //
    //Default Wrap and Crop.
    public final void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, Sprite sprite, byte flip, Vector4f blendingColor, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, zPos, depth, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    public final void renderSprite_Ro(int xPos, int yPos, int zPos, int depth, Sprite sprite, byte flip, Vector4f blendingColor, float rads, int originX, int originY, boolean fixed){renderSprite_Ro(xPos, yPos, zPos, depth, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, rads, originX, originY, fixed);}
    

    
    /**
     * Tile-Specific (Renders a Sprite without overriding pixels already on the screen).
     */
    public abstract void renderTile(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float scale, byte shearType, float shear, boolean fixed);
    public abstract void renderTile(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float scale, byte shearType, float shear, boolean fixed);
    public abstract void renderTile_Ent(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float scale, byte shearType, float shear, boolean fixed);
    public abstract void renderTile_Ent(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, float scale, byte shearType, float shear, boolean fixed);
    //
    //Defaut Crop.
    public final void renderTile(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float scale, byte shearType, float shear, boolean fixed){renderTile(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, scale, shearType, shear, fixed);}
    public final void renderTile(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float scale, byte shearType, float shear, boolean fixed){renderTile(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, scale, shearType, shear, fixed);}
    public final void renderTile_Ent(int xPos, int yPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float scale, byte shearType, float shear, boolean fixed){renderTile_Ent(xPos, yPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, scale, shearType, shear, fixed);}
    public final void renderTile_Ent(int xPos, int yPos, int zPos, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, float scale, byte shearType, float shear, boolean fixed){renderTile_Ent(xPos, yPos, zPos, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, scale, shearType, shear, fixed);}

    

    /**
     * Renders a sprite using any combination of affine transformations via a Matrix4f.
     */
    public abstract void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    //
    public final void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, boolean fixed){renderSprite_Affine_2D(matrix, invertedMatrix, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, boolean fixed){renderSprite_Affine_2D(matrix, invertedMatrix, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, boolean fixed){renderSprite_Affine(matrix, invertedMatrix, sprite, flip, wrapX, wrapY, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, boolean fixed){renderSprite_Affine(matrix, invertedMatrix, sprite, flip, wrapX, wrapY, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    //
    //
    public final void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, boolean fixed){renderSprite_Affine_2D(matrix, invertedMatrix, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine_2D(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed){renderSprite_Affine_2D(matrix, invertedMatrix, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, boolean fixed){renderSprite_Affine(matrix, invertedMatrix, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void renderSprite_Affine(Matrix4f matrix, Matrix4f invertedMatrix, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed){renderSprite_Affine(matrix, invertedMatrix, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    

    /**Renders a sprite using 4 points.*/
    public abstract void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3,
    Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public abstract void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3,
    Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public abstract void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3,
    Sprite sprite, byte flip, int wrapX, int wrapY, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public abstract void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3,
    Sprite sprite, byte flip, int wrapX, int wrapY, Vector4f blendingColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    //
    public final void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Sprite sprite, byte flip, boolean fixed)
    {renderSprite_Quad(x0, y0, x1, y1, x2, y2, x3, y3, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    //
    public final void renderSprite_Quad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed)
    {renderSprite_Quad(x0, y0, x1, y1, x2, y2, x3, y3, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}
    //
    public final void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Sprite sprite, byte flip, boolean fixed)
    {renderSprite_Quad(x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, sprite, flip, 0, 0, 0, 0, WIDTH, HEIGHT, fixed);}
    //
    public final void renderSprite_Quad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Sprite sprite, byte flip, Vector4f blendingColor, boolean fixed)
    {renderSprite_Quad(x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, sprite, flip, 0, 0, blendingColor, 0, 0, WIDTH, HEIGHT, fixed);}


    /*
     * Debug related Functions.
     */

    /**Draws a Point.*/
    public abstract void drawPoint(int xPos, int yPos, Vector4f pointColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void drawPoint(int xPos, int yPos, int zPos, Vector4f pointColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void drawPoint(int xPos, int yPos, Vector4f pointColor, boolean fixed){drawPoint(xPos, yPos, pointColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void drawPoint(int xPos, int yPos, int zPos, Vector4f pointColor, boolean fixed){drawPoint(xPos, yPos, zPos, pointColor, 0, 0, WIDTH, HEIGHT, fixed);}


    /**Draws a Line.*/
    public abstract void drawLine(int x0, int y0, int x1, int y1, Vector4f color, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void drawLine(int x0, int y0, int z0, int x1, int y1, int z1, Vector4f color, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void drawLine(int x0, int y0, int x1, int y1, Vector4f color, boolean fixed){drawLine(x0, y0, x1, y1, color, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void drawLine(int x0, int y0, int z0, int x1, int y1, int z1, Vector4f color, boolean fixed){drawLine(x0, y0, z0, x1, y1, z1, color, 0, 0, WIDTH, HEIGHT, fixed);}


    /**Draws a Rectangle.*/
    public abstract void drawRect(int xPos, int yPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void drawRect(int xPos, int yPos, int zPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void drawRect(int xPos, int yPos, int w, int h, Vector4f vecColor, boolean fixed){drawRect(xPos, yPos, w, h, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void drawRect(int xPos, int yPos, int zPos, int w, int h, Vector4f vecColor, boolean fixed){drawRect(xPos, yPos, zPos, w, h, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}


    /**Renders a filled Rectangle.*/
    public abstract void fillRect(int xPos, int yPos, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void fillRect(int xPos, int yPos, int zPos, int depth, int w, int h, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void fillRect(int xPos, int yPos, int w, int h, Vector4f vecColor, boolean fixed){fillRect(xPos, yPos, w, h, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void fillRect(int xPos, int yPos, int zPos, int depth, int w, int h, Vector4f vecColor, boolean fixed){fillRect(xPos, yPos, zPos, depth, w, h, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
    

    /**Draws a Quad.*/
    public abstract void drawQuad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void drawQuad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);


    /**Renders a filled Quad.*/
    public abstract void fillQuad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void fillQuad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void fillQuad(int x0, int y0, int x1, int y1, int x2, int y2, int x3, int y3, Vector4f vecColor, boolean fixed){fillQuad(x0, y0, x1, y1, x2, y2, x3, y3, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void fillQuad(int x0, int y0, int z0, int x1, int y1, int z1, int x2, int y2, int z2, int x3, int y3, int z3, Vector4f vecColor, boolean fixed){fillQuad(x0, y0, z0, x1, y1, z1, x2, y2, z2, x3, y3, z3, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}

    //Constant used mostly for circle-related stuff.
    public static final double TWO_PI = 2 * Math.PI;


    /**Renders a Circle.*/
    public abstract void drawCircle(int xPos, int yPos, float radius, int thickness, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    public abstract void drawCircle(int xPos, int yPos, int zPos, float radius, int thickness, Vector4f vecColor, int cropX0, int cropY0, int cropX1, int cropY1, boolean fixed);
    //
    public final void drawCircle(int xPos, int yPos, float radius, int thickness, Vector4f vecColor, boolean fixed){drawCircle(xPos, yPos, radius, thickness, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
    public final void drawCircle(int xPos, int yPos, int zPos, float radius, int thickness, Vector4f vecColor, boolean fixed){drawCircle(xPos, yPos, zPos, radius, thickness, vecColor, 0, 0, WIDTH, HEIGHT, fixed);}
   


    /*
     * Calculation Stuff
     */

    /**Converts a Vector4f color to an int color.*/
    public static int vector4fToInt(Vector4f vec)
    {
        return ((int)(vec.x * 255) << RED_OFFSET)
        | ((int)(vec.y * 255) << GREEN_OFFSET)
        | ((int)(vec.z * 255) << BLUE_OFFSET)
        | ((int)(vec.w * 255) << ALPHA_OFFSET);
    }

    /**Converts a Vector4f color to an int color.*/
    public static int vector3f_a_ToInt(Vector3f vec, float alpha)
    {
        return ((int)(vec.x * 255) << RED_OFFSET)
        | ((int)(vec.y * 255) << GREEN_OFFSET)
        | ((int)(vec.z * 255) << BLUE_OFFSET)
        | ((int)(alpha * 255) << ALPHA_OFFSET);
    }

    /**Converts an int normal to a Vector3f normal.*/
    public static Vector3f intToVector3f_Normal(int i)
    {
        return new Vector3f
        (
            ((((i & 0x00FF0000) >> RED_OFFSET) / 255.0f) * 2.0f) - 1.0f,
            ((((i & 0x0000FF00) >> GREEN_OFFSET) / 255.0f) * 2.0f) - 1.0f,
            ((((i & 0x000000FF) >> BLUE_OFFSET) / 255.0f) * 2.0f) - 1.0f
        );
    }

    /**Converts an int color to a Vector4f color.*/
    public static Vector4f intToVector4f(int i)
    {
        return new Vector4f
        (
            ((i & 0x00FF0000) >> RED_OFFSET) / 255.0f,
            ((i & 0x0000FF00) >> GREEN_OFFSET) / 255.0f,
            ((i & 0x000000FF) >> BLUE_OFFSET) / 255.0f,
            (((i & 0xFF000000) >> ALPHA_OFFSET) & 0xFF) / 255.0f
        );
    }

    /**Converts two shorts to a Vector4f color.*/
    public static Vector4f shortsToVector(short s0, short s1)
    {
        return new Vector4f
        (
            (s0 & 0x00FF) / 255.0f,
            (((s1 & 0xFF00) >> 8) & 0xFF) / 255.0f,
            (s1 & 0x00FF) / 255.0f,
            (((s0 & 0xFF00) >> 8) & 0xFF) / 255.0f
        );
    }

    public static void setShortsToVector(short s0, short s1, Vector4f dest)
    {
        dest.set
        (
            (s0 & 0x00FF) / 255.0f,
            (((s1 & 0xFF00) >> 8) & 0xFF) / 255.0f,
            (s1 & 0x00FF) / 255.0f,
            (((s0 & 0xFF00) >> 8) & 0xFF) / 255.0f
        );
    }

    /**Converts 4 bytes to a Vector4f color.*/
    public static Vector4f bytesToVector4f(byte[] b)
    {
        return new Vector4f
        (
            (b[1] & 0xFF) / 255.0f,
            (b[2] & 0xFF) / 255.0f,
            (b[3] & 0xFF) / 255.0f,
            (b[0] & 0xFF) / 255.0f
        );
    }

    /**Calculates the result of blending a color with a Vector3f blending color (0.0f to 1.0f for R, G, and B).*/
    public static int multipliedColor(int color, Vector4f blendingColor)
    {
        //If BlendingColor is opaque white, return the given color.
        return (blendingColor.x >= 1.0f && blendingColor.y >= 1.0f
        && blendingColor.z >= 1.0f && blendingColor.w >= 1.0f) ? color
        
        //Otherwise, blend it.
        : ((int)((((color & ALPHA_PORTION) >> ALPHA_OFFSET) & 0xFF) * blendingColor.w) << ALPHA_OFFSET) |
        ((int)(((color & RED_PORTION) >> RED_OFFSET) * blendingColor.x) << RED_OFFSET) |
        ((int)(((color & GREEN_PORTION) >> GREEN_OFFSET) * blendingColor.y) << GREEN_OFFSET) |
        ((int)(((color & BLUE_PORTION) >> BLUE_OFFSET) * blendingColor.z) << BLUE_OFFSET);
    }

    /**Calculates the result of transluceny of two given colors.*/
    public static int translucentColor(int topColor, int bottomColor)
    {
        //Opacque or transparent check.
        int topColor_Alpha = (topColor & ALPHA_PORTION);
        if(topColor_Alpha == 0x00000000){return bottomColor;}
        else if(topColor_Alpha == ALPHA_PORTION){return topColor;}

        int alpha = (topColor_Alpha >> ALPHA_OFFSET) & 0xff;

        //Takes the alpha of the Top Color into 0.0f to 1.0f form.
        float opacity = (float)(alpha) / 255.0f,
        deltaOpacity = (1f-opacity);

        //A hexadecimal digit = 4 bits
        //This extracts the reds, greens, and blues of color0
        //and shifts the bits to make them easily editable.
        int
        red0   = (int)(((topColor & RED_PORTION) >> RED_OFFSET) * opacity),
        green0 = (int)(((topColor & GREEN_PORTION) >> GREEN_OFFSET) * opacity),
        blue0  = (int)(((topColor & BLUE_PORTION) >> BLUE_OFFSET) * opacity);
        //They are also multiplied by the given opacity

        //Then it extracts the red, green, and blue values from color1...
        int
        red1   = (int)(((bottomColor & RED_PORTION) >> RED_OFFSET) * deltaOpacity),
        green1 = (int)(((bottomColor & GREEN_PORTION) >> GREEN_OFFSET) * deltaOpacity),
        blue1  = (int)(((bottomColor & BLUE_PORTION) >> BLUE_OFFSET) * deltaOpacity);
        //1-Opacity is used to get the delta of max opacity and the given opacity.

        //Add the results together.
        red1 += red0; green1 += green0; blue1 += blue0;

        //Limit values of the colors.
        red1 = (red1 > 0xFF) ? 0xFF : (red1 < 0x00) ? 0x00 : red1;
        //
        green1 = (green1 > 0xFF) ? 0xFF : (green1 < 0x00) ? 0x00 : green1;
        //
        blue1 = (blue1 > 0xFF) ? 0xFF : (blue1 < 0x00) ? 0x00 : blue1;

        //The final result.
        return (alpha << ALPHA_OFFSET) | (red1 << RED_OFFSET) | (green1 << GREEN_OFFSET) | (blue1 << BLUE_OFFSET);
        //This took some experimentation in piskel to figure this out by the way.
    }

    /*
    public static float[] forward(Matrix4f matrix, float x, float y)
    {
        return new float[]
        {
            (x * matrix.m00()) + (y * matrix.m10()) + matrix.m20(),
            (x * matrix.m01()) + (y * matrix.m11()) + matrix.m21()
        };
    }
    */
    
    public static void forward(Matrix4f matrix, Vector4f vec)
    {
        vec.set
        (
            (vec.x * matrix.m00()) + (vec.y * matrix.m10()) + matrix.m20(),
            (vec.x * matrix.m01()) + (vec.y * matrix.m11()) + matrix.m21(),
            0,
            0
        );
    }
}
