package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import org.jdesktop.swingx.action.ActionManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * Created by bsharpe on 11/2/2015.
 */
public class SlackSettings extends ActionGroup {
    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {
        final AnAction[] children=new AnAction[2];

        children[0] = new addChannel();
        children[1] = new removeChannels();

        return children;
    }

    public class addChannel extends AnAction {
        public addChannel() {
            super("Add Slack Channel");
        }
        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);

            String description = Messages.showInputDialog(project, "Enter a Description", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));
            if (description != null) {
                String token = Messages.showInputDialog(project, "Enter your slack webhook integration path (i.e. <xxx>/<yyy>/<zzz>.", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));

                if (token != "" && token != null) {
                    SlackStorage slackStorage = SlackStorage.getInstance();
                    slackStorage.settings.put(description, token);

                    // Debug
                    for (String key : slackStorage.settings.keySet()) {
                        System.out.println(key);
                    }

                    Messages.showMessageDialog(project, "Settings Saved.", "Information", Messages.getInformationIcon());
                }
            }
        }
    }

    public class removeChannels extends AnAction {
        public removeChannels() {
            super("Reset Slack Channels");
        }
        public void actionPerformed(AnActionEvent e) {
            final Project project = e.getData(CommonDataKeys.PROJECT);

            int confirm = Messages.showYesNoDialog(project, "This will clear all of your channel settings", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));
            if (confirm == 0) {
                SlackStorage slackStorage = SlackStorage.getInstance();
                slackStorage.settings.clear();
                Messages.showMessageDialog(project, "Settings Cleared.", "Information", Messages.getInformationIcon());
            }
        }
    }

}
