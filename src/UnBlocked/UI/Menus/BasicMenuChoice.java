package UnBlocked.UI.Menus;
/**
 * 
 */
import org.joml.Vector4f;

import UnBlocked.Graphics.*;
import UnBlocked.UI.Dialogue.DialogueBox;
import UnBlocked.Util.Shapes2D.AAB_Box2D;

public class BasicMenuChoice extends MenuChoice
{
    DialogueBox dialogueBox = null;

    protected Vector4f[] colors = null;
    protected byte colorNum = 0;

    private Sprite[] textSprites;
    private int halfTextWidth, halfTextHeight;
    protected float textScale = 1f;
    private Font font = Fonts.get("Arial");

    /**Constructor.*/
    public BasicMenuChoice(int x, int y, int width, int height, Action action, Vector4f[] colors, int slotStart, String text, float textScale)
    {
        super
        (
            x, y,
            new AAB_Box2D
            (
                (width < 19) ? 19 : width,
                (height < 19) ? 19 : height
            ),
            action
        );

        this.colors = colors;
        this.dialogueBox = new DialogueBox(x, y, width, height, colors[0], slotStart);

        this.textScale = textScale;
        setText(text);
    }

    public BasicMenuChoice(int x, int y, int width, int height, Action action, Vector4f[] colors, String text, float textScale)
    {this(x, y,  width, height, action, colors, 0, text, textScale);}

    public Sprite[] getTextSprites(){return textSprites;}

    public void setText(String text)
    {
        this.textSprites = new Sprite[text.length()];
        this.halfTextWidth = (int)(font.textToSprites(text, textSprites) * textScale)/2;
        this.halfTextHeight = (int)((font.getLineSpace()-1) * textScale)/2;
    }

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        if(selected){return highlight(intersects);}
        else{return noHighlight();}
    }

    public boolean noHighlight()
    {
        if(colorNum != 0)
        {
            dialogueBox.setColor(colors[0]);
            colorNum = 0;
        }
        return false;
    }

    public boolean highlight(boolean intersects)
    {
        if(colorNum != 1)
        {
            dialogueBox.setColor(colors[1]);
            colorNum = 1;
        }

        //Check Input
        if(input_Confirm(intersects))
        {
            //Set color back.
            dialogueBox.setColor(colors[0]);
            colorNum = 0;

            //Perform Action.
            action.perform();

            //Action performed. Don't update the rest of the menu.
            return true;
        }
        return false;
    }

    Vector4f dc = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    @Override
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        dialogueBox.render(screen, xOffset, yOffset);

        int xa = this.position.x + xOffset,
        ya = this.position.y + yOffset;

        //Text
        font.render(screen,
        xa - halfTextWidth, ya - halfTextHeight,
        textSprites, textScale, false);

        //shape.render(screen, 1.0f, xa, ya, false);
    }
}
