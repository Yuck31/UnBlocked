package UnBlocked.UI.Dialogue;
/**
 * Author: Luke Sullivan
 * Last Edit: 3/22/2022
 */
import UnBlocked.Graphics.SpriteSheet;
import UnBlocked.Graphics.Sprite;
import UnBlocked.Graphics.Sprites;

public class Dialogue
{
    public static SpriteSheet dialogueSheet = Sprites.global_UISheet("DialogueSprites");
    public static Sprite[] dialogueSprites = dialogueSheet.loadLayout("DialogueSprites");
}
