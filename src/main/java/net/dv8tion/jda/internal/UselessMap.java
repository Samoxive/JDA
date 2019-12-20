package net.dv8tion.jda.internal;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;

import java.util.Collection;
import java.util.Map;

public class UselessMap<T> implements TLongObjectMap<T>
{
    private long selfId;
    private T self;

    public UselessMap(long selfId)
    {
        this.selfId = selfId;
    }

    @Override
    public long getNoEntryKey()
    {
        return 0;
    }

    @Override
    public int size()
    {
        return self != null ? 1 : 0;
    }

    @Override
    public boolean isEmpty()
    {
        return self != null;
    }

    @Override
    public boolean containsKey(long key)
    {
        return self != null && key == selfId;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return self != null && self.equals(value);
    }

    @Override
    public T get(long key)
    {
        return selfId == key ? self : null;
    }

    @Override
    public T put(long key, T value)
    {
        return selfId == key ? self = value : null;
    }

    @Override
    public T putIfAbsent(long key, T value)
    {
        return selfId == key ? self == null ? self = value : null : null;
    }

    @Override
    public T remove(long key)
    {
        if (selfId == key) {
            T s = self;
            self = null;
            return s;
        }

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
        self = null;
    }

    @Override
    public TLongSet keySet()
    {
        return null;
    }

    @Override
    public long[] keys()
    {
        return new long[0];
    }

    @Override
    public long[] keys(long[] array)
    {
        return new long[0];
    }

    @Override
    public Collection<T> valueCollection()
    {
        return null;
    }

    @Override
    public Object[] values()
    {
        return new Object[0];
    }

    @Override
    public T[] values(T[] array)
    {
        return null;
    }

    @Override
    public TLongObjectIterator<T> iterator()
    {
        return null;
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

    @Override
    public boolean equals(Object o)
    {
        return false;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
