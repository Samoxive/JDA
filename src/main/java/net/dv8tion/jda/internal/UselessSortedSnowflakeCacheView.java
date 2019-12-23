package net.dv8tion.jda.internal;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class UselessSortedSnowflakeCacheView<T extends ISnowflake & Comparable<? super T>> extends SortedSnowflakeCacheViewImpl<T>
{
    public UselessSortedSnowflakeCacheView(Class<T> type, Function<T, String> nameMapper, Comparator<T> comparator)
    {
        super(type, nameMapper, comparator);
        elements = new TotallyUselessMap<>();
    }

    @Nullable
    @Override
    public T getElementById(@Nonnull String id)
    {
        return null;
    }

    @Nullable
    @Override
    public <R> R applyStream(@Nonnull Function<? super Stream<T>, ? extends R> action)
    {
        return null;
    }

    @Override
    public void acceptStream(@Nonnull Consumer<? super Stream<T>> action)
    {

    }

    @Nonnull
    @Override
    public List<T> getElementsByName(@Nonnull String name)
    {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public <R, A> R collect(@Nonnull Collector<? super T, A, R> collector)
    {
        return null;
    }
}
