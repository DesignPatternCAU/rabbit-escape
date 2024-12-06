package rabbitescape.engine.tokenbehaviours;


import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class RabbitTokenBehaviour extends TokenBehaviour{
    @Override
    public void performStep(Token token, World world) {

        switch (token.state){
            case TOKEN_FALLING:
            case TOKEN_FALL_TO_SLOPE:
                applyGravity(token,world);
                break;
            default:
                break;
        }
    }



}
