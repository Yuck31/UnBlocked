package UnBlocked.Graphics.SpriteRenderer;
/**
 * 
 */
import org.joml.Vector2f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public class ScaleSpriteRenderer extends SpriteRenderer
{
    @FunctionalInterface//Render Function
    public interface RenderFunction{public abstract void invoke(Screen screen, float x, float y, float z, float scaleOffset);}
    public RenderFunction renderFunction = null;

    //Scale values for the Sprite.
    private float xScale = 1.0f, yScale = 1.0f;

    
    /**Constructor.*/
    public ScaleSpriteRenderer(Sprite sprite, int xOffset, int yOffset, int zOffset, boolean fixed, boolean canColorBlend)
    {
        this.sprite = sprite;
        this.offset.set(xOffset, yOffset);
        this.fixed = fixed;

        if(canColorBlend){renderFunction = this::renderBlend_Sc;}
        else{renderFunction = this::renderNoBlend_Sc;}
    }

    /**Default Center Offset Constructor.*/
    public ScaleSpriteRenderer(Sprite sprite, boolean fixed, boolean canColorBlend)
    {this(sprite, -(sprite.getWidth()/2), -(sprite.getHeight()/2), 0, fixed, canColorBlend);}

    //Getters
    public float getXScale(){return this.xScale;}
    public float getYScale(){return this.yScale;}

    //Setters
    public void setXScale(float xScale){this.xScale = xScale;}
    public void setYScale(float yScale){this.yScale = yScale;}
    public void setScale(float xScale, float yScale){this.xScale = xScale; this.yScale = yScale;}
    public void setScale(float scale){this.xScale = scale; this.yScale = scale;}
    public void setScaleAndCenter(float scale)
    {
        this.xScale = scale;
        this.yScale = scale;
        offset.x = (-sprite.getWidth()/2f) * scale;
        offset.y = (-sprite.getHeight()/2f) * scale;
    }

    //Add Scale
    public void addXScale(float xScale){this.xScale += xScale;}
    public void addYScale(float yScale){this.yScale += yScale;}

    //Render Function.
    private void renderNoBlend_Sc(Screen screen, float x, float y, float z, float scaleOffset)
    {
        float xs = xScale * scaleOffset, ys = yScale * scaleOffset;
        //
        screen.renderSprite_Sc
        (
            (int)((x + offset.x) * scaleOffset),
            (int)((y + offset.y + sprite.getHeight()) * scaleOffset),
            (int)(z),
            sprite, flip, 0, 0, xs, ys, fixed
        );
    }

    //Render Function.
    private void renderBlend_Sc(Screen screen, float x, float y, float z, float scaleOffset)
    {
        float xs = xScale * scaleOffset, ys = yScale * scaleOffset;
        //
        screen.renderSprite_Sc
        (
            (int)((x + offset.x) * scaleOffset),
            (int)((y + offset.y) * scaleOffset),
            //(int)(((z/2f) - sprite.getHeight()) * scaleOffset), (int)(z * scaleOffset),
            (int)(z),
            sprite, flip, 0, 0, blendingColor, xs, ys, fixed
        );
    }

    //private int w0 = 0, w1 = 0;

    /**Camera-Related Render Function.*/
    public void render(Screen screen, Vector2f position, float scaleOffset)
    {renderFunction.invoke(screen, position.x, position.y, 0, scaleOffset);}

    /**Camera-Related Render Function.*/
    public void render(Screen screen, float x, float y, float z, float scaleOffset)
    {renderFunction.invoke(screen, x, y, z, scaleOffset);}

    @Override
    /**Render Function.*/
    public void render(Screen screen, Vector2f position)
    {renderFunction.invoke(screen, position.x, position.y, 0, 1.0f);}

    @Override
    /**Render Function.*/
    public void render(Screen screen, float x, float y, float z)
    {renderFunction.invoke(screen, x, y, z, 1.0f);}

    /**UI Render Function.*/
    @Override
    public void render(Screen screen, int x, int y)
    {
        screen.renderSprite_Sc
        (
            (int)(x + offset.x),
            //(int)((x + offset.x) - (((sprite.getWidth() * xScale) - sprite.getWidth()) / 2)),
            (int)(y + offset.y),
            //(int)((y + offset.y) - (((sprite.getHeight() * yScale) - sprite.getHeight()) / 2)),
            sprite, flip, xScale, yScale, fixed
        );
    }
}
