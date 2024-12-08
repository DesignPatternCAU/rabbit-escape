package rabbitescape.engine.tokenPlacement;

import rabbitescape.engine.*;

import java.util.ArrayList;
import java.util.List;

public class PortalTokenPlacementStrategy implements TokenPlacementStrategy {

    @Override
    public void addToken(WorldChanges changes, int x, int y, Token.Type type) throws World.UnableToAddToken {
        World world = changes.getWorld();
        Integer numLeft = world.abilities.get(type);

        if (numLeft == null) {
            throw new World.NoSuchAbilityInThisWorld(type);
        }

        if (numLeft == 0) {
            throw new World.NoneOfThisAbilityLeft(type);
        }

        if ( x < 0 || y < 0 || x >= world.size.width || y >= world.size.height )
        {
            throw new World.CantAddTokenOutsideWorld( type, x, y, world.size );
        }

        Block block = world.getBlockAt( x, y );
        if ( BehaviourTools.s_isFlat( block ) )
        {
            return;
        }

        // Check if placing on Exit
        if (world.isExitHere(x, y)) {
            return;
        }

        // Limit of 2 portals
        List<Token> portalTokens = new ArrayList<>();
        for (Thing thing : world.things) {
            if (thing instanceof Token) {
                Token t = (Token) thing;
                // exclude tokens to be removed (tokens in tokensToRemove list)
                if (t.type == Token.Type.portal && !changes.tokensToRemove.contains(t)) {
                    portalTokens.add(t);
                }
            }
        }
        // collect portal tokens from tokensToAdd
        for (Thing t : changes.tokensAboutToAppear()) {
            Token token = (Token) t;
            if (token.type == Token.Type.portal) {
                portalTokens.add(token);
            }
        }
        // if adding new portalToken leads to exceeding the limit ( 2 tokens max )
        if (portalTokens.size() >= 2) {
            Token oldestPortal = portalTokens.get(0);
            if (changes.tokensAboutToAppear().contains(oldestPortal)) {
                changes.removeTokenFromAddQueue(oldestPortal);
            } else {
                changes.removeToken(oldestPortal);
            }
        }
        // add new tokens to tokensToAdd
        changes.addTokenFromAddQueue( new Token( x, y, type, world) );
        world.abilities.put( type, numLeft - 1 );
    }
}