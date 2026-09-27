package top.anthonycat.complexirc.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sun.jna.platform.unix.X11;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.impl.client.keymapping.KeyMappingRegistryImpl;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.Identifier;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.spongepowered.asm.mixin.Unique;

import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.listener;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComplexircClient implements ClientModInitializer {

	public static top.anthonycat.complexirc.client.Configuration CONFIG = null;

	public KeyMapping mcirc = new KeyMapping("text.key.complexirc.toggle", InputConstants.Type.KEYSYM, InputConstants.KEY_MINUS, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("complexirc", "keybinds")));

//	public static List<GuiMessage> mcmsg = new ArrayList<>();
//	public static List<GuiMessage> ircmsg = new ArrayList<>();
//	public static List<GuiMessage> globalmsg = new ArrayList<>();

	public static String prefferedChannel = null;

	public static Boolean ircchatenabled = true;
	public static Boolean mcchatenabled = true;

	public static Configuration config;
	public static Thread ircthread;
	public static PircBotX bot;
	public static Audience audience = MinecraftClientAudiences.of().audience();
	public static MiniMessage mm = MiniMessage.miniMessage();
	public static Boolean talkinirc = false;
	public static channel currentchannel = channel.global;

	public static boolean xshownextmsg = false;

	public static boolean cat = false;
			 //Create an immutable configuration from this builder

	@Override
	public void onInitializeClient() {

		AutoConfig.register(top.anthonycat.complexirc.client.Configuration.class, Toml4jConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(top.anthonycat.complexirc.client.Configuration.class).getConfig();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		//setupirc();
		KeyMappingHelper.registerKeyMapping(mcirc);


		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (mcirc.consumeClick()) {
				if (bot==null||!bot.isConnected()){
					util.msg("<yellow>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
					setupirc();
				}
				talkinirc = !talkinirc;
				if (talkinirc) {
					util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<red>irc " + "<gray><lang:text.chat.complexirc.chat>");
				} else {
					util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<blue>minecraft " + "<gray><lang:text.chat.complexirc.chat>");
				}
			}
		});

		ClientLifecycleEvents.CLIENT_STOPPING.register((e) -> {
			if (bot!=null&&bot.isConnected()&&ircthread!=null&&ircthread.isAlive()) {
				bot.sendIRC().quitServer("closing game.");

            try {ircthread.join(1010);} catch (InterruptedException ex) {}
				ircthread.interrupt();
         }
		});

		//		HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,Identifier.fromNamespaceAndPath("complexirc","hud"),(graphics, deltaTracker) -> {
		//			if (Minecraft.getInstance().getscree)
		//			graphics.text(Minecraft.getInstance().font,"you are in irc",5,Minecraft.getInstance().getWindow().getGuiScaledHeight()-30,green);
		//		});

		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, buildContext) -> {
			dispatcher.register(ClientCommands.literal("irc")
				.then(
					ClientCommands.literal("connect")
						.executes((context) -> {
							if (bot!=null&&bot.isConnected()){
								util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.already_connected>");
								return 1;
							}
							setupirc();

							return 1;
						})
				).then(
					ClientCommands.literal("raw")
						.then(
							ClientCommands.argument("raw",StringArgumentType.greedyString())
								.executes((c) -> {
									if (bot==null||!bot.isConnected()){
										util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
										setupirc();
									}
									String raw = StringArgumentType.getString(c,"raw");
									bot.sendRaw().rawLine(raw);
									util.msg("<green>IRC | <gray>Sent raw message to IRC server");
									return 1;
								})
					)
				).then(
					ClientCommands.literal("disconnect")
						.executes((c) ->{
							if (bot==null||!bot.isConnected()){
								util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
								return 1;
							}
							bot.stopBotReconnect();
							bot.sendIRC().quitServer("Disconnected via command.");
							
							util.msg("<red>IRC | <lang:text.chat.complexirc.on_disconnect>");
							talkinirc = false;
							return 1;
						})
				).then(
					ClientCommands.literal("openconfig")
						.executes((c) ->{
							util.msg("<blue>IRC | Opening config screen...");

							Minecraft.getInstance().execute(() -> {
								Screen s = AutoConfigClient.getConfigScreen(top.anthonycat.complexirc.client.Configuration.class, Minecraft.getInstance().screen).get();
								//	util.msg("aughscreen: "+s.toString());
								Minecraft.getInstance().setScreenAndShow(s);

							});

							return 1;
						})
				).then(
					ClientCommands.literal("list")
						.executes((c) ->{
							if (bot==null||!bot.isConnected()){
								util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
								return 1;
							}
							StringBuilder names = new StringBuilder("users in ");
							names.append(CONFIG.serverConfig.postjoinchannel).append(": ");

							names.append(String.join(", ", bot.getUserChannelDao().getChannel(CONFIG.serverConfig.postjoinchannel).getUsersNicks()));

							util.msg("<blue>IRC | " + names);

							return 1;
						})
				).then(
					ClientCommands.literal("reconnect")
						.executes((c) ->{
							if (bot==null||!bot.isConnected()){
								util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
								return 1;
							}
							setupirc();
							bot.sendIRC().joinChannel(CONFIG.serverConfig.postjoinchannel);
							return 1;
						})
				)

				// ## Channel command is disabled until i can properly implement channel creation and password stuff. Also we might need to add moderation tools before.
				// .then(
				// 	ClientCommands.literal("channel")
				// 		.then(ClientCommands.argument("channel",StringArgumentType.word()).executes((c) ->{
				// 			String channel = StringArgumentType.getString(c,"channel");
				// 			if (!channel.startsWith("#")) channel = "#" + channel;
				// 			Complexirc.LOGGER.info("Channel command trigger, channel list:" + listener.cList);
				// 			for (int i = 0; i < listener.cList.size(); i++) {
				// 				if (listener.cList.get(i).equals(channel)) {
				// 					Complexirc.LOGGER.info(listener.cList.get(i));
				// 					if (bot==null||!bot.isConnected()) {
				// 						util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
				// 						prefferedChannel = channel;
										
				// 						setupirc();
				// 						bot.sendIRC().joinChannel(channel);
				// 						return 1;
				// 					}
				// 					bot.sendIRC().joinChannel(channel);
				// 					util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.joining_channel> <green>" + channel + "<gray>...");
				// 					return 1;
				// 					}
				// 				}
				// 			util.msg("<red> | <gray><lang:text.chat.complexirc.incorrect_channel>");
				// 			return 1;
				// 		}))
				// )
			);


			dispatcher.register(
				ClientCommands.literal("dm")
					.then(ClientCommands.argument("nick",StringArgumentType.word()).suggests(((commandContext, suggestionsBuilder) -> {
						if (bot==null||!bot.isConnected()){
							suggestionsBuilder.suggest("connect to irc first");
							return suggestionsBuilder.buildFuture();
						}
						bot.getUserChannelDao().getChannel(CONFIG.serverConfig.postjoinchannel).getUsersNicks().forEach(suggestionsBuilder::suggest);

						return suggestionsBuilder.buildFuture();
					})).then(
						ClientCommands.argument("msg",StringArgumentType.greedyString()).executes((commandContext -> {
							String nick = StringArgumentType.getString(commandContext,"nick");
							String msg = StringArgumentType.getString(commandContext,"msg");
							if (bot==null||!bot.isConnected()){
								util.msg("<blue>IRC | You're not connected to a IRC server. Use /irc connect to connect to the server.");
								return 1;
							}
							bot.getUserChannelDao().getUser(nick).send().message(msg);
							util.msg("<blue>IRC | DM to %s: %s".formatted(nick,msg));

							return 1;
						}))
					))
			);
		}));


	}

	public static void setupirc(){
		//CONFIG.load();
		if ((ircthread != null) && (bot != null) && bot.isConnected()){
			bot.sendIRC().quitServer("reconnecting");

			try {
				ircthread.join(1000);
				audience.sendMessage(ComplexircClient.mm.deserialize("<yellow>IRC | <gray><lang:text.chat.complexirc.reconnecting>"));
				ircthread.interrupt();
			} catch (InterruptedException ignored) {}

		}

		audience.sendMessage(ComplexircClient.mm.deserialize("<yellow>IRC | <gray><lang:text.chat.complexirc.connecting>"));

		StringBuilder requestedChannel = new StringBuilder();

		prefferedChannel = prefferedChannel != null ? prefferedChannel : CONFIG.serverConfig.postjoinchannel;

		requestedChannel.append(prefferedChannel);
		//If channel has a password, append it to the channel name with a space in between
		if (CONFIG.serverConfig.channelpass!=""){
			requestedChannel.append(" ").append(CONFIG.serverConfig.channelpass);
		}
		//sax pls stop capitalizing everything
		config = new Configuration.Builder()
              	.setName(CONFIG.serverConfig.username) //Username
              	.setLogin(CONFIG.serverConfig.username) //Login part of hostmask, eg name:login@host
				  	.setRealName(Minecraft.getInstance().getUser().getName() + " (ComplexIRC)") //note: sax wanted to use player.getName() but that would result in null
				  	.setAutoReconnect(CONFIG.serverConfig.autoreconnect) 
				  	.setAutoReconnectAttempts(5)
              	.setAutoNickChange(true) //automatically change nick when the current one is in use
              	.addAutoJoinChannel(requestedChannel.toString())//join the channel on connect, with password if provided
              	.addListener(new listener()) 
				  	.addServer(CONFIG.serverConfig.serverip, CONFIG.serverConfig.port) 
				.buildConfiguration();
		ircthread = new Thread(() -> {
				bot = new PircBotX(config);
         	try {
            	bot.startBot();
         	} catch (IOException | IrcException e) {
				Complexirc.LOGGER.info("Error connecting to IRC server: "+ e.getMessage());
         	}
      },"ircthread");
			ircthread.start();
			Complexirc.LOGGER.info("Starting irc thread...");


	}

	//ts is unused btw
	public enum channel{
		normal,
		irc,
		global
	}
	// ???????????????
    public class CONFIG {
    }
}
