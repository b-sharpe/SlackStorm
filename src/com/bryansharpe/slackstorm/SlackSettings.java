package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;

import java.util.HashSet;

/**
 * Created by bsharpe on 11/2/2015.
 */
public class SlackSettings extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);

        String description = Messages.showInputDialog(project, "Enter a Description", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));
        if (description != null) {
            String token = Messages.showInputDialog(project, "Enter your slack webhook integration path (i.e. <xxx>/<yyy>/<zzz>.", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));

            if (token != null) {
                SlackStorage slackStorage = SlackStorage.getInstance();
                slackStorage.settings.clear();
                slackStorage.settings.put(description, token);

                for ( String key : slackStorage.settings.keySet() ) {
                    System.out.println( key );
                }

                Messages.showMessageDialog(project, "Settings Saved.", "Information", Messages.getInformationIcon());
            }
        }
    }
}
