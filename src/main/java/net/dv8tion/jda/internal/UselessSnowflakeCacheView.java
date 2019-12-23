package net.dv8tion.jda.internal;

import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;

import java.util.function.Function;

public class UselessSnowflakeCacheView<T extends ISnowflake> extends SnowflakeCacheViewImpl<T>
{
    public UselessSnowflakeCacheView(Class<T> type, Function<T, String> nameMapper)
    {
        super(type, nameMapper);
        elements = new TotallyUselessMap<>();
    }
}
