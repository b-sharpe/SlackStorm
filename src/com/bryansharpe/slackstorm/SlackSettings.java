package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * Created by bsharpe on 11/2/2015.
 * Updated by Anael Chardan "anael.chardan@gmail.com"
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

            if (!isValidField(description))
            {
                errorMessage();
                return;
            }

            String userAlias = this.showInputDialog(SlackChannel.getSenderNameDescription(), SlackChannel.getSenderNameDefaultValue());

            if (!isValidField(userAlias))
            {
                errorMessage();
                return;
            }

            String icon = this.showInputDialog(SlackChannel.getSenderIconDescription(), SlackChannel.getDefaultSenderIcon());

            if (!isValidField(icon))
            {
                errorMessage();
                return;
            }

            String token = this.showInputDialog(SlackChannel.getTokenDescription(), null);

            if (!isValidField(token))
            {
                errorMessage();
                return;
            }

            //Here all is good, we can create the channel
            SlackStorage.getInstance().registerChannel(new SlackChannel(token, description, userAlias, icon));
            Messages.showMessageDialog(project, "Settings Saved.", "Information", Messages.getInformationIcon());
        }

        protected String showInputDialog(String keyDescription, String keyDefaultValue)
        {
            return Messages.showInputDialog(
                    this.project,
                    keyDescription,
                    SlackChannel.getSettingsDescription(),
                    SlackStorage.getIcon(),
                    keyDefaultValue,
                    null
            );
        }

        protected void errorMessage()
        {
            Messages.showMessageDialog(project, "Field required.", "Error", Messages.getErrorIcon());
        }

        protected boolean isValidField(String field)
        {
            return field != null && !field.isEmpty();
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
            if (Messages.showYesNoDialog(project, "This will clear all of your channels settings", "Slack Settings", SlackStorage.getIcon()) == 0) {
                SlackStorage.getInstance().clearAll();
                Messages.showMessageDialog(project, "Settings Cleared.", "Information", Messages.getInformationIcon());
            }
        }
    }

}
