package UnBlocked.UI.Menus;
/**
 * A choice for Menus.
 * 
 * Author: Luke Sullivan
 * Last Edit: 7/22/2022
 */

import UnBlocked.Util.Shapes2D.Shape2D;

public abstract class MenuChoice extends MenuComponent
{
    @FunctionalInterface//Functional Interface for what should happen when this choice is selected
    public interface Action{public abstract void perform();}
    public static final void doNothing(){return;}

    //Action Reference
    protected Action action = MenuChoice::doNothing;

    //Selected boolean
    protected boolean selected = false;

    /**Constructor.*/
    public MenuChoice(int x, int y, Shape2D shape, Action action)
    {
        super(x, y, shape);
        this.action = action;
    }

    public void setAction(Action action){this.action = action;}

    public abstract boolean noHighlight();
    public abstract boolean highlight(boolean intersects);
}
