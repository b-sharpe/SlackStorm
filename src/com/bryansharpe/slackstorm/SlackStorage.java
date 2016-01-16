package com.bryansharpe.slackstorm;

import com.intellij.openapi.components.*;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bsharpe on 11/2/2015.
 * Update by Anael Chardan "anael.chardan@gmail.com"
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

    protected List<SlackChannel> channelsRegistry = new ArrayList<>();

    @Override
    public SlackStorage getState() {
        return this;
    }

    @Override
    public void loadState(SlackStorage slackStorage) {
        settings = slackStorage.settings;
        aliases = slackStorage.aliases;
        icons = slackStorage.icons;

        for (String key : settings.keySet())
        {
            channelsRegistry.add(new SlackChannel(settings.get(key), key, aliases.get(key), icons.get(key)));
        }
    }

    public void registerChannel(SlackChannel channel)
    {
        settings.put(channel.getId(), channel.getToken());
        aliases.put(channel.getId(), channel.getSenderName());
        icons.put(channel.getId(), channel.getSenderIcon());
        channelsRegistry.add(channel);
    }

    public void clearAll()
    {
        this.settings.clear();
        this.channelsRegistry.clear();
    }

    public void clearChannelByDescription(String description)
    {
        this.settings.remove(description);
        this.channelsRegistry.remove(this.getSlackChannelByDescription(description));
    }

    public SlackChannel getSlackChannelByDescription(String description)
    {
        return channelsRegistry.stream().filter(x -> x.id.equals(description)).findFirst().get();
    }

    public List<String> getChannelsId()
    {
        return channelsRegistry.stream().map(SlackChannel::getId).collect(Collectors.toList());
    }

    public static Icon getIcon()
    {
        return IconLoader.getIcon("/icons/slack.png");
    }

    public static SlackStorage getInstance() {
        return ServiceManager.getService(SlackStorage.class);
    }
}