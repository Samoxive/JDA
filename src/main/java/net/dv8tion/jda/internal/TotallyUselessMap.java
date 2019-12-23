package net.dv8tion.jda.internal;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

public class TotallyUselessMap<T>  implements TLongObjectMap<T>
{
    @Override
    public long getNoEntryKey()
    {
        return 0;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean containsKey(long key)
    {
        return false;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return false;
    }

    @Override
    public T get(long key)
    {
        return null;
    }

    @Override
    public T put(long key, T value)
    {
        return null;
    }

    @Override
    public T putIfAbsent(long key, T value)
    {
        return null;
    }

    @Override
    public T remove(long key)
    {
        return null;
    }

    @Override
    public void putAll(Map<? extends Long, ? extends T> m)
    {

    }

    @Override
    public void putAll(TLongObjectMap<? extends T> map)
    {

    }

    @Override
    public void clear()
    {

    }

    @Override
    public TLongSet keySet()
    {
        return new TLongHashSet();
    }

    @Override
    public long[] keys()
    {
        return new long[0];
    }

    @Override
    public long[] keys(long[] array)
    {
        return array;
    }

    @Override
    public Collection<T> valueCollection()
    {
        return Collections.emptyList();
    }

    @Override
    public Object[] values()
    {
        return new Object[0];
    }

    @Override
    public T[] values(T[] array)
    {
        return array;
    }

    @Override
    public TLongObjectIterator<T> iterator()
    {
        return new TLongObjectIterator<T>()
        {
            @Override
            public long key()
            {
                return 0;
            }

            @Override
            public T value()
            {
                return null;
            }

            @Override
            public T setValue(T val)
            {
                return null;
            }

            @Override
            public void advance()
            {
                throw new NoSuchElementException();
            }

            @Override
            public boolean hasNext()
            {
                return false;
            }

            @Override
            public void remove()
            {

            }
        };
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure)
    {
        return false;
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super T> procedure)
    {
        return false;
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super T> procedure)
    {
        return false;
    }

    @Override
    public void transformValues(TObjectFunction<T, T> function)
    {

    }

    @Override
    public boolean retainEntries(TLongObjectProcedure<? super T> procedure)
    {
        return false;
    }
}
