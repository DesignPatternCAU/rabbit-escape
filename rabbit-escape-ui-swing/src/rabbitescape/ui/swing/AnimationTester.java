package rabbitescape.ui.swing;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.t;

public class AnimationTester extends JFrame
{
    private static class AnimationNotFound extends RabbitEscapeException
    {
        public final String name;

        public AnimationNotFound( String name )
        {
            this.name = name;
        }
    }

    private class Listener implements java.awt.event.MouseListener
    {
        @Override
        public void mouseClicked( MouseEvent mouseEvent )
        {
            int i = screen2index( mouseEvent.getX(), mouseEvent.getY() );

            Object[] possibilities = { "<none>", "walk", "bash" };
            String changedAnimation = (String)JOptionPane.showInputDialog(
                null,
                "Choose animation:",
                "Change animation",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                animations[i]
            );

            if ( changedAnimation.equals( "<none>") )
            {
                changedAnimation = "";
            }

            animations[i] = changedAnimation;
            loadBitmaps();
        }

        @Override
        public void mousePressed( MouseEvent mouseEvent )
        {
        }

        @Override
        public void mouseReleased( MouseEvent mouseEvent )
        {
        }

        @Override
        public void mouseEntered( MouseEvent mouseEvent )
        {
        }

        @Override
        public void mouseExited( MouseEvent mouseEvent )
        {
        }
    }

    private final int tileSize;
    private final int numTilesX;
    boolean running;

    private final java.awt.Canvas canvas;
    private SwingBitmapScaler scaler;
    private SwingPaint paint;
    private SwingBitmap[][] frames;

    private String[] animations = new String[] {
        "", "", "",
        "walk", "walk", "bash",
        "", "", ""
    };

    private static class InitUi implements Runnable
    {
        public AnimationTester animationTester;

        @Override
        public void run() {
            animationTester = new AnimationTester();
            synchronized ( this )
            {
                notifyAll();
            }
        }

        public synchronized void loopWhenReady()
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                // Will come when notified
            }
            animationTester.loop();
        }
    }

    public AnimationTester()
    {
        this.tileSize = 128;
        this.numTilesX = 3;
        int numTilesY = 3;

        setIgnoreRepaint( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        this.canvas = new java.awt.Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize(
            new java.awt.Dimension( tileSize * numTilesX, tileSize * numTilesY) );

        getContentPane().add( canvas, java.awt.BorderLayout.CENTER );

        loadBitmaps();

        canvas.addMouseListener(new Listener());

        setTitle( t( "Animation Tester" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );
    }

    private void loadBitmaps()
    {
        scaler = new SwingBitmapScaler();
        paint = new SwingPaint();
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        frames = loadAllFrames( bitmapLoader, animations );
    }

    private SwingBitmap[][] loadAllFrames( SwingBitmapLoader bitmapLoader, String[] animations )
    {
        SwingBitmap[][] ret = new SwingBitmap[animations.length][];
        int i = 0;
        for ( String animation : animations )
        {
            if ( !animation.isEmpty() )
            {
                ret[i] = loadFrames(
                    bitmapLoader, loadAnimation( animation ) );
            }
            ++i;
        }
        return ret;
    }

    private String[] loadAnimation( String name )
    {
        try
        {
            String key = "/rabbitescape/render/animations/" + name + ".rea";

            URL url = getClass().getResource( key );
            if ( url == null )
            {
                System.out.println( key + " not found." );
                throw new AnimationNotFound( name );
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader( url.openStream() ) );

            List<String> ret = new ArrayList<>();
            String ln;
            while ( ( ln = reader.readLine() ) != null )
            {
                String trimmedLn = ln.trim();
                if ( !trimmedLn.isEmpty() )
                {
                    ret.add(ln.trim());
                }
            }

            return ret.toArray( new String[ ret.size() ] );
        }
        catch ( IOException e )
        {
            throw new AnimationNotFound( name );
        }
    }

    private SwingBitmap[] loadFrames(
        SwingBitmapLoader bitmapLoader, String[] animation )
    {
        SwingBitmap[] ret = new SwingBitmap[animation.length];
        int i = 0;
        for ( String frameName : animation )
        {
            ret[i] = bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/" + frameName + ".png" );
            ++i;
        }
        return ret;
    }

    public static void main( String[] args )
    {
        InitUi initUi = new InitUi();
        SwingUtilities.invokeLater(initUi);
        initUi.loopWhenReady();
        initUi.animationTester.loop();
    }

    private void loop()
    {
        BufferStrategy strategy = canvas.getBufferStrategy();

        Renderer renderer = new Renderer( 0, 0, tileSize );

        int frameNum = 0;
        running = true;
        while( running && this.isVisible() )
        {
            new DrawFrame( strategy, renderer, frameNum ).run();

            pause();

            ++frameNum;
            if ( frameNum == 10 )
            {
                frameNum = 0;
            }
        }
    }

    private class DrawFrame extends BufferedDraw
    {
        private final int frameNum;
        private final Renderer renderer;

        public DrawFrame(
            BufferStrategy strategy, Renderer renderer, int frameNum )
        {
            super( strategy );
            this.renderer = renderer;
            this.frameNum = frameNum;
        }

        @Override
        void draw( java.awt.Graphics2D g )
        {
            g.setPaint( java.awt.Color.WHITE );
            g.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
            );

            List<Sprite> sprites = new ArrayList<>();
            int i = 0;
            for ( SwingBitmap[] bmps : frames )
            {
                if ( bmps == null )
                {
                    ++i;
                    continue;
                }
                java.awt.Point loc = int2dim( i );
                Sprite sprite = new Sprite(
                    bmps[frameNum], scaler, loc.x, loc.y, 32, 0, 0 );
                sprites.add( sprite );
                ++i;
            }

            renderer.render(
                new SwingCanvas( g ),
                    sprites.toArray( new Sprite[sprites.size() ] ),
                paint
            );
        }
    }

    private java.awt.Point int2dim( int i )
    {
        return new java.awt.Point( i % 3, i / 3 );
    }

    private int screen2index( int x, int y )
    {
        return ( numTilesX * ( y / tileSize ) ) + ( x / tileSize );
    }

    private void pause()
    {
        try
        {
            Thread.sleep( 100 );
        }
        catch (InterruptedException e)
        {
            // Ignore
        }
    }
}
