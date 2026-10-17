package top.anthonycat.complexirc.client;

import com.frikinjay.dungeonconfig.api.DungeonConfig;
import com.frikinjay.dungeonconfig.client.screen.DungeonConfigScreen;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.sun.jna.platform.unix.X11;

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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComplexircClient implements ClientModInitializer {


	public KeyMapping mcirc = new KeyMapping("text.key.complexirc.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_MINUS, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("complexirc", "keybinds")));

//	public static List<GuiMessage> mcmsg = new ArrayList<>();
//	public static List<GuiMessage> ircmsg = new ArrayList<>();
//	public static List<GuiMessage> globalmsg = new ArrayList<>();

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

	//	CONFIG = top.anthonycat.complexirc.client.Configuration.CONFIG;


	//	System.out.println("test value in complexirc konfig: "+ConfigV2.test.get());

	//	AutoConfig.register(top.anthonycat.complexirc.client.Configuration.class, Toml4jConfigSerializer::new);
	//	CONFIG = AutoConfig.getConfigHolder(top.anthonycat.complexirc.client.Configuration.class).getConfig();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		//setupirc();
		KeyMappingHelper.registerKeyMapping(mcirc);

		top.anthonycat.complexirc.client.Configuration.init();


		ClientTickEvents.END_CLIENT_TICK.register(client -> {

			//util.msg("debug username: "+ConfigV2.server.username.get());



			while (mcirc.consumeClick()) {
				if (bot==null||!bot.isConnected()){
					util.msg("<yellow>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
					setupirc();
				}
				talkinirc = !talkinirc;
				if (talkinirc) {
					util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<red>irc " + "<lang:text.chat.complexirc.chat>");
				} else {
					util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<blue>minecraft " + "<lang:text.chat.complexirc.chat>");
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
							ClientCommands.literal("forcedisconnect")
									.executes((c) ->{
										if (bot==null||!bot.isConnected()){
											util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
											return 1;
										}
										bot.stopBotReconnect();
										ircthread.interrupt();
										util.msg("<red>IRC | force disconnected (i didnt test this btw)");
										talkinirc = false;
										return 1;
									})
					).then(
					ClientCommands.literal("openconfig")
						.executes((c) ->{
							util.msg("<blue>IRC | opening config screen...");

							Minecraft.getInstance().execute(() -> {
								//Screen s = AutoConfigClient.getConfigScreen(top.anthonycat.complexirc.client.Configuration.class, Minecraft.getInstance().gui.screen()).get();
								//	util.msg("aughscreen: "+s.toString());
								Minecraft.getInstance().setScreenAndShow(new DungeonConfigScreen(Minecraft.getInstance().gui.screen(), top.anthonycat.complexirc.client.Configuration.CONFIG));
								//Minecraft.getInstance().setScreenAndShow(s);

							});

							return 1;
						})
				).then(
					ClientCommands.literal("list")
						.executes((c) ->{
						//	ConfigV2.test.set("uwu");
							//ConfigV2.CONFIG.save();
							if (bot==null||!bot.isConnected()){
								util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.not_connected>");
								return 1;
							}
							StringBuilder names = new StringBuilder("users in ");
							names.append(top.anthonycat.complexirc.client.Configuration.postjoinchannel.get()).append(": ");

							names.append(String.join(", ", bot.getUserChannelDao().getChannel(top.anthonycat.complexirc.client.Configuration.postjoinchannel.get()	).getUsersNicks()));

							util.msg("<blue>IRC | "+names);

							return 1;
						})
				)
			);







			dispatcher.register(
				ClientCommands.literal("dm")
					.then(ClientCommands.argument("nick",StringArgumentType.word()).suggests(((commandContext, suggestionsBuilder) -> {
						if (bot==null||!bot.isConnected()){
							suggestionsBuilder.suggest("connect to irc first");
							return suggestionsBuilder.buildFuture();
						}
						bot.getUserChannelDao().getChannel(top.anthonycat.complexirc.client.Configuration.postjoinchannel.get()).getUsersNicks().forEach(suggestionsBuilder::suggest);

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
			bot.sendIRC().quitServer("Attempting to reconnect.");

			try {
				ircthread.join(1000);
				audience.sendMessage(ComplexircClient.mm.deserialize("<yellow>IRC | <gray><lang:text.chat.complexirc.reconnecting>"));
				ircthread.interrupt();
			} catch (InterruptedException ignored) {}

		}

		audience.sendMessage(ComplexircClient.mm.deserialize("<yellow>IRC | <gray><lang:text.chat.complexirc.connecting>"));

//		StringBuilder channelwithpass = new StringBuilder();
//
//		//apparently hashtags dont work in dungeonconfig
//		channelwithpass.append(top.anthonycat.complexirc.client.Configuration.postjoinchannel);
//		if (top.anthonycat.complexirc.client.Configuration.channelpass.get()!=""){
//			channelwithpass.append(" ").append(top.anthonycat.complexirc.client.Configuration.channelpass.get());
//		}

		config = new Configuration.Builder()
              .setName(top.anthonycat.complexirc.client.Configuration.username.get()) //Username
              .setLogin(top.anthonycat.complexirc.client.Configuration.username.get()) //Login part of hostmask, eg name:login@host
				  .setRealName(Minecraft.getInstance().getUser().getName()+" (ComplexIRC)") //Real name part of hostmask, eg name:login@host
				  .setAutoReconnect(top.anthonycat.complexirc.client.Configuration.autoreconnect.get())
				  .setAutoReconnectAttempts(5)
              .setAutoNickChange(true) //Automatically change nick when the current one is in use
            //  .addAutoJoinChannel(channelwithpass.toString())//Join the channel on connect, with password if provided
              .addListener(new listener()) 
				  .addServer(top.anthonycat.complexirc.client.Configuration.serverip.get(), top.anthonycat.complexirc.client.Configuration.port.get())

				.buildConfiguration();
		ircthread = new Thread(() -> {
				bot = new PircBotX(config);
         	try {
            	bot.startBot();
         	} catch (IOException | IrcException e) {
				Complexirc.LOGGER.info("*kaboom* irc server connection failed: "+ e.getMessage());
         	}
      },"ircthread");
			ircthread.start();
			Complexirc.LOGGER.info("irc thread starting");


	}

	public enum channel{
		normal,
		irc,
		global
	}

}