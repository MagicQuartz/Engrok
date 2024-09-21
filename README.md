![Engrok Thumbnail](https://cdn.modrinth.com/data/cached_images/05c01e0a7a1de72593e5a47ee8129ae467fc9831.png)

<center>
  <h2>Engrok</h2>
   <a href="https://modrinth.com/mod/cloth-config"><img src="https://wsrv.nl/?url=https://cdn.modrinth.com/data/cached_images/c45c8cf9b2c779559cd5befea3e10be30d21ff0a.png&amp;n=-1" alt="Requires Cloth Config" width="200"></a>
  <a href="https://modrinth.com/mod/fabric-api"><img src="https://wsrv.nl/?url=https://cdn.modrinth.com/data/cached_images/8bd6d7ff822dc33ffb55af6cc0c7062cec53e9e3.png&amp;n=-1" alt="Requires Fabric API" width="200"></a>
</center>
<br>
Engrok is a server-side mod for dedicated servers which opens an Ngrok tunnel automatically once the server starts. There's an integration for GitHub Gists which uploads the Ngrok tunnel IP to a GitHub Gist (an existing one or a new one, depending on the mod's config - if there isn't an existing one, a new one will be created and saved to the config file automatically) so users can either retreive the IP from the Gist directly, or, using the mod Ever Changing on their clients to automatically retrieve it for them.

# **How to use**
For the mod to create a config file you can modify you need to run the server with the mod loaded first. Then make sure to stop the server, then head to the folder "config" and open the file "engrok.json5" with a text editor.
The config'll probably look like this at the beginning:

```
{
	"enabled": true,
	"ngrokAuthToken": "",
	"regionSelect": "AUTO",
	"gitHubAuthToken": "",
	"gistId": ""
}
```

# **Config File**
- _Enabled_: Self explanatory. Controls the mod.
- _Ngrok Auth Token_: If you want the mod to do automatically open an Ngrok tunnel, you'll have to provide it your Ngrok Authentication Token, which you can get from the [Ngrok Website Dashboard](https://dashboard.ngrok.com/get-started/your-authtoken). You need an Ngrok account in order to acquire an auth token.
- _Region Select_: Ngrok is capable of automatically selecting the tunnel location with the least amount of latency relative to you, so you should probably leave it at its default value. Otherwise, you can choose from [this list](https://ngrok.com/docs/network-edge/#points-of-presence).
- _GitHub Auth Token_: In case you want to automatically update the tunnel's address  for your friends, I have made a GitHub integration which either makes a new Gist and saves its ID in the next field, or edits the existing gist in the following field. To get a GitHub Authentication Token, you'll need to make sure you're logged into GitHub, then press your profile picture on the top right, head into `Settings » Developer Settings » Personal access tokens » Tokens (classic)` and press `Generate new token`, then `Generate new token (classic)`. Then set `Expiration` to `No expiration` (or shorter, if you'd like), and under the `Select scopes` list make sure `gist` is ticked/enabled, then press `Generate Token`. Make sure to copy the token, because you'll need to regenerate a new one if you don't, then copy it into the config file.
- _Gist ID_: If you have already made a gist, you can change this ID to the gist's ID to have the mod edit it everytime the tunnel is opened (How to find a gist ID: Let's take this gist link for example [https://gist.github.com/MagicQuartz/3bef909cddbfbb117f0bcf101222a5dc](https://gist.github.com/MagicQuartz/3bef909cddbfbb117f0bcf101222a5dc). You can copy the part after the last slash to have your ID, in this case it's 3bef909cddbfbb117f0bcf101222a5dc).

# **In-game Commands**
**/engrok**<br>
This command essentially manipulates the config file. Leaving any of these commands without their parameters will empty the config from them and leave a blank "".
-   `/engrok setNgrokAuth <token>` - Sets the Ngrok Authentication Token in the config file.
-   `/engrok setGitHubAuth <token>` - Sets the GitHub Authentication Token in the config file.
-   `/engrok setGistId <id>` - Sets the Gist ID in the config file.

**/tunnel**<br>
This command controls the Ngrok tunnel.
-   `/tunnel open` - Opens the tunnel if it's closed
-   `/tunnel close` - Closes the tunnel if it's open.
-   `/tunnel status` - Tells you wether the tunnel is open or close and its IP address if it's open.

**/gist**<br>
Displays data relating to the gist.
-   `/gist getIp` - Provides you with the IP of the tunnel as shown in the gist online, to make sure it is updated to be like the actual tunnel address you can see using `/tunnel status`.
-   `/gist getUrl` - Provides you with the url of the gist, which you can share with friends to use with the Ever Changing mod (More on that soon) to automatically receive the IP every time it changes.

This mod is inspired by [Ngrok LAN](https://modrinth.com/mod/ngrok-lan/) by [Alujjdnd](https://modrinth.com/user/Alujjdnd) and a big chunk of this mod's code has been taken from Ngrok LAN.
Please keep in mind this mod was made in about 12 hours so it may not work perfectly, but I did my best to patch everything, so please do not try to intentionally break things.
