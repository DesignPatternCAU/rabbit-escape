package rabbitescape.engine.tokenbehaviours;


import rabbitescape.engine.Block;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class BreakBlockTokenBehaviour extends TokenBehaviour{

    @Override
    public void performStep(Token token, World world) {
        switch(token.state) {
            case TOKEN_FALLING:
            case TOKEN_FALL_TO_SLOPE:
                applyGravity(token,world);
                break;
            case TOKEN_ON_SLOPE:
            case TOKEN_STILL:
                removeBlock(token,world);
                break;
            default:
        }

    }

    private void removeBlock(Token token, World world) {

        Block belowBlock = world.getBlockAt(token.x, token.y + 1);
        Block onBlock = world.getBlockAt(token.x, token.y);
        if (onBlock != null) {
            world.changes.removeBlockAt(token.x, token.y);
            world.changes.removeToken(token);
            return;
        }
        if (belowBlock != null) {
            world.changes.removeBlockAt(token.x, token.y + 1);
            world.changes.removeToken(token);

        }
    }


}
