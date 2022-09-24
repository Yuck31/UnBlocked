package UnBlocked.UI.Menus;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/22/2022
 */
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import UnBlocked.Controller;
import UnBlocked.Game;
import UnBlocked.Graphics.Font;
import UnBlocked.Graphics.Screen;
import UnBlocked.Util.Shapes2D.AAB_Box2D;

public class TextBox extends MenuComponent
{
    private Controller keyboard;

    private Vector4f rectColor;
    private Vector4f fontColor;
    private int width, height;
    //
    private Font font;
    private float textScale = 1.0f;
    private String text = new String(""),
    previousText = new String(text);
    //
    private byte typingMode;
    private byte cursorTime = 0;

    /**Constructor.*/
    public TextBox(int x, int y, int width, int height, Font font, byte typingMode, float textScale, Vector4f rectColor, Vector4f fontColor)
    {
        super
        (
            x, y,
            new AAB_Box2D
            (
                0, 0,
                width, height
            )
        );
        keyboard = Game.controller;
        //
        this.width = width;
        this.height = height;
        this.font = font;
        this.textScale = textScale;
        this.typingMode = typingMode;
        //
        this.rectColor = rectColor;
        this.fontColor = fontColor;
    }

    @Override
    public boolean update(boolean selected, boolean intersects)
    {
        if(selected)
        {
            if(!keyboard.isTyping() && keyboard.getStringObject() != text
            && intersects && Game.mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
            {
                previousText = String.valueOf(text);
                //
                keyboard.beginTyping(text, typingMode);
                return true;
            }
        }
        else if(!intersects && Game.mouse.buttonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT))
        {
            //text =
            keyboard.endTyping();
        }
        //
        if(keyboard.getStringObject() == text)
        {
            //text = keyboard.getStringObject();
            cursorTime = (byte)((cursorTime+1) % 60);
            //
            //if(!keyboard.isTyping()){typing = false;}
        }
        //
        return false;
    }

    public TextBox setText(String text)
    {
        this.text = text;
        this.previousText = String.valueOf(text);
        //
        return this;
    }

    public byte getText_Byte()
    {
        try
        {
            byte result = Byte.parseByte(text);
            previousText = String.valueOf(text);
            return result;
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            text = String.valueOf(previousText);
        }
        //
        previousText = "0";
        text = "0";
        return 0;
    }

    public short getText_Short()
    {
        try
        {
            short result = Short.parseShort(text);
            previousText = String.valueOf(text);
            return result;
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            text = String.valueOf(previousText);
        }
        //
        previousText = "0";
        text = "0";
        return 0;
    }

    public int getText_Int()
    {
        try
        {
            int result = Integer.parseInt(text);
            previousText = String.valueOf(text);
            return result;
        }
        catch(NumberFormatException e)
        {
            //e.printStackTrace();
            text = String.valueOf(previousText);
        }
        //
        previousText = "0";
        text = "0";
        return 0;
    }

    public char getText_Char()
    {
        char result = text.charAt(0);
        previousText = String.valueOf(text);
        return result;
    }

    public String getText_String()
    {
        previousText = String.valueOf(text);
        return text;
    }

    public float getText_Float()
    {
        try
        {
            float result = Float.parseFloat(text);
            previousText = String.valueOf(text);
            return result;
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            text = String.valueOf(previousText);
        }
        //
        previousText = "1.0";
        text = "1.0";
        return 1.0f;
    }

    public double getText_Double()
    {
        try
        {
            double result = Double.parseDouble(text);
            previousText = String.valueOf(text);
            return result;
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
            text = String.valueOf(previousText);
        }
        //
        previousText = "1.0";
        text = "1.0";
        return 1.0;
    }

    @Override
    public void render(Screen screen, int xOffset, int yOffset, int cropX0, int cropY0, int cropX1, int cropY1)
    {
        int xa = position.x + xOffset,
        ya = position.y + yOffset;
        //
        //Rect
        screen.fillRect(xa, ya, width, height,
        rectColor, cropX0, cropY0, cropX1, cropY1, false);

        //Text
        font.render(screen, xa+1, ya+1,
        text, textScale, fontColor, false);

        //Cursor
        if(keyboard.getStringObject() == text && cursorTime < 30)
        {
            font.renderCursor(screen, xa+1, ya+1, text, keyboard.getCursorIndex(),
            textScale, fontColor, false);
        }
    }
}
