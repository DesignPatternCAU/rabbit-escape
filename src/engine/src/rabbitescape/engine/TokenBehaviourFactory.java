package rabbitescape.engine;

import rabbitescape.engine.tokenbehaviours.BreakBlockTokenBehaviour;
import rabbitescape.engine.tokenbehaviours.RabbitTokenBehaviour;
import rabbitescape.engine.tokenbehaviours.TokenBehaviour;

public class TokenBehaviourFactory {
    public static TokenBehaviour createTokenBehaviour(Token.Type type) {
        switch (type) {
            case breakblock:
                return new BreakBlockTokenBehaviour();
            default:
                return new RabbitTokenBehaviour();
        }
    }
}
