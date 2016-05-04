package com.bryansharpe.slackstorm;

/**
 * Created by Anael CHARDAN "anael.chardan@gmail.com" on 01/16/2016
 */
public class SlackChannel {
    protected String id;
    protected String token;
    protected String senderName = "SlackStorm";
    protected String senderIcon = ":thunder_cloud_and_rain:";

    public SlackChannel(String token, String id, String senderName, String senderIcon) {
        this.token = token;
        this.id = id;
        this.senderName = senderName;
        this.senderIcon = senderIcon;
    }

    public String getPayloadMessage(String title, String message) {
        message = message.replace("\\", "\\\\").replace("\"", "\\\"");

        return "{" +
                    "\"attachments\" : [{" +
                        "\"title\" : \"" + title + "\"," +
                        "\"text\" : \"```" + message + "```\"," +
                        "\"mrkdwn_in\" : [\"title\", \"text\"]" +
                    "}]," +
                    "\"username\" : \"" + this.getSenderName() + "\"," +
                    "\"icon_emoji\" : \""+ this.getSenderIcon() +"\"" +
                "}";
    }

    public String getId() {
        return id;
    }

    public static String getIdDescription() {
        return "Enter a Description";
    }

    public String getToken() {
        return token;
    }

    public static String getTokenDescription() {
        return "Enter your slack webhook integration path (i.e. <xxx>/<yyy>/<zzz>.";
    }

    public String getSenderName() {
        return senderName == null ? getSenderNameDefaultValue() : senderName;
    }

    public static String getSenderNameDescription() {
        return "Username to post as:";
    }

    public static String getSenderNameDefaultValue() {
        return "SlackStorm";
    }

    public String getSenderIcon() {
        return senderIcon == null ? getDefaultSenderIcon() : senderIcon ;
    }

    public static String getSenderIconDescription() {
        return "Icon used to post:";
    }

    public static String getDefaultSenderIcon() {
        return ":thunder_cloud_and_rain:";
    }

    public static String getSettingsDescription() {
        return "Slack Channel Settings";
    }

    public String getUrl() {
        return "https://hooks.slack.com/services/" + this.token;
    }
}
