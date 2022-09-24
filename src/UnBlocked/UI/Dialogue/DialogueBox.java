package UnBlocked.UI.Dialogue;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/24/2022
 */
import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.UI.UIComponent;

public class DialogueBox extends UIComponent
{
    private int width, height;
    private Vector4f color = null;

    private Sprite[] boxSprites = new Sprite[9];

    /**Constructor.*/
    public DialogueBox(int x, int y, int width, int height, Vector4f color, int slotStart)
    {
        super(x, y);
        this.width = (width < 19) ? 19 : width;
        this.height = (height < 19) ? 19 : height;
        this.color = color;

        for(int i = 0; i < boxSprites.length; i++)
        {boxSprites[i] = Dialogue.dialogueSprites[slotStart + i];}
    }

    /**Constructor. */
    public DialogueBox(int x, int y, int width, int height, Vector4f color)
    {this(x, y, width, height, color, 0);}

    public void setColor(Vector4f color){this.color = color;}

    public boolean expand(int wInc, int hInc, int targetWidth, int targetHeight)
    {
        width += wInc; height += hInc;

        boolean done = true;
        if((wInc > 0 && width > targetWidth) || (wInc < 0 && width < targetWidth))
        {
            width = targetWidth;
            done = false;
        }
        if((wInc > 0 && height > targetHeight) || (wInc < 0 && height < targetHeight))
        {
            height = targetHeight;
            done = false;
        }
        //
        return done;
    }

    /**
     * Renders this dialouge box.
     * 
     * @param screen screen object.
     * @param mainX menu's X position.
     * @param mainY menu's Y position.
     */
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        int xa = this.position.x + xOffset, ya = this.position.y + yOffset;

        int spriteWidth = boxSprites[0].getWidth(),
        spriteHeight = boxSprites[0].getHeight();

        int w = width - (spriteWidth*2),
        h = height - (spriteHeight*2);

        int l = xa - (width/2), mX = l + spriteWidth, r = l + width - spriteWidth;
        int t = ya - (height/2), mY = t + spriteHeight, b = t + height - spriteHeight;

        //Top-Left
        screen.renderSprite(l, t, boxSprites[0], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1,
        false);

        //Stretch Top-Middle
        screen.renderSprite_St(mX, t, boxSprites[1], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1, w, spriteHeight,
        false);

        //Top-Right
        screen.renderSprite(r, t, boxSprites[2], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1,
        false);


        //Stretch Middle-Left
        screen.renderSprite_St(l, mY, boxSprites[3], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1, spriteWidth, h,
        false);

        //Scale Middle-Middle
        screen.renderSprite_St(mX, mY, boxSprites[4], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1, w, h,
        false);

        //Stretch Middle-Right
        screen.renderSprite_St(r, mY, boxSprites[5], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1, spriteWidth, h,
        false);


        //Bottom-Left
        screen.renderSprite(l, b, boxSprites[6], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1,
        false);

        //Stretch Bottom-Middle
        screen.renderSprite_St(mX, b, boxSprites[7], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1, w, spriteHeight,
        false);

        //Bottom-Right
        screen.renderSprite(r, b, boxSprites[8], Sprite.FLIP_NONE, color,
        cropX0, cropY0, cropX1, cropY1,
        false);
    }

    public void render(Screen screen, int mainX, int mainY){render(screen, mainX, mainY, 0, 0, screen.getWidth(), screen.getHeight());}
    public void render(Screen screen){render(screen, 0, 0, 0, 0, screen.getWidth(), screen.getHeight());}
}
