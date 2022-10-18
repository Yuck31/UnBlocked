package UnBlocked.Graphics.SpriteRenderer;
/**
 * Author: Luke Sullivan
 * Last Edit: 10/13/2022
 */
import org.joml.Vector2f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public class BasicSpriteRenderer extends SpriteRenderer
{
    @FunctionalInterface//Render Function Interface
    public interface RenderFunction{public abstract void invoke(Screen screen, float x, float y, float z);}
    public RenderFunction renderFunction = null;

    /**Constructor.*/
    public BasicSpriteRenderer(Sprite sprite, int xOffset, int yOffset, boolean fixed, boolean canColorBlend)
    {
        this.sprite = sprite;
        this.offset.set(xOffset, yOffset);
        this.fixed = fixed;

        if(canColorBlend){renderFunction = this::renderBlend;}
        else{renderFunction = this::renderNoBlend;}
    }

    /**Default Center Offset Constructor.*/
    public BasicSpriteRenderer(Sprite sprite, boolean fixed, boolean canColorBlend)
    {this(sprite, -(sprite.getWidth()/2), -(sprite.getHeight()/2), fixed, canColorBlend);}

    //Render Function.
    private void renderNoBlend(Screen screen, float x, float y, float z)
    {
        screen.renderSprite
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)z,
            sprite, flip, fixed
        );
    }

    //Render Function.
    private void renderBlend(Screen screen, float x, float y, float z)
    {
        screen.renderSprite
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            (int)z,
            sprite, flip, blendingColor, fixed
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
        screen.renderSprite
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            sprite, flip, fixed
        );
    }
}
