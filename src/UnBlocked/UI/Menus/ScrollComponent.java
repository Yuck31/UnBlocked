package UnBlocked.UI.Menus;

import UnBlocked.Util.Shapes2D.AAB_Box2D;

/**
 * Author: Luke Sullivan
 * Last Edit: 3/14/2022
 */
//import static org.lwjgl.glfw.GLFW.*;

//import UnBlocked.Game;
//import UnBlocked.Graphics.Screen;

public abstract class ScrollComponent extends MenuComponent
{
    MenuChoice.Action upAction = MenuChoice::doNothing, downAction = MenuChoice::doNothing;
    //
    /**
     * Constructor.
     * 
     * @param menu
     * @param x
     * @param y
     * @param width
     * @param height
     * @param upAction
     * @param downAction
     */
    public ScrollComponent(int x, int y, int width, int height,
    MenuChoice.Action upAction, MenuChoice.Action downAction)
    {
        super(x, y, new AAB_Box2D(width, height));
    }
}
