package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public class LookupTable2D <T extends LookupItem2D> implements Iterable<T>
{
    private final class ItemIterator implements Iterator<T>
    {
        private int cursor;

        public ItemIterator()
        {
            this.cursor = 0;
        }

        @Override
        public boolean hasNext()
        {
            return cursor < list.size();
        }

        @Override
        public T next()
        {
            if( this.hasNext() )
            {
                return list.get( cursor++ );
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<T> iterator()
    {
        return new ItemIterator();
    }

    // Arrays of generics not allowed, use Vector instead
    private final Vector<Vector<LookupItems2D<T>>> table;
    private final List<T> list;
    /**
     * The size this table was created with. Note that changing the dimensions
     * will not change the size of an existing table.
     */
    public final Dimension size;

    public LookupTable2D( List<T> list, Dimension size )
    {
        // The table can store items +/-1 outside the nominal size.
        table = new Vector<Vector<LookupItems2D<T>>>( size.width + 2 );
        for ( int x = -1; x < size.width + 1 ; x++ )
        {
            table.add( new Vector<LookupItems2D<T>>( size.height + 2 ) );
            for ( int y = -1 ; y < size.height + 1 ; y++ )
            {
                table.get( i( x ) ).add( new LookupItems2D<T>( new Position( x, y ) ) );
            }
        }
        for ( T item: list)
        {
            Position position = item.getPosition();
            table.get( i( position.x ) ).get( i( position.y ) ).add( item );
        }
        this.list = list;
        this.size = size;
    }

    /**
     * Create an empty lookup table.
     *
     * @param size The dimensions of the table.
     */
    public LookupTable2D( Dimension size )
    {
        this( new ArrayList<T>(), size );
    }

    /**
     * @return The oldest item at this position.
     */
    public T getItemAt( int x, int y )
    {
        return table.get( i( x ) ).get( i( y ) ).getItem( 0 );
    }

    public List<T> getItemsAt( int x, int y )
    {
        return table.get( i( x ) ).get( i( y ) ).getItems();
    }

    public void addAll( List<T> newItems )
    {
        list.addAll( newItems );
        for ( T item: newItems)
        {
            Position position = item.getPosition();
            table.get( i( position.x ) ).get( i( position.y ) ).add( item );
        }
    }

    public void add( T newItem )
    {
        list.add( newItem );
        Position position = newItem.getPosition();
        table.get( i( position.x ) ).get( i( position.y ) ).add( newItem );
    }

    public void removeAll( List<T> itemsGoing )
    {
        list.removeAll( itemsGoing );
        for ( T item: itemsGoing )
        {
            Position position = item.getPosition();
            table.get( i ( position.x ) ).get( i( position.y ) ).remove( item );
        }
    }

    public void removeItemsAt( int x, int y )
    {
        removeAll( getItemsAt( x, y ) );
    }

    public List<T> getListCopy()
    {
        return new ArrayList<T>( list );
    }

    /**
     * Convert coordinate to index. This allows the table to
     * store items 1 place outside the nominal size.
     */
    private int i( int c )
    {
        return c + 1;
    }

    public Iterable<T> getItems()
    {
        return list;
    }
}
