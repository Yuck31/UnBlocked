package UnBlocked.UI.Menus;
/**
 * Author: Luke Sullivan
 * Last Edit: 3/10/2022
 */
import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteRenderer.ScaleSpriteRenderer;
import UnBlocked.Util.Shapes2D.Shape2D;

public class ScaleMenuChoice extends MenuChoice
{
    //Sprite Renderer, manages scaling and positioning.
    private ScaleSpriteRenderer spriteRenderer = null;
    private float minScale = 1.0f, maxScale = 1.05f,
    scale = minScale, scaleInc = 0.01f;

    //This should be a size of 2.
    private Sprite[] sprites = null;

    /**Constructor.*/
    public ScaleMenuChoice(int x, int y, Shape2D shape, Action action, Sprite[] sprites)
    {
        super(x, y, shape, action);
        this.sprites = sprites;

        spriteRenderer  = new ScaleSpriteRenderer(sprites[0], 0, 0, 0, false, false);
    }

    @Override
    public boolean update(boolean selected, boolean confirmPressed)
    {
        if(selected)
        {
            
        }
        else{noHighlight();}

        //No action performed. Keep going.
        return false;
    }

    @Override
    public boolean noHighlight()
    {
        if(this.selected)
        {
            this.selected = false;
            spriteRenderer.setSprite(sprites[0]);
        }
        if(scale > minScale)
        {
            scale -= scaleInc;
            if(scale < minScale){scale = minScale;}
        }
        return false;
    }

    @Override
    public boolean highlight(boolean intersects)
    {
        if(!this.selected)
        {
            this.selected = true;
            spriteRenderer.setSprite(sprites[1]);
        }
        if(scale < maxScale)
        {
            scale += scaleInc;
            if(scale > maxScale){scale = maxScale;}
        }

        //Check Input
        if(input_Confirm(intersects))
        {
            //Perform Action.
            action.perform();

            //Action performed. Don't update the rest of the menu.
            return true;
        }
        return false;
    }

    @Override
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        int xa = this.position.x + xOffset,
        ya = this.position.y + yOffset;
        //
        spriteRenderer.render(screen, xa, ya);
    }
}
