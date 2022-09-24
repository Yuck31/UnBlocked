package UnBlocked.UI.Dialogue;
/**
 * 
 */
import org.joml.Vector4f;

import UnBlocked.Game;
//import UnBlocked.Graphics.Font;
//import UnBlocked.Graphics.Fonts;
import UnBlocked.Graphics.Screen;
import UnBlocked.UI.Menus.BasicMenuChoice;
import UnBlocked.UI.Menus.ListMenu;

public class DropDown_Menu extends BasicMenuChoice
{
    private ListMenu subMenu;
    //
    private boolean droppedDown = false;
    private byte putUpTime = 30;
    //
    //private Font arial = Fonts.get("Arial");

    /**Constructor.*/
    public DropDown_Menu(int x, int y, int width, int height, Vector4f[] colors, String text, float textScale)
    {
        super(x, y, width, height, null, colors, 59, text, textScale);
        //
        this.action = () ->
        {
            if(!droppedDown){droppedDown = true;}
            else
            {
                subMenu.offset = 0;
                droppedDown = false;
            }
        };
        //
        this.subMenu = new ListMenu(x, y, width, height, 3);
    }

    public DropDown_Menu addOption(String text, Action action)
    {
        subMenu.addOption(text, action, colors);
        //
        return this;
    }

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        if(!droppedDown)
        {
            return super.update(selected, intersects);
        }
        else
        {
            super.update(selected, intersects);
            subMenu.update(selected, intersects);
            //
            if(!selected && !subMenu.intersects(Game.mouse, false))
            {
                putUpTime--;
                if(putUpTime <= 0)
                {
                    droppedDown = false;
                    //
                    subMenu.offset = 0;
                    putUpTime = 30;
                }
            }
            else
            {
                putUpTime = 30;
                return true;
            }
        }
        //
        return false;
    }

    @Override
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        //Base choice.
        super.render(screen, xOffset, yOffset, cropX0, cropY0, cropX1, cropY1);

        //Sub-Menu.
        if(droppedDown)
        {
            subMenu.render(screen, position.x + xOffset, position.y + yOffset,
            cropX0, cropY0, cropX1, cropY1);
        }
    }
}
