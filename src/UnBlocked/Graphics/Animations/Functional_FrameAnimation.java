package UnBlocked.Graphics.Animations;
/**
 * This is a Frame Animation that can have specific functionality applied to each frame.
 * This allows for stuff like frame-by-frame collision, frame-by-frame instantiation, etc.
 * 
 * Author: Luke Sullivan
 * Last Edit: 8/21/2022
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import UnBlocked.Game;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteRenderer.SpriteRenderer;

public class Functional_FrameAnimation extends Animation
{
    @FunctionalInterface
    public interface Action{public abstract void perform(float timeMod, byte loopStatus);}
    public static final void doNothing(float timeMod){return;}

    //Timer.
    protected FrameAnimation_Timer timer;

    //Array of Sprites.
    protected transient Sprite[] sprites;

    //Pivots.
    protected short[][] offsets;

    //Actions.
    protected Action[] actions;
    protected short[] actionNumbers;

    /**
     * Constructor.
     * 
     * @param loopTo the frame to loop to when the animation ends.
     * @param sprites the array of sprites each frame uses.
     * @param rates the array of rates between each frame.
     * @param offsets the offsets for each frame.
     * @param actions the array of actions to work with.
     * @param actionNumbers the numbers indicating which action to perform for each frame.
     */
    public Functional_FrameAnimation(int loopTo, Sprite[] sprites, float[] rates, short[][] offsets, Action[] actions, short[] actionNumbers)
    {
        //Set Sprites.
        this.sprites = sprites;

        //Create Timer.
        timer = new FrameAnimation_Timer(loopTo, rates);

        //Set everything else.
        this.offsets = offsets;
        this.actions = actions;
        this.actionNumbers = actionNumbers;
    }

    public static final byte BYTES_PER_FRAME = 12;

    /**
     * Loads a Functional_FrameAnimation with an Animation .dat file, Sprite array, and Action array.
     * Loop Point is first 4 bytes.
     * For one frame: Sprite ID - T I M E - X Pos - Y Pos - Action ID
     * 
     * @param animName the name of the animation file to load data from (relative to Animation.animationsPath).
     * @param spritePool the array of sprites (obtained from a sprite layout) to get sprites from.
     * @param actions the array of actions for the function numbers to use.
     * 
     * @return a Functional_FrameAnimation loaded from the given data.
     */
    public static Functional_FrameAnimation load(String animName, Sprite[] spritePool, Action... actions)
    {
        //Get animation .dat file.
        File file = new File(Animation.animationsPath + animName + "_ANM.dat");

        //Calculate how many frames are in it (first 4 bytes are the loopTo, so exclude them).
        int length = (int)((file.length()-4)/BYTES_PER_FRAME);

        int loopTo = -1;
        Sprite[] resultSprites = new Sprite[length];
        float[] rates = new float[length];
        short[][] offsets = new short[length][2];
        short[] actionNumbers = new short[length];

        try
        {
            //Set up the input stream.
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            //Make bytes array.
            byte[] bytes = new byte[4];

            //Get Loop Point value.
            bis.read(bytes);
            loopTo = Game.bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
            
            bytes = new byte[BYTES_PER_FRAME];
            for(int i = 0; i < length; i++)
            {
                //Put the next 10 bytes into the bytes array.
                bis.read(bytes);

                //Sprite.
                short sID = Game.bytesToShort(bytes[0], bytes[1]);
                resultSprites[i] = spritePool[sID];

                //Rate.
                rates[i] = Game.bytesToFloat(bytes[2], bytes[3], bytes[4], bytes[5]);

                //Offsets.
                offsets[i][0] = Game.bytesToShort(bytes[6], bytes[7]);
                offsets[i][1] = Game.bytesToShort(bytes[8], bytes[9]);

                //Action Number.
                actionNumbers[i] = Game.bytesToShort(bytes[10], bytes[11]);
            }

            //Close the input streams.
            bis.close();
            fis.close();
        }
        catch(FileNotFoundException e){e.printStackTrace();}
        catch(IOException e){e.printStackTrace();}

        return new Functional_FrameAnimation(loopTo, resultSprites, rates, offsets, actions, actionNumbers);
    }
    

    /**Updates this animation.*/
    public final Sprite update(float timeMod){return sprites[timer.update(timeMod)];}


    /**
     * Advances this animation and sets the result to the given SpriteRenderer.
     * 
     * @param timeMod the modififer of time to use.
     * @param spriteRenderer the SpriteRenderer to set this animation's current sprite and offset to.
     */
    public final int update(float timeMod, SpriteRenderer spriteRenderer)
    {
        //Update the timer.
        int frame = timer.update(timeMod);

        //Set Sprite.
        spriteRenderer.setSprite(sprites[frame]);

        //Set Offsets.
        spriteRenderer.setOffset(offsets[frame][0], offsets[frame][1]);

        //Perform Action.
        actions[actionNumbers[frame]].perform(timeMod, timer.getLoopStatus());

        //Return frame.
        return frame;
    }


    //Pivot Getters.
    public final short getOffsetX(){return offsets[timer.getFrame()][0];}
    public final short getOffsetY(){return offsets[timer.getFrame()][1];}

    public final Sprite getSprite(){return sprites[timer.getFrame()];}
    public final Sprite getSprite(int index){return sprites[index];}

    //Frame Getter/Setter.
    public final int getFrame(){return timer.getFrame();}
    public final void setFrame(int frame){timer.setFrame(frame);}

    /**Resets this FrameAnimation from its first frame.*/
    public final void resetAnim(){timer.resetTimer();}

    /**Returns all of the Sprites in this Animation.*/
    public final Sprite[] getSprites(){return sprites;}
}
