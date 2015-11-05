package com.bryansharpe.slackstorm;

import com.intellij.openapi.components.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bsharpe on 11/2/2015.
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

    @Override
    public SlackStorage getState() {
        return this;
    }

    @Override
    public void loadState(SlackStorage slackStorage) {
        settings = slackStorage.settings;
    }

    public static SlackStorage getInstance() {
        return ServiceManager.getService(SlackStorage.class);
    }
}