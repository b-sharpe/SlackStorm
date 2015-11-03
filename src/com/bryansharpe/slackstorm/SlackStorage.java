package com.bryansharpe.slackstorm;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;

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

    public String token;

    @Override
    public SlackStorage getState() {
        return this;
    }

    @Override
    public void loadState(SlackStorage slackStorage) {
        token = slackStorage.token;
    }

    public static SlackStorage getInstance() {
        return ServiceManager.getService(SlackStorage.class);
    }
}