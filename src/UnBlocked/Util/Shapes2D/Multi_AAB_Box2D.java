package UnBlocked.Util.Shapes2D;
/**
 * 
 */

import UnBlocked.Graphics.Screen;

public class Multi_AAB_Box2D extends Shape2D
{
    private AAB_Box2D[] boxes;

    public Multi_AAB_Box2D(AAB_Box2D... boxes)
    {
        this.boxes = boxes;
    }

    @Override
    public int getWidth()
    {
        int minX = boxes[0].left(), maxX = boxes[0].right();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            //
            if(box.left() < minX){minX = box.left();}
            if(box.right() > maxX){minX = box.right();}
        }

        return maxX - minX;
    }

    @Override
    public int getHeight()
    {
        int minY = boxes[0].back(), maxY = boxes[0].front();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            //
            if(box.back() < minY){minY = box.back();}
            if(box.front() > maxY){minY = box.front();}
        }

        return maxY - minY;
    }

    public int left()
    {
        int result = boxes[0].left();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            if(box.left() < result){result = box.left();}
        }

        return result;
    }

    public int right()
    {
        int result = boxes[0].right();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            if(box.right() > result){result = box.right();}
        }

        return result;
    }

    public int back()
    {
        int result = boxes[0].back();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            if(box.back() < result){result = box.back();}
        }

        return result;
    }

    public int front()
    {
        int result = boxes[0].front();

        for(int i = 1; i < boxes.length; i++)
        {
            AAB_Box2D box = boxes[i];
            if(box.front() > result){result = box.front();}
        }

        return result;
    }

    @Override
    public boolean intersects(double x, double y, float thisX, float thisY)
    {
        //Check for collision with each box.
        for(int i = 0; i < boxes.length; i++)
        {
            if(boxes[i].intersects(x, y, thisX, thisY))
            {return true;}
        }

        //No collision was made.
        return false;
    }

    @Override
    public void render(Screen screen, float scale, float x, float y, boolean fixed)
    {
        //Render each box.
        for(int i = 0; i < boxes.length; i++)
        {boxes[i].render(screen, scale, x, y, fixed);}
    }

}
