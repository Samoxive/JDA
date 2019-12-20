package net.dv8tion.jda.internal;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;

import java.util.function.Function;

public class UselessSnowflakeCacheViewImpl<T extends ISnowflake> extends SnowflakeCacheViewImpl<T>
{
    public UselessSnowflakeCacheViewImpl(Class<T> type, Function<T, String> nameMapper, long selfId)
    {
        super(type, nameMapper);
        elements = new UselessMap<>(selfId);
    }
}
