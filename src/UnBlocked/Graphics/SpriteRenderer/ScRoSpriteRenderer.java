package UnBlocked.Graphics.SpriteRenderer;
import org.joml.Vector2f;

/**
 * 
 */
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;

public class ScRoSpriteRenderer extends SpriteRenderer
{
    @FunctionalInterface//Render Function
    public interface RenderFunction{public abstract void invoke(Screen screen, float x, float y, float z, float scaleOffset);}
    public RenderFunction renderFunction = null;

    //Scale values for the Sprite.
    private float xScale = 1.0f, yScale = 1.0f;

    //Rotation Degrees.
    private float rads = 0f;

    //Rotation Origin point.
    private final Vector2f origin = new Vector2f();

    /**Constructor.*/
    public ScRoSpriteRenderer(Sprite sprite, int xOffset, int yOffset, float xScale, float yScale, float degrees, int originX, int originY, boolean fixed, boolean canColorBlend)
    {
        this.sprite = sprite;
        this.offset.set(xOffset, yOffset);
        this.fixed = fixed;

        setDegrees(degrees);
        this.origin.x = originX;
        this.origin.y = originY;

        if(canColorBlend){renderFunction = this::renderBlend_ScRo;}
        else{renderFunction = this::renderNoBlend_ScRo;}
    }


    //
    //Scale
    //

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


    //
    //Rotation
    //

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



    //Render Function.
    private void renderNoBlend_ScRo(Screen screen, float x, float y, float z, float scaleOffset)
    {
        float xs = xScale * scaleOffset, ys = yScale * scaleOffset;
        //
        screen.renderSprite_ScRo
        (
            (int)((x + (((flip & Sprite.FLIP_X) == 1) ? -(sprite.getWidth() + offset.x) : offset.x)) * scaleOffset),
            (int)((y + offset.y) * scaleOffset),
            sprite, flip, 0, 0,
            xs, ys,
            rads, (int)origin.x, (int)origin.y,
            fixed
        );

        //origin.x = (origin.x + 0.1f) % 10.0f;
        //origin.y = (origin.y + 0.1f) % 10.0f;
        //addRads(0.01f);
    }

    //Render Function.
    private void renderBlend_ScRo(Screen screen, float x, float y, float z, float scaleOffset)
    {
        float xs = xScale * scaleOffset, ys = yScale * scaleOffset;
        //
        screen.renderSprite_ScRo
        (
            (int)((x + offset.x) * scaleOffset),
            (int)((y + offset.y) * scaleOffset),
            sprite, flip, 0, 0, blendingColor,
            xs, ys,
            rads, (int)(origin.x * xScale), (int)(origin.y * yScale),
            fixed
        );
    }


    @Override
    /**Render Function.*/
    public void render(Screen screen, Vector2f position)
    {renderFunction.invoke(screen, position.x, position.y, 0, 1.0f);}
    
    /**Render Function.*/
    public void render(Screen screen, Vector2f position, float scaleOffset)
    {renderFunction.invoke(screen, position.x, position.y, 0, scaleOffset);}

    @Override
    /**Render Function.*/
    public void render(Screen screen, float x, float y, float z)
    {renderFunction.invoke(screen, x, y, z, 1.0f);}

    @Override
    public void render(Screen screen, int x, int y)
    {
        screen.renderSprite_ScRo
        (
            (int)(x + offset.x),
            (int)(y + offset.y),
            sprite, flip, xScale, yScale,
            rads, (int)(origin.x * xScale), (int)(origin.y * yScale),
            fixed
        );
    }
}
