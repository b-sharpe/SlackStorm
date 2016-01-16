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
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bsharpe on 11/2/2015.
 * Updated by Anael CHARDAN "anael.chardan@gmail.com" on 01/16/2016
 */
public class SlackPost extends ActionGroup {

    @NotNull
    @Override
    public AnAction[] getChildren(AnActionEvent anActionEvent) {

        // Get settings
        SlackStorage slackStorage = SlackStorage.getInstance();
        AnAction[] children = new AnAction[slackStorage.channelsRegistry.size()];

        return slackStorage.channelsRegistry.stream().map(SlackMessage::new).collect(Collectors.toList()).toArray(children);
    }

    /**
     * Sends the message to the slack channel based on the config
     * settings.
     */
    public class SlackMessage extends AnAction {
        private SlackStorage storage = SlackStorage.getInstance();

        private Project project;
        private Editor editor;
        private String currentFileName;

        private String selectedText;

        private SlackChannel channel;

        public SlackMessage(SlackChannel channel) {
            super(channel.getId());
            this.channel = channel;
        }

        @Override
        public void update(final AnActionEvent e) {
            actionEventObserved(e);

            //Set visibility only in case of existing project and editor and if some text in the editor is selected
            e.getPresentation().setVisible((project != null && editor != null && selectedText != null && storage.settings.size() > 0));
        }

        public void actionPerformed(AnActionEvent anActionEvent) {
            actionEventObserved(anActionEvent);

            if (this.selectedText == null)
            {
                return;
            }

            try {
                this.pushMessage(selectedText, this.buildFileDetails());
            } catch (IOException e) {
                e.printStackTrace();
            }
            editor.getSelectionModel().removeSelection();
        }


        protected void actionEventObserved(AnActionEvent event)
        {
            this.project = event.getData(CommonDataKeys.PROJECT);
            this.editor = event.getData(CommonDataKeys.EDITOR);

            if (editor == null)
            {
                currentFileName = null;
                selectedText = null;
                return;
            }

            final VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

            if (currentFile != null)
            {
                this.currentFileName = currentFile.getName();
                this.selectedText = editor.getSelectionModel().hasSelection() ? editor.getSelectionModel().getSelectedText() : null;
                return;
            }

            this.selectedText = null;
        }

        protected String buildFileDetails()
        {
            CaretState selectionInfo = editor.getCaretModel().getCaretsAndSelections().get(0);
            int selectionStart = selectionInfo.getSelectionStart().line + 1;
            int selectionEnd = selectionInfo.getSelectionEnd().line + 1;
            return "File: " + currentFileName + ", Line(s): " + selectionStart + "-" + selectionEnd;
        }

        private void pushMessage(String message, String details) throws IOException {
            String input = "payload=" + URLEncoder.encode(channel.getPayloadMessage(details, message), "UTF-8");

            try {
                URL url = new URL(channel.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
                wr.writeBytes(input);
                wr.flush ();
                wr.close ();

                if (conn.getResponseCode() == 200) {
                    Messages.showMessageDialog(project, "Message Sent.", "Information", SlackStorage.getIcon());
                    return;
                }

                Messages.showMessageDialog(project, "Error Occurred.", "Error", Messages.getErrorIcon());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
