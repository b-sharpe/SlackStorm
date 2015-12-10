package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xml.XmlCoreEnvironment;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsharpe on 11/2/2015.
 */
public class SlackPost extends ActionGroup {
    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {

        // Get settings
        SlackStorage slackStorage = SlackStorage.getInstance();
        final AnAction[] children = new AnAction[slackStorage.settings.size()];

        // Create a new action for each Channel config
        int count = 0;
        for (String key : slackStorage.settings.keySet()) {
            children[count] = new SlackMessage(key);
            count++;
        }

        return children;
    }

    /**
     * Sends the message to the slack channel based on the config
     * settings.
     */
    public class SlackMessage extends AnAction {
        private static final String UTF_8 = "UTF-8";
        private static final String SLACK_ENDPOINT = "https://hooks.slack.com/services/";
        private String token;
        private String channelKey;

        public SlackMessage(String channelKey) {
            super(channelKey);
            this.channelKey = channelKey;
        }

        @Override
        public void update(final AnActionEvent e) {
            //Get required data keys
            final Project project = e.getData(CommonDataKeys.PROJECT);
            final Editor editor = e.getData(CommonDataKeys.EDITOR);

            // Get settings
            SlackStorage slackSettings = SlackStorage.getInstance();

            //Set visibility only in case of existing project and editor and if some text in the editor is selected
            e.getPresentation().setVisible((project != null && editor != null
                    && editor.getSelectionModel().hasSelection()
                    && slackSettings.settings.size() > 0));
        }

        public void actionPerformed(AnActionEvent anActionEvent) {
            //Get all the required data from data keys
            final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
            final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
            final Document document = editor.getDocument();
            final VirtualFile currentFile = FileDocumentManager.getInstance().getFile(document);
            final SelectionModel selectionModel = editor.getSelectionModel();

            String selectedText = selectionModel.getSelectedText();
            if (selectedText == null) {
                return;
            }

            // Get details
            String fileName = currentFile.getName();
            CaretState selectionInfo = editor.getCaretModel().getCaretsAndSelections().get(0);
            int selectionStart = selectionInfo.getSelectionStart().line + 1;
            int selectionEnd = selectionInfo.getSelectionEnd().line + 1;
            String fileDetails = "_File: " + fileName + ", Line(s): " + selectionStart + "-" + selectionEnd + "_";

            this.pushMessage(selectedText, fileDetails, anActionEvent);
            selectionModel.removeSelection();
        }

        private void pushMessage(String message, String details, final AnActionEvent actionEvent) {
            final Project project = actionEvent.getRequiredData(CommonDataKeys.PROJECT);
            CloseableHttpClient httpclient = HttpClients.createDefault();

            // Reload our settings
            SlackStorage slackSettings = SlackStorage.getInstance();
            this.token = slackSettings.settings.get(this.channelKey);
            String alias = slackSettings.aliases.get(this.channelKey);

            // Fallback from previous versions
            if (alias == null || alias.isEmpty()) {
                alias = "SlackStorm";
            }

            HttpPost httppost = new HttpPost(SLACK_ENDPOINT + this.token);

            // Simple escape @todo: check against slack input options
            message = message.replace("\"", "\\\"");

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
            params.add(new BasicNameValuePair("payload", "{" +
                    "\"text\" : \"" + details + " ```" + message + "```\"," +
                    "\"username\" : \"" + alias + "\"," +
                    "\"icon_emoji\" : \":thunder_cloud_and_rain:\"" +
                    "}"));
            try {
                httppost.setEntity(new UrlEncodedFormEntity(params, UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //Execute and get the response.
            HttpResponse response = null;
            try {
                response = httpclient.execute(httppost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                Messages.showMessageDialog(project, "Message Sent.", "Information", IconLoader.getIcon("/icons/slack.png"));
            }
            else {
                Messages.showMessageDialog(project, "Error Occurred.", "Error", Messages.getErrorIcon());
            }
        }

    }

}
