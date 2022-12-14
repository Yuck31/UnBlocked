package UnBlocked.Entities;
/**
 * 
 */
import org.joml.Vector2f;

import UnBlocked.Level;
import UnBlocked.TileRenderable;

public abstract class Entity implements TileRenderable
{
    //Level.
    protected Level level = null;

    //Position.
    protected Vector2f position = new Vector2f();
    protected int tileX, tileY;

    /**Constructor.*/
    public Entity(int tileX, int tileY){setTilePosition(tileX, tileY);}

    //Level Setter.
    public final void init(Level level){this.level = level;}

    //Is solid.
    public abstract boolean isSolid();


    //
    //Position Getters.
    //
    public final Vector2f getPosition(){return this.position;}

    
    //
    //Position setters.
    //
    public final void setX(float x){this.position.x = x;}
    public final void setY(float y){this.position.y = y;}
    public final void setPosition(float x, float y){this.position.set(x, y);}

    public final void setTileX(int tileX)
    {
        this.tileX = tileX;
        position.x = tileX << Level.TILE_BITS;
    }

    public final void setTileY(int tileY)
    {
        this.tileY = tileY;
        position.y = tileY << Level.TILE_BITS;
    }

    public final void setTilePosition(int tileX, int tileY)
    {
        this.tileX = tileX;
        this.tileY = tileY;
        position.set(tileX << Level.TILE_BITS, tileY << Level.TILE_BITS);
    }

    /**Update function.*/
    public void update(float timeMod){}
}
