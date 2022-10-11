package UnBlocked.Util.Shapes2D;
/**
 * 
 */

import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;

public class AAB_Box2D extends Shape2D
{
    //Dimensions.
    protected int width, height;

    /**Constructor.*/
    public AAB_Box2D(int width, int height, int xOffset, int yOffset)
    {
        super(xOffset, yOffset);
        this.width = width;
        this.height = height;
    }

    /**Construtor. Offsets are Dimemsions / 2 by default.*/
    public AAB_Box2D(int width, int height)
    {this(width, height, -(width/2), -(height/2));}

    public int getWidth(){return width;}
    public int getHeight(){return height;}

    public void setDimensions(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int left(){return xOffset;}
    public int right(){return xOffset + width;}
    public int back(){return yOffset + height;}
    public int front(){return yOffset + height;}

    @Override
    public boolean intersects(double x, double y, float thisX, float thisY)
    {
        float//Set Left, Back, and Bottom points
        this_x = thisX + this.xOffset,
        this_y = thisY + this.yOffset;

        float//Dimensions
        this_width = this_x + this.width,
        this_height = this_y + this.height;

        return(x < this_width && y < this_height
            && x > this_x && y > this_y);
    }

    Vector4f g = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
    @Override
    public void render(Screen screen, float scale, float x, float y, boolean fixed)
    {
        int xPos = (int)((x + xOffset) * scale);
        int yPos = (int)((y + yOffset) * scale);

        screen.drawRect(xPos, yPos, (int)(width * scale), (int)(height * scale), g, fixed);
    }
}
