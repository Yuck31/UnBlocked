package UnBlocked.UI.Menus;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/21/2022
 */
import static org.lwjgl.glfw.GLFW.*;

import UnBlocked.Mouse;
import UnBlocked.Graphics.Screen;
import UnBlocked.UI.UIComponent;
import UnBlocked.Util.Shapes2D.Shape2D;
import UnBlocked.Game;
import UnBlocked.Controller;

public abstract class MenuComponent extends UIComponent
{
    //Shape2D.
    protected Shape2D shape;

    /**Constructor.*/
    public MenuComponent(int x, int y, Shape2D shape)
    {
        super(x, y);
        this.shape = shape;
    }

    /**Checks if the given Mouse intersects this MenuChoice.*/
    public final boolean intersects(int xOffset, int yOffset, Mouse mouse, boolean needsToMove)
    {
        int xa = this.position.x + xOffset,
        ya = this.position.y + yOffset;
        //
        return
        (
            (mouse.isMoving() || !needsToMove)
            && shape.intersects(mouse.getX(), mouse.getY(), xa, ya)
        );
    }

    public final boolean intersects(int xOffset, int yOffset, Mouse mouse){return intersects(xOffset, yOffset, mouse, false);}
    public final boolean intersects(Mouse mouse, boolean needsToMove){return intersects(0, 0, mouse, needsToMove);}
    public final boolean intersects(Mouse mouse){return intersects(0, 0, mouse, false);}

    /**Checks if either a confirm button or left-click is pressed.*/
    public boolean input_Confirm(boolean intersects)
    {
        return Game.controller.menu_InputPressed_AnyPlayer(Controller.menu_CONFIRM)
        | (intersects && Game.mouse.buttonPressed(GLFW_MOUSE_BUTTON_LEFT));
    }

    /**Checks if either a confirm button or left-click is pressed.*/
    public boolean input_Confirm_Held(boolean intersects)
    {
        return Game.controller.menu_InputHeld_AnyPlayer(Controller.menu_CONFIRM)
        | (intersects && Game.mouse.buttonHeld(GLFW_MOUSE_BUTTON_LEFT));
    }

    /**Returns true if it was selected and confirmed.*/
    public abstract boolean update(boolean selected, boolean intersects);

    /**
     * Renders this MenuComponent.
     * 
     * @param screen the screen object to render to.
     * @param xOffset a given X offset.
     * @param yOffset a given Y offset.
     * @param cropX0 left crop.
     * @param cropY0 up crop.
     * @param cropX1 right crop.
     * @param cropY1 down crop.
     */
    public abstract void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1);
    public final void render(Screen screen, int xOffset, int yOffset){render(screen, xOffset, yOffset, 0, 0, screen.getWidth(), screen.getHeight());}
}
