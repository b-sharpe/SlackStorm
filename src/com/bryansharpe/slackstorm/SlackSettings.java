package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

/**
 * Created by bsharpe on 11/2/2015.
 *
 * Create the main toolbar group with add/clear
 *
 * @TODO: allow removing of individual channels
 */
public class SlackSettings extends ActionGroup {
    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {
        final AnAction[] children = new AnAction[2];

        children[0] = new addChannel();
        children[1] = new removeChannels();

        return children;
    }

    /**
     * Add a new channel.
     * @todo: should have a better key system rather than a full text string
     */
    public class addChannel extends AnAction {
        public addChannel() {
            super("Add Slack Channel");
        }
        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);
            boolean validSetting = false;

            String description = Messages.showInputDialog(
                    project,
                    "Enter a Description", "Slack Settings",
                    IconLoader.getIcon("/icons/slack.png")
            );

            // Don't bother if description wasn't entered since we need a good key for display.
            // See main to-do about keys.
            if (description != null && !description.isEmpty()) {

                String userAlias = Messages.showInputDialog(
                        project,
                        "Username to post as:", "Slack Settings",
                        IconLoader.getIcon("/icons/slack.png"),
                        "SlackStorm",
                        null
                );

                if (userAlias != null && !userAlias.isEmpty()) {

                    String token = Messages.showInputDialog(
                            project,
                            "Enter your slack webhook integration path (i.e. <xxx>/<yyy>/<zzz>.", "Slack Settings",
                            IconLoader.getIcon("/icons/slack.png")
                    );

                    // All good
                    if (token != null && !token.isEmpty()) {
                        validSetting = true;
                        SlackStorage slackStorage = SlackStorage.getInstance();
                        slackStorage.settings.put(description, token);
                        slackStorage.aliases.put(description, userAlias);
                        Messages.showMessageDialog(project, "Settings Saved.", "Information", Messages.getInformationIcon());
                    }
                }
            }

            if (!validSetting) {
                Messages.showMessageDialog(project, "Field required.", "Error", Messages.getErrorIcon());
            }
        }
    }

    /**
     * Clear all channels from settings
     */
    public class removeChannels extends AnAction {
        public removeChannels() {
            super("Reset Slack Channels");
        }
        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);

            // Prompt since we are killing ALL
            int confirm = Messages.showYesNoDialog(project, "This will clear all of your channel settings", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));
            if (confirm == 0) {
                SlackStorage slackStorage = SlackStorage.getInstance();
                slackStorage.settings.clear();
                slackStorage.aliases.clear();
                Messages.showMessageDialog(project, "Settings Cleared.", "Information", Messages.getInformationIcon());
            }
        }
    }

}
