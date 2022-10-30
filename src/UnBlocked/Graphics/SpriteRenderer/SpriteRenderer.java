package UnBlocked.Graphics.SpriteRenderer;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/15/2022
 */
//import org.joml.Vector3i;
import org.joml.Vector2f;
import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public abstract class SpriteRenderer
{
    protected boolean fixed = true;

    //Sprite Object.
    protected Sprite sprite = null;

    //Offset.
    public final Vector2f offset = new Vector2f();

    //Flip Attribute.
    protected byte flip = Sprite.FLIP_NONE;

    //Vector4f representing the color to blend with.
    protected Vector4f blendingColor = new Vector4f();

    /**Gets this SpriteRenderer's Sprite.*/
    public final Sprite getSprite(){return sprite;}

    /**Sets this SpriteRenderer's Sprite.*/
    public final void setSprite(Sprite sprite){this.sprite = sprite;}

    //Offset Getters.
    public final float getXOffset(){return offset.x;}
    public final float getYOffset(){return offset.y;}
    public final Vector2f getOffset(){return offset;}

    //Offset setters.
    public final void setXOffset(int xOffset){this.offset.x = xOffset;}
    public final void setYOffset(int yOffset){this.offset.y = yOffset;}
    public final void setOffset(int xOffset, int yOffset){this.offset.set(xOffset, yOffset);}
    public final void setOffset(Vector2f offset){this.offset.set(offset);}

    //Offset adders.
    public final void addXOffset(int xOffset){this.offset.x += xOffset;}
    public final void addYOffset(int yOffset){this.offset.y += yOffset;}

    //FlipAttribute Getter/Setter
    public final byte getFlip(){return this.flip;}
    public final void setFlip(byte flip){this.flip = flip;}
    
    /**Sets this SpriteRenderer's blendingColor.*/
    public final void setBlendingColor(float r, float g, float b, float a){blendingColor.set(r, g, b, a);}
    public final void setBlendingColor(Vector4f color){blendingColor.set(color);}

    /**Render Function.*/
    public abstract void render(Screen screen, Vector2f position);

    /**Render Function.*/
    public abstract void render(Screen screen, float x, float y, float z);
    public void render(Screen screen, float x, float y){render(screen, x, y, 0.0f);}

    /**UI Render Function.*/
    public abstract void render(Screen screen, int x, int y);
}
