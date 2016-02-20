package rabbitescape.engine;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static rabbitescape.engine.Direction.DOWN;
import static rabbitescape.engine.Direction.LEFT;
import static rabbitescape.engine.Direction.RIGHT;
import static rabbitescape.engine.Direction.UP;

import java.util.Map;

import org.junit.Test;

import rabbitescape.engine.util.Util;

public class TestWaterRegion
{
    @Test
    public void roundTripPersistance()
    {
        WaterRegion start = new WaterRegion( 0, 0, Util.newSet( UP, LEFT, RIGHT, DOWN ), 25 );
        start.contents += 10;

        Map<String, String> persisted = start.saveState();
        WaterRegion end = new WaterRegion( 0, 0, null, 0 );
        end.restoreFromState( persisted );

        assertThat( end, equalTo( start ) );
    }
}
