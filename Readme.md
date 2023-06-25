# DiscordUtils
DiscordUtils is a library that helps you create discord bots using the API wrapper [JDA](https://github.com/discord-jda/JDA).
It is split into multiple modules that you can enable and then start using. 
The basic setup looks like this:

```java
public class MyMainClass {
	public final DiscordUtils discordUtils;

	public static void main(String[] args) {
		new MyMainClass(args[0]);
	}

	public MyMainClass(String token) {
		discordUtils = new DiscordUtils(this, 
			JDABuilder.createDefault(token)
			//Further configure your JDABuilder
		);
	}
}
```
You can then start registering modules that can be used later in your code.

## Wiki
For a detailed explanation of all modules, you can take a look at the [wiki on GitHub](https://github.com/MineKingBot/DiscordUtils/wiki).