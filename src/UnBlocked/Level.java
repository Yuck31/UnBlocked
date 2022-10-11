package UnBlocked;
/**
 * 
 */
import java.io.File;

import UnBlocked.Graphics.Screen;
import UnBlocked.Entities.Player;

public class Level
{
    //Player.
    private Player player;

    //Constants.
    public static final int TILE_BITS = 4, TILE_SIZE = 1 << TILE_BITS;

    //Dimensions and Tiles.
    private int width, height;
    private byte[] tiles;

    //Camera.
    private LevelCamera camera = null;

    /**Constructor loading from file.*/
    public Level(File file)
    {
        this.camera = new LevelCamera(this);
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}
    
    public void update()
    {
        //Update tile animations.
    }

    public void render(Screen screen)
    {
        //
        //Determine what tiles the camera can currently see.
        //

        //Get Camera Position
        int cameraX = (int)camera.getX(), cameraY = (int)camera.getY();

        //Set Screen offset relative to camera position.
        screen.setCameraOffsets(camera.getScaledX(), camera.getScaledY());

        //Get Camera Zoom.
        float scale = camera.getScale();

        //Calculate what needs to be rendered horizontally.
        int x0 = cameraX >> Level.TILE_BITS;
        int x1 = (int)((cameraX + (screen.getWidth() / scale)) + Level.TILE_SIZE) >> Level.TILE_BITS;
        if(x0 < 0){x0 = 0;} else if(x0 >= width){x0 = width;}
        if(x1 < 0){x1 = 0;} else if(x1 >= width){x1 = width;}

        //Calculate what needs to be rendered vertically.
        int y0 = cameraY >> Level.TILE_BITS;
        int y1 = (int)((cameraY + (screen.getHeight() / scale)) + Level.TILE_SIZE) >> Level.TILE_BITS;
        if(y0 < 0){y0 = 0;} else if(y0 >= height){y0 = height;}
        if(y1 < 0){y1 = 0;} else if(y1 >= height){y1 = height;}


        //
        //Iterate through all visible tiles and render them.
        //Bottom to Top, Left to Right.
        //
        for(int y = y1; y >= y0; y--)
        {
            for(int x = x0; x < x1; x++)
            {
                //int tileID = getTileID(x, y);
                //TileRenderable t = getRenderable(tileID);
                //t.render(screen, x, y, scale);
            }
        }
    }
}
