package net.dv8tion.jda.internal;

import net.dv8tion.jda.internal.utils.cache.MemberCacheViewImpl;

public class UselessMemberCacheView extends MemberCacheViewImpl
{
    public UselessMemberCacheView(long selfId)
    {
        elements = new UselessMap<>(selfId);
    }
}
