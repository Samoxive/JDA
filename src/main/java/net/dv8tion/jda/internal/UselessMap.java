package net.dv8tion.jda.internal;

import gnu.trove.function.TObjectFunction;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.*;

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
        if (self == null) {
            self = m.get(selfId);
        }
    }

    @Override
    public void putAll(TLongObjectMap<? extends T> map)
    {
        if (self == null) {
            self = map.get(selfId);
        }
    }

    @Override
    public void clear()
    {
        self = null;
    }

    @Override
    public TLongSet keySet()
    {
        TLongHashSet set = new TLongHashSet();
        if (self != null) {
            set.add(selfId);
        }
        return set;
    }

    @Override
    public long[] keys()
    {
        if (self != null) {
            return new long[]{selfId};
        }
        return new long[0];
    }

    @Override
    public long[] keys(long[] array)
    {
        if (self != null && array.length > 0) {
            array[0] = selfId;
        }
        return array;
    }

    @Override
    public Collection<T> valueCollection()
    {
        if (self != null) {
            return Collections.singletonList(self);
        }
        return Collections.emptyList();
    }

    @Override
    public Object[] values()
    {
        if (self != null) {
            return new Object[]{self};
        }
        return new Object[0];
    }

    @Override
    public T[] values(T[] array)
    {
        if (self != null && array.length > 0) {
            array[0] = self;
        }
        return array;
    }

    @Override
    public TLongObjectIterator<T> iterator()
    {
        if (self != null) {
            return new TLongObjectIterator() {
                private boolean done = self != null;
                @Override
                public long key()
                {
                    return selfId;
                }

                @Override
                public Object value()
                {
                    return self;
                }

                @Override
                public Object setValue(Object val)
                {
                    return null;
                }

                @Override
                public void advance()
                {
                    if (done) {
                        throw new NoSuchElementException();
                    }
                }

                @Override
                public boolean hasNext()
                {
                    return !done;
                }

                @Override
                public void remove()
                {

                }
            };
        }
        return null;
    }

    @Override
    public boolean forEachKey(TLongProcedure procedure)
    {
        if (self != null) {
            return procedure.execute(selfId);
        }
        return false;
    }

    @Override
    public boolean forEachValue(TObjectProcedure<? super T> procedure)
    {
        if (self != null) {
            return procedure.execute(self);
        }
        return false;
    }

    @Override
    public boolean forEachEntry(TLongObjectProcedure<? super T> procedure)
    {
        if (self != null) {
            return procedure.execute(selfId, self);
        }
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
        if (!(o instanceof TLongObjectMap)) {
            return false;
        }

        TLongObjectMap m = (TLongObjectMap) o;
        return Objects.equals(self, m.get(selfId));
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(self);
    }
}
