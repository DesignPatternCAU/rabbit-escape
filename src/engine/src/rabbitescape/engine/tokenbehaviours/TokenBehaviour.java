package rabbitescape.engine.tokenbehaviours;


import rabbitescape.engine.World;
import rabbitescape.engine.Token;

public abstract class TokenBehaviour {
    public abstract void performStep(Token token, World world);


    protected void applyGravity(Token token, World world) {

        token.y++;
        if (token.y >= world.size.height) {
            world.changes.removeToken(token);
        }
    }
}
