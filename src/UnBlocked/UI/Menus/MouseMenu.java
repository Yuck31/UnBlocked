package UnBlocked.UI.Menus;
/**
 * This is a Menu that only recieves input from the mouse.
 * 
 * Author: Luke Sullivan
 * Last Edit: 7/24/2022
 */

import UnBlocked.Game;
import UnBlocked.Mouse;
import UnBlocked.Graphics.Screen;
import UnBlocked.Util.Shapes2D.Shape2D;

public class MouseMenu extends Menu
{
    /**Constructor.*/
    public MouseMenu(int x, int y, Shape2D shape)
    {
        super(x, y, shape);
    }

    /**Default Constructor.*/
    public MouseMenu(Shape2D shape){this(0, 0, shape);}

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        Mouse mouse = Game.mouse;
        //
        for(int i = 0; i < menuComponents.size(); i++)
        {
            //Cache it.
            MenuComponent m = menuComponents.get(i);
            boolean mouseIntersects = false;

            if(m.intersects(0, 0, mouse, false))
            {mouseIntersects = true;}

            //Update Menu Choice.
            if(m.update(mouseIntersects, mouseIntersects)){return true;}
        }
        //
        return false;
    }

    @Override
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
