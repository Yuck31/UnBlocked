package UnBlocked;
/**
 * Author: Luke Sullivan
 * Last Edit: 10/19/2022
 */
import java.io.File;

import org.joml.Vector4f;

import UnBlocked.Graphics.Screen;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprites;
import UnBlocked.Graphics.Animations.FrameAnimation;
import UnBlocked.Entities.Player;

public class Level
{
    //Player.
    private Player player;

    //Constants.
    public static final int TILE_BITS = 4, TILE_SIZE = 1 << TILE_BITS;

    //Dimensions and Tile arrays.
    private int width, height;
    private byte[] tiles, entities;

    //Tile animations.
    private Tile[][] tileAnims = new Tile[2][8];
    private byte num_backgroundTiles, num_playgroundTiles;

    //Camera.
    private LevelCamera camera = null;

    /**Constructor loading from file.*/
    public Level(File file)
    {
        this.camera = new LevelCamera(this);
    }

    /**Test Constructor.*/
    public Level()
    {
        //Make camera.
        this.camera = new LevelCamera(this);

        //Set dimensions.
        this.width = 6;
        this.height = 6;

        //Set tiles.
        this.tiles = new byte[]
        {
            8, 8, 8, 8, 8, 8,
            9, 0, 1, 1, 0, 9,
            9, 0, 1, 1, 0, 9,
            9, 0, 1, 1, 0, 9,
            9, 0, 1, 1, 0, 9,
            8, 8, 8, 8, 8, 8,
        };

        //Color vector.
        Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

        //Set animations.
        SpriteSheet testBrick = Sprites.loadTileSheet("TestBrick");
        tileAnims[0][0] = new Tile(new Sprite(testBrick, 0, 0, 20, 20), 4, -4, color);
        tileAnims[0][1] = new Tile(new Sprite(testBrick, 20, 0, 20, 20), 4, -4, color);
        tileAnims[1][0] = new Tile(new Sprite(testBrick, 0, 20, 20, 20), 0, 0, color);
        tileAnims[1][1] = new Tile(new Sprite(testBrick, 20, 20, 20, 20), 0, 0, color);

        num_backgroundTiles = 2;
        num_playgroundTiles = 2;

        //Set entities.
        this.entities = new byte[]
        {
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0, 0, 0, 1, 0, 0,
            0, 0, 0, 0, 0, 0,
        };

        player = new Player(3, 3);
        player.init(this);
    }

    public int getWidth(){return width;}
    public int getHeight(){return height;}
    
    /**Update function.*/
    public void update()
    {
        //Update Player and any moving Entities.
        player.update(1.0f);

        //Update background tile animations.
        for(int i = 0; i < num_backgroundTiles; i++)
        {tileAnims[0][i].update(1.0f);}

        //Update playground tile animations.
        for(int i = 0; i < num_playgroundTiles; i++)
        {tileAnims[1][i].update(1.0f);}
    }

    /**Render function.*/
    public void render(Screen screen)
    {
        //
        //Determine what tiles the camera can currently see.
        //

        //Get Camera Position
        int cameraX = (int)camera.getX(), cameraY = (int)camera.getY();

        //Set Screen offset relative to camera position.
        //screen.setCameraOffsets(camera.getScaledX(), camera.getScaledY());
        screen.setCameraOffsets(cameraX, cameraY);

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
        for(int y = y1-1; y >= y0; y--)
        {
            for(int x = x0; x < x1; x++)
            {
                //Get Tile ID.
                int tileID = getTileID_Unsafe(x, y),
                a = tileID / 8, slot = tileID % 8;

                //Get and render Tile Animation.
                Tile t = tileAnims[a][slot];
                t.render(screen, x << TILE_BITS, y << TILE_BITS, scale);


                //Get Entity ID.
                int entityID = getEntityID(x, y);

                //Nothing case.
                if(entityID == 0){continue;}

                //Player case.
                if(entityID == 1){player.render(screen, x, y, scale);}
            }
        }
    }

    /**Gets the Tile ID at the given Tile point without doing an out-of-bounds check.*/
    public byte getTileID_Unsafe(int x, int y){return tiles[x + y * width];}

    /**Gets the Tile ID at the given Tile point.*/
    public byte getTileID(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return 0;}

        return tiles[x + y * width];
    }

    /**Gets the Entity ID at the given Tile point.*/
    public byte getEntityID(int x, int y)
    {
        if(x < 0 || x >= width || y < 0 || y >= height)
        {return 0;}

        return entities[x + y * width];
    }
}
