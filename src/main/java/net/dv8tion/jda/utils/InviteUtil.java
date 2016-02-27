/**
 *    Copyright 2015-2016 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.utils;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.exceptions.PermissionException;
import net.dv8tion.jda.hooks.EventListener;
import net.dv8tion.jda.hooks.SubscribeEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class InviteUtil
{
    /**
     * Takes an invite url or invite code and changes it into an {@link net.dv8tion.jda.utils.InviteUtil.Invite Invite}.
     * If the url or code isn't a proper invite, this returns <code>null</code>
     *
     * @param code
     *          The invite url or code
     * @return
     *      An {@link net.dv8tion.jda.utils.InviteUtil.Invite Invite} representing the invite specified by the provided
     *      code or url. This will return <code>null</code> if the provided code or url is invalid.
     * @throws java.lang.NullPointerException
     *      If the provided String is null.
     */
    public static Invite resolve(String code)
    {
        if (code == null)
            throw new NullPointerException("The provided invite code/url was null.");
        if (code.startsWith("http"))
        {
            String[] split = code.split("/");
            code = split[split.length - 1];
        }
        JSONObject response = new JDAImpl(false).getRequester().get("https://discordapp.com/api/invite/" + code);
        if (response.has("code"))
        {
            JSONObject guild = response.getJSONObject("guild");
            JSONObject channel = response.getJSONObject("channel");
            return new Invite(response.getString("code"), response.isNull("xkcdpass") ? null : response.getString("xkcdpass"), guild.getString("name"), guild.getString("id"),
                    channel.getString("name"), channel.getString("id"), channel.getString("type").equals("text"));
        }
        return null;
    }

    /**
     * Creates a standard-invite (valid for 24hrs, infinite usages, permanent access and not human-readable).
     * To create a customized Invite, use {@link #createInvite(Channel, JDA, InviteDuration, int, boolean, boolean)} instead.
     *
     * @param chan
     *      The channel to create the invite for.
     * @param jda
     *      The JDA-instance from who the invite should be created from.
     * @return
     *      The created AdvancedInvite object.
     * @throws net.dv8tion.jda.exceptions.PermissionException
     *      If the account connected to the provided JDA object does not have
     *      {@link net.dv8tion.jda.Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE} for the provided channel.
     */
    public static AdvancedInvite createInvite(Channel chan, JDA jda)
    {
        return createInvite(chan, jda, InviteDuration.ONE_DAY, 0, false, false);
    }

    /**
     * Creates an advanced invite.
     *
     * @param chan
     *      The channel to create the invite for.
     * @param jda
     *      The JDA-instance from who the invite should be created from.
     * @param duration
     *      The duration the invide should be valid for.
     * @param maxUses
     *      The maximum amount of usages of this invite. 0 means infinite usages.
     * @param temporary
     *      Whether or not the invite should only grant temporary access to the Guild (members will get removed after they log out, unless they get a role assigned).
     * @param humanReadable
     *      Wheter or not the invite should be in human-readable form.
     * @return
     *      The created AdvancedInvite object.
     * @throws net.dv8tion.jda.exceptions.PermissionException
     *      If the account connected to the provided JDA object does not have
     *      {@link net.dv8tion.jda.Permission#CREATE_INSTANT_INVITE Permission.CREATE_INSTANT_INVITE} for the provided channel.
     */
    public static AdvancedInvite createInvite(Channel chan, JDA jda, InviteDuration duration, int maxUses, boolean temporary, boolean humanReadable)
    {
        if (!chan.checkPermission(jda.getSelfInfo(), Permission.CREATE_INSTANT_INVITE))
            throw new PermissionException(Permission.CREATE_INSTANT_INVITE);

        maxUses = Math.max(0, maxUses);
        JSONObject response = ((JDAImpl) jda).getRequester().post("https://discordapp.com/api/channels/" + chan.getId() + "/invites",
                new JSONObject()
                        .put("max_age", duration.getDuration())
                        .put("temporary", temporary)
                        .put("max_uses", maxUses)
                        .put("xkcdpass", humanReadable));
        if (response.has("code"))
        {
            return AdvancedInvite.fromJson(response, jda);
        }
        return null;
    }

    @Deprecated
    public static void join(Invite invite, JDA jda)
    {
        join(invite, jda, null);
    }

    /**
     * Uses joins the {@link net.dv8tion.jda.entities.Guild Guild} specified by the provided
     * {@link net.dv8tion.jda.utils.InviteUtil.Invite Invite}. Joining invites is always an async process, so you can
     * provided a {@link java.util.function.Consumer Consumer} to define code to be run after the
     * {@link net.dv8tion.jda.entities.Guild Guild} is joined.<br>
     * Example:
     * <pre>{@code
     * InviteUtil.join(Invite, JDA, joinedGuild ->
     * {
     *     System.out.println("I just joined a guild called: " + joinedGuild.getName();
     * });
     * }</pre>
     * <b>Note:</b> If you do not wish to execute any code after joining, provide <code>null</code> for the consumer.
     *
     * @param invite
     *          The {@link net.dv8tion.jda.utils.InviteUtil.Invite Invite} which to join.
     * @param jda
     *          The {@link net.dv8tion.jda.JDA JDA} instance which will join the {@link net.dv8tion.jda.entities.Guild Guild}
     *          specified by the invie.
     * @param callback
     *          The code which will be executed when the {@link net.dv8tion.jda.entities.Guild Guild} has been successfully
     *          joined. This value can be <code>null</code>.
     */
    public static void join(Invite invite, JDA jda, Consumer<Guild> callback)
    {
        if (invite == null)
            throw new NullPointerException("The provided Invite was null!");
        if (jda == null)
            throw new NullPointerException("The provided JDA object was null!");
        if(callback != null)
            jda.addEventListener(new AsyncCallback(invite.getGuildId(), callback));
        ((JDAImpl) jda).getRequester().post("https://discordapp.com/api/invite/" + invite.getCode(), new JSONObject());
    }

    @Deprecated
    public static void join(String code, JDA jda)
    {
        join(code, jda, null);
    }

    /**
     * Uses joins the {@link net.dv8tion.jda.entities.Guild Guild} specified by the provided
     * invite code/url. Joining invites is always an async process, so you can
     * provided a {@link java.util.function.Consumer Consumer} to define code to be run after the
     * {@link net.dv8tion.jda.entities.Guild Guild} is joined.<br>
     * Example:
     * <pre>{@code
     * InviteUtil.join(Invite, JDA, joinedGuild ->
     * {
     *     System.out.println("I just joined a guild called: " + joinedGuild.getName();
     * });
     * }</pre>
     * <b>Note:</b> If you do not wish to execute any code after joining, provide <code>null</code> for the consumer.
     *
     * @param code
     *          The code/url of the invite which to join.
     * @param jda
     *          The {@link net.dv8tion.jda.JDA JDA} instance which will join the {@link net.dv8tion.jda.entities.Guild Guild}
     *          specified by the invite.
     * @param callback
     *          The code which will be executed when the {@link net.dv8tion.jda.entities.Guild Guild} has been successfully
     *          joined. This value can be <code>null</code>.
     */
    public static void join(String code, JDA jda, Consumer<Guild> callback)
    {
        Invite invite = resolve(code);
        if (invite == null)
            throw new IllegalArgumentException("The provided Invite code was invalid, thus JDA cannot " +
                    "attempt to join using it! Provided Code: " + code);
        join(invite, jda, callback);
    }

    /**
     * Tells Discord to delete the specified Invite from the server. After deleting, this Invite will no longer work
     * for anyone.
     *
     * @param invite
     *          The {@link net.dv8tion.jda.utils.InviteUtil.Invite Invite} to delete.
     * @param jda
     *          The account to use to delete the provided invite.
     * @throws net.dv8tion.jda.exceptions.PermissionException
     *          Thrown if the account connected to the provided JDA object does not have permission
     *          to delete the invite.
     */
    public static void delete(Invite invite, JDA jda)
    {
        Channel channel;
        if (invite.isTextChannel)
            channel = jda.getTextChannelById(invite.getChannelId());
        else
            channel = jda.getVoiceChannelById(invite.getChannelId());

        //If the channel is null, that means JDA doesn't know about it, so we absolutely don't have permission to delete.
        if (channel == null || !channel.checkPermission(jda.getSelfInfo(), Permission.MANAGE_CHANNEL))
            throw new PermissionException(Permission.MANAGE_CHANNEL,
                    "JDA cannot delete the invite because the currently logged in account does not have permission");

        ((JDAImpl) jda).getRequester().delete("https://discordapp.com/api/invite/" + invite.getCode());
    }

    /**
     * Tells Discord to delete the specified Invite from the server. After deleting, this Invite will no longer work
     * for anyone.
     *
     * @param code
     *          The invite code or url specifying the discord invite to delete.
     * @param jda
     *          The account to use to delete the provided invite.
     * @throws net.dv8tion.jda.exceptions.PermissionException
     *          Thrown if the account connected to the provided JDA object does not have permission
     *          to delete the invite.
     * @throws java.lang.IllegalArgumentException
     *          Thrown if the provided invite code/url was an invalid Invite.
     */
    public static void delete(String code, JDA jda)
    {
        Invite invite = resolve(code);
        if (invite == null)
            throw new IllegalArgumentException("The provided Invite code was invalid, thus JDA cannot attempt" +
                    "to delete it! Provided Code: " + code);

        delete(invite, jda);
    }

    /**
     * Provides a list of all {@link net.dv8tion.jda.utils.InviteUtil.AdvancedInvite Invites} for the given {@link net.dv8tion.jda.entities.Guild Guild}.
     *
     * @param guildObj
     *          The Guild whose invites are being retrieved.
     * @return
     *      An Immutable List of {@link net.dv8tion.jda.utils.InviteUtil.AdvancedInvite Invites}.
     */
    public static List<AdvancedInvite> getInvites(Guild guildObj)
    {
        if (!PermissionUtil.checkPermission(guildObj.getJDA().getSelfInfo(), Permission.MANAGE_SERVER, guildObj))
            throw new PermissionException(Permission.MANAGE_SERVER);

        List<AdvancedInvite> invites = new ArrayList<>();

        JSONArray array = ((JDAImpl)guildObj.getJDA()).getRequester().getA("https://discordapp.com/api/guilds/" + guildObj.getId() + "/invites");

        for (int i = 0; i < array.length(); i++)
        {
            JSONObject invite = array.getJSONObject(i);

            if (invite.has("code"))
            {
                invites.add(AdvancedInvite.fromJson(invite, guildObj.getJDA()));
            }
        }

        return Collections.unmodifiableList(invites);
    }

    /**
     * Provides a list of all {@link net.dv8tion.jda.utils.InviteUtil.AdvancedInvite Invites} for the given {@link net.dv8tion.jda.entities.Channel Channel}.
     *
     * @param channelObj
     *          The Guild whose invites are being retrieved.
     * @return
     *      An Immutable List of {@link net.dv8tion.jda.utils.InviteUtil.AdvancedInvite Invites}.
     */
    public static List<AdvancedInvite> getInvites(Channel channelObj)
    {
        if (!PermissionUtil.checkPermission(channelObj.getJDA().getSelfInfo(), Permission.MANAGE_CHANNEL, channelObj))
            throw new PermissionException(Permission.MANAGE_CHANNEL);

        List<AdvancedInvite> invites = new ArrayList<>();

        JSONArray array = ((JDAImpl)channelObj.getJDA()).getRequester().getA("https://discordapp.com/api/channels/" + channelObj.getId() + "/invites");

        for (int i = 0; i < array.length(); i++)
        {
            JSONObject invite = array.getJSONObject(i);

            if (invite.has("code"))
            {
                invites.add(AdvancedInvite.fromJson(invite, channelObj.getJDA()));
            }
        }

        return Collections.unmodifiableList(invites);
    }

    public static class Invite
    {
        private final String code;
        private final String humanCode;
        private final String guildName, guildId;
        private final String channelName, channelId;
        private final boolean isTextChannel;

        private Invite(String code, String humanCode, String guildName, String guildId, String channelName, String channelId, boolean isTextChannel)
        {
            this.code = code;
            this.humanCode = humanCode;
            this.guildName = guildName;
            this.guildId = guildId;
            this.channelName = channelName;
            this.channelId = channelId;
            this.isTextChannel = isTextChannel;
        }

        public String getCode()
        {
            return code;
        }

        public String getHumanCode()
        {
            return humanCode;
        }

        public String getUrl()
        {
            return "https://discord.gg/" + (humanCode == null ? code : humanCode);
        }

        public String getGuildName()
        {
            return guildName;
        }

        public String getGuildId()
        {
            return guildId;
        }

        public String getChannelName()
        {
            return channelName;
        }

        public String getChannelId()
        {
            return channelId;
        }

        public boolean isTextChannel()
        {
            return isTextChannel;
        }
    }

    public static class AdvancedInvite extends Invite {

        private final InviteDuration duration;
        private final String guildSplashHash;
        private final boolean temporary;
        private final int maxUses;
        private final OffsetDateTime createdAt;
        private final int uses;
        //TODO what happens if the inviter left the server (and therefore is unknown for the api)?
        private final User inviter;
        private final boolean revoked;

        private AdvancedInvite(String code, String humanCode, String guildName, String guildId, String channelName, String channelId, boolean isTextChannel, InviteDuration duration, String guildSplashHash, boolean temporary,
                int maxUses, OffsetDateTime createdAt, int uses, User inviter, boolean revoked) {
            super(code, humanCode, guildName, guildId, channelName, channelId, isTextChannel);
            this.duration = duration;
            this.guildSplashHash = guildSplashHash;
            this.temporary = temporary;
            this.maxUses = maxUses;
            this.createdAt = createdAt;
            this.uses = uses;
            this.inviter = inviter;
            this.revoked = revoked;
        }

        public InviteDuration getDuration()
        {
            return duration;
        }

        public String getGuildSplashHash()
        {
            return guildSplashHash;
        }

        public boolean isTemporary()
        {
            return temporary;
        }

        public int getMaxUses()
        {
            return maxUses;
        }

        public OffsetDateTime getCreatedAt()
        {
            return createdAt;
        }

        public int getUses()
        {
            return uses;
        }

        public User getInviter()
        {
            return inviter;
        }

        public boolean isRevoked()
        {
            return revoked;
        }

        private static AdvancedInvite fromJson(JSONObject object, JDA jda)
        {
            JSONObject guild = object.getJSONObject("guild");
            JSONObject channel = object.getJSONObject("channel");
            JSONObject inviter = object.getJSONObject("inviter");

            return new AdvancedInvite(
                    object.getString("code"),
                    object.isNull("xkcdpass") ? null : object.getString("xkcdpass"),
                    guild.getString("name"),
                    guild.getString("id"),
                    channel.getString("name"),
                    channel.getString("id"),
                    channel.getString("type").equals("text"),
                    InviteDuration.getFromDuration(object.getInt("max_age")),
                    guild.isNull("splash_hash") ? null : guild.getString("splash_hash"),
                    object.getBoolean("temporary"),
                    object.getInt("max_uses"),
                    OffsetDateTime.parse(object.getString("created_at")),
                    object.getInt("uses"),
                    jda.getUserById(inviter.getString("id")),
                    object.getBoolean("revoked"));
        }
    }

    enum InviteDuration {
        INFINITE(0), THIRTY_MINUTES(1800),
        ONE_HOUR(3600), SIX_HOURS(6*3600), TWELVE_HOURS(12*3600),
        ONE_DAY(24*3600);

        private final int duration;

        InviteDuration(int duration)
        {
            this.duration = duration;
        }

        private int getDuration()
        {
            return duration;
        }

        private static InviteDuration getFromDuration(int duration)
        {
            for (InviteDuration dur : InviteDuration.values())
            {
                if (dur.getDuration() == duration)
                {
                    return dur;
                }
            }
            return INFINITE;
        }
    }

    private static class AsyncCallback implements EventListener
    {
        private final String id;
        private final Consumer<Guild> cb;

        public AsyncCallback(String id, Consumer<Guild> cb)
        {
            this.id = id;
            this.cb = cb;
        }

        @Override
        @SubscribeEvent
        public void onEvent(Event event)
        {
            if (event instanceof GuildJoinEvent && ((GuildJoinEvent) event).getGuild().getId().equals(id))
            {
                event.getJDA().removeEventListener(this);
                cb.accept(((GuildJoinEvent) event).getGuild());
            }
        }
    }
}
