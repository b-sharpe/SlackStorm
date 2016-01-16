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

    public Map<String, String> settings = new HashMap<>();
    public Map<String, String> aliases  = new HashMap<>();
    public Map<String, String> icons  = new HashMap<>();

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

        settings.keySet()
                .stream()
                .forEach(key -> channelsRegistry.add(new SlackChannel(settings.get(key), key, aliases.get(key), icons.get(key))));
    }

    public void registerChannel(SlackChannel channel) {
        this.settings.put(channel.getId(), channel.getToken());
        this.aliases.put(channel.getId(), channel.getSenderName());
        this.icons.put(channel.getId(), channel.getSenderIcon());
        this.channelsRegistry.add(channel);
    }

    public void removeChannelByDescription(String description) {
        this.settings.remove(description);
        this.aliases.remove(description);
        this.icons.remove(description);
        this.channelsRegistry.remove(this.getSlackChannelByDescription(description));
    }

    public void clearAll() {
        this.settings.clear();
        this.aliases.clear();
        this.icons.clear();
        this.channelsRegistry.clear();
    }

    public SlackChannel getSlackChannelByDescription(String description) {
        return channelsRegistry.stream().filter(x -> x.id.equals(description)).findFirst().get();
    }

    public List<String> getChannelsId() {
        return channelsRegistry.stream().map(SlackChannel::getId).collect(Collectors.toList());
    }

    public static Icon getSlackIcon() {
        return IconLoader.getIcon("/icons/slack.png");
    }

    public static SlackStorage getInstance() {
        return ServiceManager.getService(SlackStorage.class);
    }
}