package rabbitescape.render;

import java.util.Random;
import rabbitescape.engine.util.Position;
import rabbitescape.engine.CellularDirection;
import rabbitescape.engine.WaterRegion;
import rabbitescape.render.gameloop.WaterAnimation;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.Vertex;

public class WaterParticle
{

    private static final Random rand = new Random();
    /** Change in opacity per animation step. */
    public static final int alphaStepMagnitude = 32;
    /** Acceleration due to gravity in cells per animation step squared. */
    private final float gravity = 0.008f;
    /** Damping: fraction of velocity retained per animation step. */
    private final float damping = 0.95f;
    /** Converts flow into velocity */
    private final float flowFactor = 0.0002f;
    /** Variation in colour +/- half this on 0-255 scale. */
    private final float colVar = 60.0f;
    /** Half width of kite-shaped streak in nominal pixels (32 to a cell). */
    private final static float kite = 1.0f;

    /** Coordinates within world */
    public float x, y, lastX, lastY;
    /** Velocity in cells per animation step.*/
    private float vx, vy;
    /** Particles fade in and out. 0-255. */
    public int alpha = alphaStepMagnitude;
    public int alphaStep = alphaStepMagnitude;
    public int colR = 130, colG = 167, colB = 221;

    public WaterParticle(WaterRegionRenderer wrr)
    {
        // make a starting point in a cell
        boolean hasPipe = wrr.hasPipe();
        x = genPosInCell( hasPipe );
        y = genPosInCell( hasPipe );
        // create Coordinates in a part of the cell with the most flow
        if ( !hasPipe )
        {
            CellularDirection xBias = largeFlowMag( wrr, CellularDirection.LEFT,
                                                        CellularDirection.RIGHT );
            x = biasCoord( x, CellularDirection.LEFT, xBias);
            CellularDirection yBias = largeFlowMag( wrr, CellularDirection.UP,
                                                        CellularDirection.DOWN );
            y = biasCoord( y, CellularDirection.UP, xBias);
        }
        // move across the world to the correct cell
        Position p = wrr.region.getPosition();
        x += (float)p.x;
        y += (float)p.y;
        lastX = x; lastY = y;
        Vertex flow = wrr.netFlow();
        vx = genVelComponent( hasPipe ) + flow.x * flowFactor ;
        vy = genVelComponent( hasPipe ) + flow.y * flowFactor;
        // randomise colour
        colR += (int)( ( rand.nextFloat() - 0.5f ) * colVar );
        colG += (int)( ( rand.nextFloat() - 0.5f ) * colVar );
        colB += (int)( ( rand.nextFloat() - 0.5f ) * colVar );
    }

    private float genPosInCell( boolean hasPipe )
    {
        return hasPipe ?
               (rand.nextFloat() + 2f) / 5f : // middle fifth of cell
               rand.nextFloat() ;
    }

    private float genVelComponent( boolean hasPipe )
    {
        return hasPipe ?
               ( rand.nextFloat() - 0.5f ) / 4f : // sprays out more
               ( rand.nextFloat() - 0.5f ) / 16f ;
    }

    /**
     * Compares flow magnitude for the given directions: if one is
     * significantly larger than the other, it is returned. Returns null
     * if neither is much bigger.
     */
    private CellularDirection largeFlowMag(WaterRegionRenderer wrr,
                                           CellularDirection a, CellularDirection b)
    {
        int aMag = Math.abs(wrr.edgeNetFlow(a));
        int bMag = Math.abs(wrr.edgeNetFlow(b));
        if ( aMag > 2.0f * bMag )
        {
            return a;
        }
        if ( bMag > 2.0f * aMag )
        {
            return b;
        }
        return null;
    }

    private float biasCoord( float coord, CellularDirection lowerBiasDir,
                             CellularDirection biasDir)
    {
        if ( biasDir == null )
        {
            return coord;
        }
        coord = coord / 2.0f;
        if ( biasDir == lowerBiasDir )
        {
            return coord;
        }
        return coord + 0.5f;
    }

    /**
     * Constructor for tests
     */
    public WaterParticle(float x, float y)
    {
        this.x = x; this.y = y;
    }

    public boolean outOfRegion( WaterRegionRenderer wrr)
    {
        Position p = wrr.region.getPosition();
        float rx = (float)p.x, ry = (float)p.y;
        return  x < rx || y < ry ||
                x >= ( rx + 1f ) || y >= ( ry + 1f) ;
    }

    /**
     * return position, scaled by f and offset. Also offset from
     * region's origin to global origin.
     */
    public static Vertex position( float x_, float y_,
                                   float tileSize, Vertex offset )
    {
        return new Vertex(offset.x + x_ * tileSize,
                          offset.y + y_ * tileSize);
    }

    /**
     * return the WaterRegionRenderer for this particle's position.
     * May return null if the particle has fallen (or drifted sideways)
     * to a cell with no renderer.
     */
     public WaterRegionRenderer rendererByPosition( WaterAnimation wa)
     {
        int cx = (int)Math.floor(x), cy = (int)Math.floor(y);
        return wa.lookupRenderer.getItemAt( cx, cy );
     }

     /**
      * create a polygon to represent a streak of water.
      */
     public PolygonBuilder polygon()
     {
        // multiply by 32 to convert to units of nominal pixels
        Vertex last = ( new Vertex( lastX, lastY ) ).multiply( 32.0f );
        Vertex here = ( new Vertex( x, y ) ).multiply( 32.0f );
        // vector in the direction of travel
        Vertex direction = here.subtract(last);
        // vector from here to the tip of the kite
        Vertex tip = direction.multiply( kite / direction.magnitude() );
        // vector from here to one side of the kite
        Vertex side = tip.rot90();

        PolygonBuilder p = new PolygonBuilder();
        p.add( last );
        p.add( here.add( side ) );
        p.add( here.add( tip ) );
        p.add( here.subtract( side ) );

        return p;
     }

    /**
     * Apply momentum and gravity and damping. Called once per animation step.
     */
    public void step()
    {
        // store previous position, so streaks can be drawn.
        lastX = x; lastY = y;
        // apply momentum
        x += vx; y += vy;
        // apply gravity
        vy += gravity;
        // apply damping to limit top speed
        vx *= damping; vy *= damping;
    }
}