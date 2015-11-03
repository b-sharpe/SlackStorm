package com.bryansharpe.slackstorm;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsharpe on 11/2/2015.
 */
public class SlackPost extends AnAction {
    private static final String UTF_8 = "UTF-8";
    private String token;

    public SlackPost() {
        SlackStorage settings = SlackStorage.getInstance();
        token = settings.token;
    }

    @Override
    public void update(final AnActionEvent e) {
        //Get required data keys
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        //Set visibility only in case of existing project and editor and if some text in the editor is selected
        e.getPresentation().setVisible((project != null && editor != null
                && editor.getSelectionModel().hasSelection()
                && token != null));
    }

    public void actionPerformed(AnActionEvent anActionEvent) {
        //Get all the required data from data keys
        final Editor editor = anActionEvent.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = anActionEvent.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null) {
            return;
        }

        this.pushMessage(selectedText, anActionEvent);
        selectionModel.removeSelection();
    }

    private void pushMessage(String message, final AnActionEvent actionEvent) {
        final Project project = actionEvent.getRequiredData(CommonDataKeys.PROJECT);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://hooks.slack.com/services/" + this.token);

        message = message.replace("\"", "\\\"");

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add(new BasicNameValuePair("payload", "{\"text\" : \"```" + message + "```\"}"));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Execute and get the response.
        HttpResponse response = null;
        //noinspection MagicConstant
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
