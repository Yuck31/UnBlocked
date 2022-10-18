package UnBlocked.Graphics.SpriteRenderer;
/**
 * A Sprite Renderer that can render a Rotated Sprite.
 * 
 * Author: Luke Sullivan
 * Last Edit: 10/13/2022
 */
import org.joml.Vector2f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public class RotateSpriteRenderer extends SpriteRenderer
{
    @FunctionalInterface//Render Function
    public interface RenderFunction{public abstract void invoke(Screen screen, float x, float y, float z);}
    public RenderFunction renderFunction = null;

    //Rotation Degrees.
    private float rads = 0f;

    //Rotation Origin point.
    private final Vector2f origin = new Vector2f();

    /**Constructor.*/
    public RotateSpriteRenderer(Sprite sprite, int xOffset, int yOffset, float degrees, int originX, int originY, boolean fixed, boolean canColorBlend)
    {
        this.sprite = sprite;
        this.offset.set(xOffset, yOffset);
        this.fixed = fixed;

        setDegrees(degrees);
        this.origin.x = originX;
        this.origin.y = originY;

        if(canColorBlend){renderFunction = this::renderBlend_Ro;}
        else{renderFunction = this::renderNoBlend_Ro;}
    }

    /**Default Center Offset Constructor.*/
    public RotateSpriteRenderer(Sprite sprite, boolean fixed, boolean canColorBlend)
    {this(sprite, -(sprite.getWidth()/2), -(sprite.getHeight()/2), 0f, (sprite.getWidth()/2), (sprite.getHeight()/2), fixed, canColorBlend);}

    //Getters
    public float getRads(){return rads;}
    public float getDegrees(){return (float)(rads * (180/Math.PI));}

    public float getOriginX(){return origin.x;}
    public float getOriginY(){return origin.y;}

    //Setters
    public void setRads(float rads){this.rads = rads;}
    public void setDegrees(float degrees){this.rads = (float)(degrees * (Math.PI/180));}

    public void setOriginX(int originX){this.origin.x = originX;}
    public void setOriginY(int originY){this.origin.y = originY;}
    public void setOrigin(int originX, int originY){this.origin.set(originX, originY);}

    public static final float MAX_RADS = (float)(360 * (Math.PI/180));

    //Adder
    public void addRads(float rads){this.rads += rads;}
    public void addDegrees(float degrees){this.rads += (float)(degrees * (Math.PI/180)) % MAX_RADS;}

    //protected Matrix4f mat = new Matrix4f().ortho(0, 640, 360, 0, 360, 0);
    //protected Vector4f vec = new Vector4f();

    //Render Function.
    private void renderNoBlend_Ro(Screen screen, float x, float y, float z)
    {
        screen.renderSprite_Ro
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)z,
            sprite, flip, rads, (int)origin.x, (int)origin.y, fixed
        );

        //addRads(0.01f);
    }

    //Render Function.
    private void renderBlend_Ro(Screen screen, float x, float y, float z)
    {
        screen.renderSprite_Ro
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)z,
            sprite, flip, blendingColor, rads, (int)origin.x, (int)origin.y, fixed
        );
    }

    /**Render Function.*/
    @Override
    public void render(Screen screen, Vector2f position)
    {renderFunction.invoke(screen, position.x, position.y, 0);}

    /**Render Function.*/
    @Override
    public void render(Screen screen, float x, float y, float z)
    {renderFunction.invoke(screen, x, y, z);}

    /**UI Render Function.*/
    @Override
    public void render(Screen screen, int x, int y)
    {
        screen.renderSprite_Ro((int)(x + offset.x), (int)(y + offset.y),
        sprite, flip, rads, (int)origin.x, (int)origin.y, fixed);
    }
}
