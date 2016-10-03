package com.bryansharpe.slackstorm;

import com.intellij.openapi.components.*;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bsharpe on 11/2/2015.
 * Updated by Anael Chardan "anael.chardan@gmail.com"
 * Updated by Clément GARBAY "clementgarbay@gmail.com" on 01/16/2016
 */
@State(
        name = "SlackStorage",
        storages = {
                @Storage(
                        file = StoragePathMacros.APP_CONFIG + "/slack_settings.xml"
                )
        }
)
public class SlackStorage implements PersistentStateComponent<SlackStorage> {

    public Map<String, String> settings = new HashMap<String, String>();
    public Map<String, String> aliases  = new HashMap<String, String>();
    public Map<String, String> icons  = new HashMap<String, String>();
    public Map<String, String> channels  = new HashMap<String, String>();

    protected List<SlackChannel> channelsRegistry = new ArrayList<SlackChannel>();

    @Override
    public SlackStorage getState() {
        return this;
    }

    @Override
    public void loadState(SlackStorage slackStorage) {
        settings = slackStorage.settings;
        aliases = slackStorage.aliases;
        icons = slackStorage.icons;
        channels = slackStorage.channels;

        for (String key: settings.keySet()) {
            channelsRegistry.add(new SlackChannel(settings.get(key), key, aliases.get(key), icons.get(key), channels.get(key)));
        }
    }

    public void registerChannel(SlackChannel channel) {
        this.settings.put(channel.getId(), channel.getToken());
        this.aliases.put(channel.getId(), channel.getSenderName());
        this.icons.put(channel.getId(), channel.getSenderIcon());
        this.channels.put(channel.getId(), channel.getChannelName());
        this.channelsRegistry.add(channel);
    }

    public void removeChannelByDescription(String description) {
        this.settings.remove(description);
        this.aliases.remove(description);
        this.icons.remove(description);
        this.channels.remove(description);
        this.channelsRegistry.remove(this.getSlackChannelByDescription(description));
    }

    public void clearAll() {
        this.settings.clear();
        this.aliases.clear();
        this.icons.clear();
        this.channels.clear();
        this.channelsRegistry.clear();
    }

    public SlackChannel getSlackChannelByDescription(String description) {
        for (SlackChannel slackChannel : channelsRegistry) {
            if (slackChannel.id.equals(description)) {
                return slackChannel;
            }
        }

        return null;
    }

    public List<String> getChannelsId() {
        List<String> channelsId = new ArrayList<String>();

        for (SlackChannel slackChannel : channelsRegistry) {
            channelsId.add(slackChannel.id);
        }

        return channelsId;
    }

    public static Icon getSlackIcon() {
        return IconLoader.getIcon("/icons/slack.png");
    }

    public static SlackStorage getInstance() {
        return ServiceManager.getService(SlackStorage.class);
    }
}