package UnBlocked.UI.Menus;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/22/2022
 */
import UnBlocked.Game;
import UnBlocked.Graphics.Screen;
//import UnBlocked.Controller;
//import UnBlocked.Mouse;
import UnBlocked.Util.Shapes2D.Shape2D;

public class BasicMenu extends Menu
{
    protected int currentChoice = 0;
    
    /**Constructor.*/
    public BasicMenu(int x, int y, Shape2D shape, MenuComponent... components)
    {super(x, y, shape, components);}

    /**Default Constructor.*/
    public BasicMenu(Shape2D shape, MenuComponent... components){this(0, 0, shape, components);}

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        if(selected)
        {
            for(int i = 0; i < menuComponents.size(); i++)
            {
                //Cache the component.
                MenuComponent m = menuComponents.get(i);
                boolean mouseIntersects = false;

                //Mouse Check.
                if(m.intersects(this.position.x, this.position.y, Game.mouse, true))
                {
                    currentChoice = i;
                    mouseIntersects = true;
                }

                //Update Menu Choice.
                if(m.update(currentChoice == i, mouseIntersects)){return true;}
            }
        }
        //
        return false;
    }

    @Override
    /**Renders all of the MenuComponents associated with this Menu.*/
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        for(int i = 0; i < menuComponents.size(); i++)
        {
            menuComponents.get(i).render
            (
                screen, position.x + xOffset, position.y + yOffset,
                cropX0, cropY0, cropX1, cropY1
            );
        }
    }
}
