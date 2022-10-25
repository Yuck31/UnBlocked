package UnBlocked;
/**
 * 
 */
import org.joml.Vector2f;

import UnBlocked.Util.Shapes2D.AAB_Box2D;

public class LevelCamera
{
    //Level instance.
    private Level level = null;

    //Camera Shape.
    private AAB_Box2D shape = null;

    //Camera position.
    private Vector2f position = new Vector2f(), potentialPosition = new Vector2f(),
    entityPosition = new Vector2f();
    private float scale = 1.0f;

    //Rather this camera is bounded by camera boundaries or not.
    private boolean isBounded = true;

    /**Constructor.*/
    public LevelCamera(Level level, int width, int height)
    {
        this.level = level;
        shape = new AAB_Box2D(width, height, 0, 0);
    }

    /**Constructor.*/
    public LevelCamera(Level level){this(level, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);}

    /**Updates this camera's position.*/
    public void update()
    {
        if(isBounded)
        {
            //Set potential position relative to entity.
            potentialPosition.set
            (
                (entityPosition.x) - ((shape.getWidth() >> 1)),
                (entityPosition.y) - ((shape.getHeight() >> 1))
            );


            //Check collision with the level boundaries and move accordingly.
            float right = potentialPosition.x + shape.getWidth(),
            levelWidth = level.getWidth() << Level.TILE_BITS;
            
            if(potentialPosition.x < 0)
            {
                potentialPosition.x = (right > levelWidth) ?
                (shape.getWidth() - levelWidth) / 2 : 0;
            }
            else if(right > levelWidth)
            {
                potentialPosition.x = (potentialPosition.x < 0) ?
                (shape.getWidth() - levelWidth) / 2 : levelWidth - shape.getWidth();
            }

            //NOW set the actual position.
            //position.set(potentialPosition);
        }
    }


    public float getX(){return position.x;}
    public int getScaledX(){return (int)(position.x * scale);}
    //
    public float getY(){return position.y;}
    public int getScaledY(){return (int)(position.y * scale);}

    public Vector2f getPosition(){return position;}
    public void setPosition(float x, float y){this.position.set(x, y);}
    public void addPosition(float xv, float yv){this.position.add(xv, yv);}

    //Scale Getter/Setter/Adder.
    public float getScale(){return scale;}
    public void setScale(float scale, int width, int height)
    {
        //Set scale value.
        this.scale = scale;

        //Calculate new width and height.
        int w = (int)(width / this.scale),
        h = (int)(height / this.scale);

        //Update Box.
        shape.setDimensions(w, h);
    }
    public void addScale(float scale, int width, int height)
    {
        this.scale += scale;

        //Calculate new width and height.
        int w = (int)(width / this.scale),
        h = (int)(height / this.scale);

        //Update Box.
        shape.setDimensions(w, h);
    }

    public void setBounded(boolean isBounded){this.isBounded = isBounded;}
}
