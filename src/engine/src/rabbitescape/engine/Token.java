package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.tokenbehaviours.TokenBehaviour;

public class Token extends Thing
{
    public static class UnknownType extends RabbitEscapeException
    {
        public final Type type;

        public UnknownType( Type type )
        {
            this.type = type;
        }

        private static final long serialVersionUID = 1L;
    }

    public static enum Type
    {
        bash,
        dig,
        bridge,
        block,
        climb,
        explode,
        brolly,
        portal,
        breakblock,
        jump
    }

    public final Type type;
    private final TokenBehaviour tokenbehaviour;

    public Token( int x, int y, Type type )
    {
        super( x, y, switchType( type, false, false, true ) );
        this.type = type;
        this.tokenbehaviour=TokenBehaviourFactory.createTokenBehaviour(type);
    }

    public Token( int x, int y, Type type, World world )
    {
        this( x, y, type );
        boolean onSlope = BehaviourTools.isSlope( world.getBlockAt( x, y ) );
        // Can't use calcNewState here since we have just been created, so
        // can't be moving (until a time step passes).
        state = switchType( type, false, false, onSlope );
    }

    private static State switchType(
        Type type, 
        boolean moving,
        boolean slopeBelow, 
        boolean onSlope 
    )
    {
        switch( type )
        {
            case bash:
            case dig:
            case bridge:
            case block:
            case climb:
            case explode:
            case brolly:
            case breakblock:
            case portal:
            case jump:
                return chooseState(
                moving, 
                slopeBelow, 
                onSlope,
                TOKEN_FALLING,
                TOKEN_STILL,
                TOKEN_FALL_TO_SLOPE,
                TOKEN_ON_SLOPE
                );

            default: throw new UnknownType( type );
        }
    }

    private static State chooseState( 
        boolean moving, 
        boolean slopeBelow,
        boolean onSlope, 
        State falling,
        State onFlat, 
        State fallingToSlope,
        State onSlopeState
    )
    {
        if ( onSlope )
        {
            return onSlopeState;
        }
        if ( !moving )
        {
            return onFlat;
        }
        if ( slopeBelow )
        {
            return fallingToSlope;
        }
        return falling;
    }

    @Override
    public void calcNewState( World world )
    {
        Block onBlock = world.getBlockAt( x, y );
        Block belowBlock = world.getBlockAt( x, y + 1 );
        boolean still = (
               BehaviourTools.s_isFlat( belowBlock )
            || ( onBlock != null )
            || BridgeTools.someoneIsBridgingAt( world, x, y )
        );

        state = switchType( type, !still,
            BehaviourTools.isSlope( belowBlock ),
            BehaviourTools.isSlope( onBlock ) );
    }

    @Override
    public void step( World world )
    {
        if(tokenbehaviour !=null) {
            tokenbehaviour.performStep(this,world);
        }
    }

    @Override
    public Map<String, String> saveState( boolean runtimeMeta )
    {
        return new HashMap<String, String>();
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
    }

    public static String name( Type ability )
    {
        String n = ability.name();
        return n.substring( 0, 1 ).toUpperCase() + n.substring( 1 );
    }

    @Override
    public String overlayText()
    {
        return type.toString();
    }
}
