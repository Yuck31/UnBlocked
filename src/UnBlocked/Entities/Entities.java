package UnBlocked.Entities;
/**
 * 
 */

public class Entities
{
    private static final Entity[] ENTITIES =
    {
        new NullEntity(-1, -1),//Nothing
        new Player(0, 0),//Player
        null,//Boat0
        null,//Boat1
        null,//Boat2
        null,//Liquid0
        null,//Liquid1
        null,//Liquid2
        new Block(0, 0)
    };

    private Entities(){}

    public static Entity get(int index){return ENTITIES[index];}
}
