package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by bsharpe on 11/2/2015.
 * Updated by Anael Chardan "anael.chardan@gmail.com"
 * Updated by Clément GARBAY "clementgarbay@gmail.com" on 01/16/2016
 *
 * Create the main toolbar group with add/clear
 */
public class SlackSettings extends ActionGroup {
    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {
        final AnAction[] children = new AnAction[3];

        children[0] = new addChannel();
        children[1] = new removeChannel();
        children[2] = new removeChannels();

        return children;
    }

    /**
     * Add a new channel
     */
    public class addChannel extends AnAction {

        private Project project;

        public addChannel() {
            super("Add Slack Channel");
        }

        public void actionPerformed(AnActionEvent e) {
            this.project = e.getData(CommonDataKeys.PROJECT);

            String description = this.showInputDialog(SlackChannel.getIdDescription(), null);

            if (!isValidField(description)) {
                errorMessage();
                return;
            }

            String userAlias = this.showInputDialog(SlackChannel.getSenderNameDescription(), SlackChannel.getSenderNameDefaultValue());

            if (!isValidField(userAlias)) {
                errorMessage();
                return;
            }

            String icon = this.showInputDialog(SlackChannel.getSenderIconDescription(), SlackChannel.getDefaultSenderIcon());

            if (!isValidField(icon)) {
                errorMessage();
                return;
            }

            String token = this.showInputDialog(SlackChannel.getTokenDescription(), null);

            if (!isValidField(token)) {
                errorMessage();
                return;
            }

            // Here all is good, we can create the channel
            SlackStorage.getInstance().registerChannel(new SlackChannel(token, description, userAlias, icon));
            Messages.showMessageDialog(this.project, "Settings Saved.", "Information", Messages.getInformationIcon());
        }

        protected String showInputDialog(String keyDescription, String keyDefaultValue) {
            return Messages.showInputDialog(
                    this.project,
                    keyDescription,
                    SlackChannel.getSettingsDescription(),
                    SlackStorage.getSlackIcon(),
                    keyDefaultValue,
                    null
            );
        }

        protected void errorMessage() {
            Messages.showMessageDialog(this.project, "Field required.", "Error", Messages.getErrorIcon());
        }

        protected boolean isValidField(String field) {
            return field != null && !field.isEmpty();
        }
    }

    /**
     * Remove a channel from settings
     */
    public class removeChannel extends AnAction {

        public removeChannel() {
            super("Remove Slack Channel");
        }

        @Override
        public void update(final AnActionEvent e) {
            e.getPresentation().setEnabled(SlackStorage.getInstance().getChannelsId().size() > 0);
        }

        @Override
        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);

            List<String> channelsId = SlackStorage.getInstance().getChannelsId();

            if (channelsId.size() > 0) {
                String channelToRemove = Messages.showEditableChooseDialog(
                    "Select the channel to remove",
                    SlackChannel.getSettingsDescription(),
                    SlackStorage.getSlackIcon(),
                    channelsId.toArray(new String[channelsId.size()]),
                    channelsId.get(0),
                    null
                );

                if (channelsId.contains(channelToRemove)) {
                    SlackStorage.getInstance().removeChannelByDescription(channelToRemove);
                    Messages.showMessageDialog(project, "Channel \"" + channelToRemove + "\" removed.", "Information", Messages.getInformationIcon());
                }
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

        @Override
        public void update(final AnActionEvent e) {
            e.getPresentation().setEnabled(SlackStorage.getInstance().getChannelsId().size() > 0);
        }

        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);
            // Prompt since we are killing ALL
            if (Messages.showYesNoDialog(project, "This will clear all of your channels settings", "Slack Settings", SlackStorage.getSlackIcon()) == 0) {
                SlackStorage.getInstance().clearAll();
                Messages.showMessageDialog(project, "Settings cleared.", "Information", Messages.getInformationIcon());
            }
        }
    }

}
