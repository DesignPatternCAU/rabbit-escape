package rabbitescape.engine;

import rabbitescape.engine.tokenPlacement.BreakBlockTokenPlacementStrategy;
import rabbitescape.engine.tokenPlacement.DefaultTokenPlacementStrategy;
import rabbitescape.engine.tokenPlacement.PortalTokenPlacementStrategy;
import rabbitescape.engine.tokenPlacement.TokenPlacementStrategy;

import java.util.HashMap;
import java.util.Map;

public class TokenPlacementRegistry {
    private static final Map<Token.Type, TokenPlacementStrategy> strategies = new HashMap<>();

    static {
        // Register strategies for each token type
        strategies.put(Token.Type.portal, new PortalTokenPlacementStrategy());
        strategies.put(Token.Type.breakblock, new BreakBlockTokenPlacementStrategy());
    }

    public static TokenPlacementStrategy getStrategy(Token.Type type) {
        // If we haven't defined a strategy explicitly,
        // return a default strategy or throw an error.
        return strategies.getOrDefault(type, new DefaultTokenPlacementStrategy());
    }
}