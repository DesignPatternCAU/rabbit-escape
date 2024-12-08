package rabbitescape.engine.tokenPlacement;

import rabbitescape.engine.Token;
import rabbitescape.engine.World.UnableToAddToken;
import rabbitescape.engine.WorldChanges;

public interface TokenPlacementStrategy {
    /**
     * Attempt to add the token of the specified type at (x,y).
     * Throws the appropriate exceptions if not possible
     */
    void addToken(WorldChanges changes, int x, int y, Token.Type type) throws UnableToAddToken;
}