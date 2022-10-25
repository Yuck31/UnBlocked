package UnBlocked.Graphics.Animations;
/**
 * Author: Luke Sullivan
 * Last Edit: 7/27/2022
 */
public class FrameAnimation_Timer
{
    //Frame.
    protected int frame = 0;

    //Rate of Change.
    private float time = 0;
    private final float[] rates;

    //Loop Stuff.
    private boolean doesLoop = false;
    public static final byte
    PLAYING = 0,
    HAS_LOOPED = 1,
    HAS_ENDED = 2;
    private byte loopStatus = PLAYING;
    private int loopTo = -1;

    //Has a frame advanced?
    private boolean frameAdvanced = false;

    /**
     * Constructor.
     * 
     * @param loopTo
     * @param rates
     */
    public FrameAnimation_Timer(int loopTo, float[] rates)
    {
        //Set Rates.
        this.rates = rates;

        //Set loopTo frame.
        this.loopTo = loopTo;
        if(loopTo >= 0){doesLoop = true;}
    }

    /**
     * Constructor with one rate and a loopTo frame.
     * 
     * @param loopTo
     * @param frames
     * @param rate
     */
    public FrameAnimation_Timer(int loopTo, int frames, float rate)
    {
        //Set rate.
        this.rates = new float[frames];
        for(int i = 0; i < rates.length; i++){rates[i] = rate;}

        //Set loopTo.
        this.loopTo = loopTo;
        if(loopTo >= 0){doesLoop = true;}
    }

    /**Updates this animation.*/
    public final int update(float timeMod)
    {
        //Set frameAdvanced.
        frameAdvanced = false;

        //Increment time.
        time += timeMod;

        if(time >= rates[frame])
        {
            while(time >= rates[frame])
            {
                //Increment Time accordingly
                time -= rates[frame];

                //Increment frame accordingly
                frame++;
                frameAdvanced = true;

                if(frame >= rates.length)
                {
                    if(doesLoop)
                    {
                        frame = loopTo;
                        loopStatus = HAS_LOOPED;
                    }
                    else
                    {
                        frame = rates.length-1;
                        loopStatus = HAS_ENDED;
                    }
                }
            }
        }
        else if(time < 0)
        {
            while(time < 0)
            {
                //Increment Time accordingly
                time += rates[frame];

                //Increment frame accordingly
                frame--;
                frameAdvanced = true;

                if((loopStatus == HAS_LOOPED && frame < loopTo) || frame < 0)
                {
                    if(doesLoop)
                    {
                        frame = rates.length-1;
                        loopStatus = HAS_LOOPED;
                    }
                    else{frame = rates.length-1;}
                }
            }
        }
        return this.frame;
    }

    //FrameRate Getters.
    public final float getFrameRate(){return rates[this.frame];}
    public final float getFrameRate(int f){return rates[f];}
    public final float[] getRates(){return rates;}

    //Frame Getter/Setter.
    public final int getFrame(){return frame;}
    public final void setFrame(int frame){this.frame = frame; time = 0;}

    //LoopTo Getter.
    public int getLoopTo(){return loopTo;}

    //Loop Status.
    public byte getLoopStatus(){return loopStatus;}
    public boolean hasEnded(){return loopStatus == HAS_ENDED;}

    //Has Frame Advanced.
    public final boolean hasFrameAdvanced(){return frameAdvanced;}
    
    /**Resets this timer.*/
    public final void resetTimer()
    {
        frame = 0;
        time = 0;
        loopStatus = PLAYING;
    }
}
