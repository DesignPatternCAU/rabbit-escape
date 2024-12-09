package rabbitescape.engine.tokenPlacement;

import rabbitescape.engine.*;

public class DefaultTokenPlacementStrategy implements TokenPlacementStrategy{
    @Override
    public void addToken(WorldChanges changes, int x, int y, Token.Type type) throws World.UnableToAddToken {
        World world = changes.getWorld();
        Integer numLeft = world.abilities.get( type );

        if ( numLeft == null )
        {
            throw new World.NoSuchAbilityInThisWorld( type );
        }

        if ( numLeft == 0 )
        {
            throw new World.NoneOfThisAbilityLeft( type );
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

        // add new tokens to tokensToAdd
        changes.addTokenFromAddQueue( new Token( x, y, type, world) );
        world.abilities.put( type, numLeft - 1 );
    }
}