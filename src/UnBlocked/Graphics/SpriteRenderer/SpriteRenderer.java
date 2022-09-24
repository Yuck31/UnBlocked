package UnBlocked.Graphics.SpriteRenderer;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/15/2022
 */
//import org.joml.Vector3i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public abstract class SpriteRenderer
{
    protected boolean fixed = true;

    //Sprite Object.
    protected Sprite sprite = null;

    //Offset.
    public final Vector3f offset = new Vector3f();

    //Flip Attribute.
    protected byte flip = Sprite.FLIP_NONE;

    //Vector4f representing the color to blend with.
    protected Vector4f blendingColor = new Vector4f();

    /**Sets this SpriteRenderer's Sprite.*/
    public final void setSprite(Sprite sprite){this.sprite = sprite;}

    //Offset Getters.
    public final float getXOffset(){return offset.x;}
    public final float getYOffset(){return offset.y;}
    public final float getZOffset(){return offset.z;}
    public final Vector3f getOffset(){return offset;}

    //Offset setters.
    public final void setXOffset(int xOffset){this.offset.x = xOffset;}
    public final void setYOffset(int yOffset){this.offset.y = yOffset;}
    public final void setOffset(int xOffset, int yOffset, int zOffset){this.offset.set(xOffset, yOffset, zOffset);}
    public final void setOffset(Vector3f offset){this.offset.set(offset);}

    //FlipAttribute Getter/Setter
    public final byte getFlip(){return this.flip;}
    public final void setFlip(byte flip){this.flip = flip;}
    
    /**Sets this SpriteRenderer's blendingColor.*/
    public final void setBlendingColor(float r, float g, float b, float a){blendingColor.set(r, g, b, a);}

    /**Render Function.*/
    public abstract void render(Screen screen, Vector3f position);

    /**Render Function.*/
    public abstract void render(Screen screen, float x, float y, float z);

    /**UI Render Function.*/
    public abstract void render(Screen screen, int x, int y);
}
