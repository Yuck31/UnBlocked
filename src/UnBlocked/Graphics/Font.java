package UnBlocked.Graphics;
/**
 * A set of Sprites representing Characters in a font.
 * 
 * Author: Luke Sullivan
 * Last Edit: 6/24/2022
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Vector4f;

import UnBlocked.Game;

public class Font
{
    //SpriteSheet.
    private SpriteSheet sheet;

    //Sprites.
    private Sprite[] sprites;

    //Space between lines.  
    private short charSpace = 2, wordSpace = 4;
    private int lineSpace = 0;//, averageCharHeight;

    //Binds a char to a slot in the Sprites array.
    private HashMap<Character, Integer> charToSlot = new HashMap<Character, Integer>();

    /**Constructor.*/
    public Font(SpriteSheet sheet, String name)
    {
        //Set sheet.
        this.sheet = sheet;

        //Get char-binding .dat file.
        File file = new File(Fonts.fontsPath + name + ".dat");

        //Resulting char length (file.length / 2 because 1 char is 2 bytes).
        //int length = (int)file.length() >> 1;
        int length = (int)(file.length()-4) >> 1;

        try
        {
            //Set up the input stream.
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            
            byte[] bytes = new byte[2];
            for(int i = 0; i < length; i++)
            {
                //Put the next 2 bytes into the bytes array.
                bis.read(bytes);

                //Create a new char out of the bytes.
                char character = Game.bytesToChar(bytes[0], bytes[1]);

                //Bind the character to the HashMap.
                charToSlot.put(character, i);
            }

            //Set charSpace.
            bis.read(bytes);
            charSpace = Game.bytesToShort(bytes[0], bytes[1]);

            //Set wordSpace.
            bis.read(bytes);
            wordSpace = Game.bytesToShort(bytes[0], bytes[1]);

            //Close the input streams.
            bis.close();
            fis.close();
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}

        //System.out.println("Break.");


        //Set length of Sprites array.
        sprites = new Sprite[length];

        //Use black pixels to split the sheet into multiple Sprites.
        int currentX = 0;
        for(int i = 0; i < sprites.length; i++)
        {
            int width = 0, height = 0;

            for(int x = currentX; x < sheet.getWidth(); x++)
            {
                if(sheet.getPixel(x, 0) == 0xFF000000)
                {width = x - currentX; break;}
            }
            boolean bottom = true;
            for(int y = 0; y < sheet.getHeight(); y++)
            {
                if(sheet.getPixel(currentX, y) == 0xFF000000)
                {
                    height = y;
                    bottom = false;
                    break;
                }
            }
            if(bottom){height = sheet.getHeight();}

            sprites[i] = new Sprite(sheet, currentX, 0, width, height);
            currentX += (width + 1);

            //Adjust lineSpace.
            if(height > lineSpace){lineSpace = height;}

            //Add statistic to averageCharHeight.
            //averageCharHeight += height;
        }

        //Add one to lineSpace.
        lineSpace++;

        //Divide averageCharHeight.
        //averageCharHeight /= sprites.length;
    }

    public SpriteSheet getSheet(){return sheet;}

    public short getCharSpace(){return charSpace;}
    public short getWordSpace(){return wordSpace;}

    public int getLineSpace(){return lineSpace;}

    /**Converts a String to a Sprite array using this font.*/
    public Sprite[] textToSprites(String text)
    {
        Sprite[] result = new Sprite[text.length()];

        for(int i = 0; i < result.length; i++)
        {
            char c = text.charAt(i);

            if(c == ' '){result[i] = Sprites.nullSprite;}
            else{result[i] = sprites[charToSlot.get(c)];}
        }

        return result;
    }

    /**
     * Converts a String to the given Sprite array using this font.
     * 
     * @param text the string of text to convert.
     * @param spriteArray the array to put the sprites in.
     * @return the width of the output using charSpace and wordSpace values.
     */
    public int textToSprites(String text, Sprite[] spriteArray)
    {
        int textWidth = 0;

        for(int i = 0; i < spriteArray.length; i++)
        {
            char c = text.charAt(i);

            if(c == ' ')
            {
                spriteArray[i] = Sprites.nullSprite;
                textWidth += wordSpace;
            }
            else
            {
                spriteArray[i] = sprites[charToSlot.get(c)];
                textWidth += spriteArray[i].getWidth() + charSpace;
            }
        }

        return textWidth;
    }

    private int f0 = 0, f1 = 0;
    /**Renders a String of Text to the Screen in this font.*/
    public void render(Screen screen, int x, int y, String text, float scale, Vector4f blendingColor, boolean fixed)
    {
        int xOffset = 0, yOffset = 0;
        //
        for(int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);

            switch(c)
            {
                case ' ':
                xOffset += (wordSpace * scale);
                break;

                case '`':
                xOffset = 0;
                yOffset += (lineSpace * scale);
                break;

                default:
                Integer slot = charToSlot.get(c);
                if(slot == null)
                {
                    Sprite s0 = sprites[0];
                    //
                    screen.drawRect(x + xOffset, y + yOffset, (int)(s0.getWidth() * scale), (int)(s0.getHeight() * scale), blendingColor, fixed);
                    xOffset += ((s0.getWidth() + charSpace) * scale);
                }
                else
                {
                    Sprite s = sprites[slot];
                    //
                    screen.renderSprite_Sc(x + xOffset, y + yOffset, s, Sprite.FLIP_NONE, f1, f1, blendingColor, scale, scale, fixed);
                    xOffset += ((s.getWidth() + charSpace) * scale);

                    //screen.renderSprite(x + xOffset, y + yOffset, s, Sprite.Flip.NONE, f1, f1, blendingColor, fixed);
                    //xOffset += ((s.getWidth() + charSpace) * scale);
                }
                
                break;
            }
        }

        //f0 = (f0+1) % 5;
        //f1 = (f1 + ((f0 == 0) ? 1 : 0)) % 120;
    }

    public void renderCursor(Screen screen, int x, int y, String text, int cursorIndex, float scale, Vector4f blendingColor, boolean fixed)
    {
        if(cursorIndex > text.length()){cursorIndex = text.length();}
        int xOffset = 0;
        //
        for(int i = 0; i < cursorIndex; i++)
        {
            char c = text.charAt(i);

            switch(c)
            {
                case ' ':
                xOffset += (wordSpace * scale);
                break;

                //case '`':
                //xOffset = 0;
                //break;

                default:
                Integer slot = charToSlot.get(c);
                if(slot == null)
                {
                    Sprite s0 = sprites[0];
                    xOffset += ((s0.getWidth() + charSpace) * scale);
                }
                else
                {
                    Sprite s = sprites[slot];
                    xOffset += ((s.getWidth() + charSpace) * scale);
                }
                break;
            }
        }
        //
        screen.drawLine(x + xOffset-1, y, x + xOffset, (int)(y + (lineSpace * scale)), blendingColor, fixed);
    }

    /**Renders a String of Text to the Screen in this font, dafault scale is 1f.*/
    public void render(Screen screen, int x, int y, String text, Vector4f blendingColor, boolean fixed)
    {render(screen, x, y, text, 1f, blendingColor, fixed);}

    /**Renders a String of Text to the Screen in this font, default blending color is white.*/
    public void render(Screen screen, int x, int y, String text, float scale, boolean fixed)
    {render(screen, x, y, text, scale, Screen.DEFAULT_BLEND, fixed);}

    /**Renders a String of Text to the Screen in this font using default scale and blending color.*/
    public void render(Screen screen, int x, int y, String text, boolean fixed)
    {render(screen, x, y, text, 1f, Screen.DEFAULT_BLEND, fixed);}


    /**Renders an Array of Sprites using this font's charSpace and wordSpace value.*/
    public void render(Screen screen, int x, int y, Sprite[] textSprites, float scale, Vector4f blendingColor, boolean fixed)
    {
        int xOffset = 0, yOffset = 0;
        for(int i = 0; i < textSprites.length; i++)
        {
            Sprite s = textSprites[i];

            if(s == Sprites.nullSprite){xOffset += (wordSpace * scale);}
            else
            {
                screen.renderSprite_Sc(x + xOffset, y + yOffset, s, Sprite.FLIP_NONE, blendingColor, scale, scale, fixed);
                xOffset += ((s.getWidth() + charSpace) * scale);
            }
        }
    }

    /**Renders an Array of Sprites using this font's charSpace and wordSpace value, default blending color is white.*/
    public void render(Screen screen, int x, int y, Sprite[] textSprites, float scale, boolean fixed)
    {render(screen, x, y, textSprites, scale, Screen.DEFAULT_BLEND, fixed);}

    /**Renders an Array of Sprites using this font's charSpace and wordSpace value, default scale is 1f.*/
    public void render(Screen screen, int x, int y, Sprite[] textSprites, Vector4f blendingColor, boolean fixed)
    {render(screen, x, y, textSprites, 1f, blendingColor, fixed);}

    /**Renders an Array of Sprites using this font's charSpace and wordSpace value ussing default scale and blending color.*/
    public void render(Screen screen, int x, int y, Sprite[] textSprites, boolean fixed)
    {render(screen, x, y, textSprites, 1f, Screen.DEFAULT_BLEND, fixed);}
}
