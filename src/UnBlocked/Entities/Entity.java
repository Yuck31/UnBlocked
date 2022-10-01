package UnBlocked.Entities;
/**
 * 
 */
import org.joml.Vector2i;

public abstract class Entity
{
    //Position.
    Vector2i position = new Vector2i();

    /**Constructor.*/
    public Entity(int x, int y){position.set(x, y);}

    public void update(){}
}
