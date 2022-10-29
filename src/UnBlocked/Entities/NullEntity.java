package UnBlocked.Entities;
/**
 * 
 */
import UnBlocked.Graphics.Screen;

public class NullEntity extends Entity
{
    /**Constructor.*/
    public NullEntity(int tileX, int tileY)
    {
        super(tileX, tileY);
    }

    @Override
    public void render(Screen screen, int x, int y, float scale){return;}

    @Override
    public boolean isSolid(){return false;}
}
