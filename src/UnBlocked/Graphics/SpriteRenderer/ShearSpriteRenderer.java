package UnBlocked.Graphics.SpriteRenderer;
/**
 * Author: Luke Sullivan
 * Last Edit: 8/11/2022
 */
import org.joml.Vector2f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public class ShearSpriteRenderer extends SpriteRenderer
{
    @FunctionalInterface//Render Function
    public interface RenderFunction{public abstract void invoke(Screen screen, float x, float y, float z);}
    public RenderFunction renderFunction = null;

    private float xShear = 0.0f, yShear = 0.0f;

    /**Constructor.*/
    public ShearSpriteRenderer(Sprite sprite, int xOffset, int yOffset, boolean fixed, boolean canColorBlend)
    {
        this.sprite = sprite;
        this.offset.set(xOffset, yOffset);
        this.fixed = fixed;
        //
        if(canColorBlend){renderFunction = this::renderBlend_Sh;}
        else{renderFunction = this::renderNoBlend_Sh;}
    }

    /**Default Constructor.*/
    public ShearSpriteRenderer(Sprite sprite, boolean fixed, boolean canColorBlend)
    {this(sprite, -(sprite.getWidth()/2), -(sprite.getHeight()/2), fixed, canColorBlend);}

    public float getXShear(){return xShear;}
    public void setXShear(float xShear){this.xShear = xShear;}

    public float getYShear(){return yShear;}
    public void setYShear(float yShear){this.yShear = yShear;}


    public void setShear(float xShear, float yShear)
    {
        this.xShear = xShear;
        this.yShear = yShear;
    }

    private float sx = 0;

    //Render Function.
    private void renderNoBlend_Sh(Screen screen, float x, float y, float z)
    {
        screen.renderSprite_Sh
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)(z),
            //(int)((z + offset.z + (sprite.getHeight() * 2))),
            sprite, flip, -sx, sx, 0, 0, fixed
        );

        sx = ((sx + 0.01f) % 5);
        //if(sx < 1){sx = 1;}
        //sx = 2.3f;
        //System.out.println(sx);
    }

    //Render Function.
    private void renderBlend_Sh(Screen screen, float x, float y, float z)
    {
        screen.renderSprite_Sh
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)((z/2f) - sprite.getHeight()), 
            sprite, flip, blendingColor, xShear, yShear, 0, 0, fixed
        );
    }

    /**Render Function.*/
    @Override
    public void render(Screen screen, Vector2f position)
    {renderFunction.invoke(screen, position.x, position.y, 0.0f);}

    /**Render Function.*/
    @Override
    public void render(Screen screen, float x, float y, float z)
    {renderFunction.invoke(screen, x, y, z);}

    /**UI Render Function.*/
    @Override
    public void render(Screen screen, int x, int y)
    {
        screen.renderSprite_Sh((int)(x + offset.x), (int)(y + offset.y),
        sprite, flip, xShear, yShear, fixed);
    }
}
