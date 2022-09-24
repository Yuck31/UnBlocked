package UnBlocked.UI.Menus;
/**
 * 
 */
import org.lwjgl.glfw.GLFW;

import org.joml.Vector4f;

import UnBlocked.Game;
import UnBlocked.Mouse;
import UnBlocked.Graphics.Screen;
import UnBlocked.UI.Menus.MenuChoice.Action;
import UnBlocked.Util.Shapes2D.AAB_Box2D;

public class ListMenu extends Menu
{
    public int offset;
    //
    private int maxElements;
    public int elementWidth, elementHeight;

    /**Constructor.*/
    public ListMenu(int x, int y, int elementWidth, int elementHeight, int maxElements)
    {
        super(x, y, new AAB_Box2D(elementWidth, elementHeight));
        //
        this.elementWidth = elementWidth;
        this.elementHeight = elementHeight;
        //
        this.maxElements = maxElements;
    }

    public ListMenu addOption(String text, Action action, Vector4f[] colors)
    {
        addComponent
        (
            new BasicMenuChoice
            (
                0, (elementHeight * (numComponents()+1)),
                elementWidth, elementHeight, action, colors,
                59, text, 1.0f
            )
        );
        //
        return this;
    }

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        Mouse mouse = Game.mouse;
        byte scroll = mouse.getScroll();
        //
        if(scroll < 0){offset = (offset+1) % menuComponents.size();}
        else if(scroll > 0){offset = ((offset-1) + (menuComponents.size())) % menuComponents.size();}
        //
        boolean button = mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
        int limit = (offset + maxElements >= menuComponents.size()) ? menuComponents.size() : offset + maxElements;
        //
        for(int i = offset; i < limit; i++)
        {
            //Cache it.
            MenuComponent m = menuComponents.get(i);
            boolean mouseIntersects = false;

            //Mouse Check.
            if(m.intersects(this.position.x, this.position.y - (offset * elementHeight), Game.mouse, true))
            {
                //currentChoice = i;
                mouseIntersects = true;
            }

            //Update Menu Choice.
            if(m.update(mouseIntersects, mouseIntersects)){return true;}
        }
        //
        if(button){mouse.pressButton(GLFW.GLFW_MOUSE_BUTTON_LEFT);}
        //
        return false;
    }

    @Override
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        int limit = (offset + maxElements >= menuComponents.size()) ? menuComponents.size() : offset + maxElements;
        //
        for(int i = offset; i < limit; i++)
        {
            menuComponents.get(i).render(screen, 0, offset * -elementHeight,
            0, elementHeight, elementWidth, elementHeight + (menuComponents.size() * elementHeight));
        }
    }
}
