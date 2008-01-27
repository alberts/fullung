package net.lunglet.com4j;

import java.util.Iterator;

import com4j.Com4jObject;

public final class ComUtils {
    private ComUtils() {
    }

    public static <E extends Com4jObject> Iterable<E> queryIterable(final Iterable<? extends Com4jObject> iterable,
            final Class<E> clazz) {
        final Iterator<? extends Com4jObject> iterator = iterable.iterator();
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public E next() {
                        return iterator.next().queryInterface(clazz);
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                };
            }
        };
    }
}
