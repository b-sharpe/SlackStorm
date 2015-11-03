package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;

/**
 * Created by bsharpe on 11/2/2015.
 */
public class SlackSettings extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        String token = Messages.showInputDialog(project, "Enter your slack webhook integration path (i.e. <xxx>/<yyy>/<zzz>.", "Slack Settings", IconLoader.getIcon("/icons/slack.png"));

        SlackStorage settings = SlackStorage.getInstance();

        if (token != null) {
            settings.token = token;
            Messages.showMessageDialog(project, "Reload project for changes to take effect.", "Information", Messages.getInformationIcon());
        }

    }
}
