# SlackStorm
PHP Storm (IntelliJ) plugin that allows posting code snippets to a Slack channel. Sharing code snippets to colleagues over IM is a pain, so I made it slightly less painful. 

Simple: Hightlight some text, hit 'Send to Slack', rejoice

# Install the plugin

### Method 1:
- File->Settings in your IDE
- Open 'Plugins' and hit 'Browse Repositories'
- Search for 'Slack Storm'
- Follow directions

### Method 2:
- Get the .jar 
- Install the SlackStorm plugin via "Install plugin from disk..." in preferences
- Restart

# Add new Slack channel
- Create a incoming webhook integration for your channel
- Copy the webhook path (the part after https://hooks.slack.com/services/)
- In IntelliJ : Tools > Slack Settings > Add Slack Channel

