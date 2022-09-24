package UnBlocked.Graphics.Animations;
/**
 * A simple frame by frame animation.
 * 
 * Author: Luke Sullivan
 * Last Edit: 3/21/2022
 */
import UnBlocked.Graphics.Sprite;

public class FrameAnimation extends Animation
{
    //Timer.
    protected FrameAnimation_Timer timer;

    //Array of Sprites.
    protected transient Sprite[] sprites;

    /**
     * Constructor.
     * 
     * @param loopTo
     * @param sprites
     * @param rates
     */
    public FrameAnimation(int loopTo, Sprite[] sprites, float[] rates)
    {
        //Set Sprites.
        this.sprites = sprites;

        //Create Timer.
        timer = new FrameAnimation_Timer(loopTo, rates);
    }

    /**
     * Constructor with one rate and a loopTo frame.
     * 
     * @param loopTo
     * @param sprites
     * @param rate
     */
    public FrameAnimation(int loopTo, Sprite[] sprites, float rate)
    {
        //Set Sprites.
        this.sprites = sprites;

        //Create Timer
        timer = new FrameAnimation_Timer(loopTo, sprites.length, rate);
    }

    /**Updates this animation.*/
    public final Sprite update(float timeMod){return sprites[timer.update(timeMod)];}

    public final Sprite getSprite(){return sprites[timer.getFrame()];}
    public final Sprite getSprite(int index){return sprites[index];}

    /**Returns all of the Sprites in this Animation.*/
    public final Sprite[] getSprites(){return sprites;}

    //FrameRate Getters.
    public final float getFrameRate(){return timer.getFrameRate();}
    public final float getFrameRate(int f){return timer.getFrameRate(f);}

    //Frame Getter/Setter.
    public final int getFrame(){return timer.getFrame();}
    public final void setFrame(int frame){timer.setFrame(frame);}

    /**Resets this FrameAnimation from its first frame.*/
    public final void resetAnim(){timer.resetTimer();}
}
