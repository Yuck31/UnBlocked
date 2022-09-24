package UnBlocked.UI;
/**
 * Component for UI.
 * 
 * Author: Luke Sullivan
 * Last Edit: 3/15/2022
 */
import org.joml.Vector2i;

public abstract class UIComponent
{
    //Coordinates of this UIPanel
    public final Vector2i position = new Vector2i();

    /**Constructor.*/
    public UIComponent(int x, int y){position.set(x, y);}

    public int getX(){return position.x;}
    public int getY(){return position.y;}
}
