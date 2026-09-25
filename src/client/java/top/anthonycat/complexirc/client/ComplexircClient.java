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

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComplexircClient implements ClientModInitializer {

	public static top.anthonycat.complexirc.client.Configuration CONFIG = null;

	private final int green = new Color(0,255,0).getRGB();

	public KeyMapping mcirc = new KeyMapping("switch", InputConstants.Type.KEYBOARD, InputConstants.KEY_MINUS,KeyMapping.Category.register(Identifier.fromNamespaceAndPath("complexirc","keybinds")));


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

	public static boolean cat = false;
			 //Create an immutable configuration from this builder


	@Override
	public void onInitializeClient() {
		AutoConfig.register(top.anthonycat.complexirc.client.Configuration.class, Toml4jConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(top.anthonycat.complexirc.client.Configuration.class).getConfig();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		//setupirc();
		KeyMappingHelper.registerKeyMapping(mcirc);


		ClientTickEvents.END_CLIENT_TICK.register(clien -> {

			while (mcirc.consumeClick()) {
				talkinirc = !talkinirc;
				if (talkinirc) {
					util.msg("<blue>IRC | you are now talking in irc");
				}else{
					util.msg("<red>IRC | you are now talking in minecraft");
				}

			}

		});

		ClientLifecycleEvents.CLIENT_STOPPING.register((e) -> {
			if (bot!=null&&bot.isConnected()&&ircthread!=null&&ircthread.isAlive()) {
				bot.sendIRC().quitServer("exiting game");

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
										audience.sendMessage(mm.deserialize("<blue>attempting to connect to irc.."));
										setupirc();

										return 1;
									})
					).then(
							ClientCommands.literal("raw")
									.then(
											ClientCommands.argument("raw",StringArgumentType.greedyString())
													.executes((c) -> {
														String raw = StringArgumentType.getString(c,"raw");
														bot.sendRaw().rawLine(raw);
														util.msg("<blue>sent raw message to irc server");


														return 1;
													})
									)
					).then(
							ClientCommands.literal("disconnect")
									.executes((c) ->{
										bot.stopBotReconnect();
										bot.sendIRC().quitServer("disconnected via /irc disconnect");
										util.msg("<blue>disconnecting from irc");
										talkinirc = false;
										return 1;
									})
					).then(
							ClientCommands.literal("openconfig")
									.executes((c) ->{
										util.msg("<blue>probably™ opened config");

										Minecraft.getInstance().execute(() -> {
											Screen s = AutoConfigClient.getConfigScreen(top.anthonycat.complexirc.client.Configuration.class, Minecraft.getInstance().gui.screen()).get();
										//	util.msg("aughscreen: "+s.toString());
											Minecraft.getInstance().setScreenAndShow(s);

										});

										return 1;
									}))
					.then(
							ClientCommands.literal("list")
									.executes((c) ->{
										StringBuilder names = new StringBuilder("users in ");
										names.append(CONFIG.serverstuff.postjoinchannel).append(": ");

										names.append(String.join(", ", bot.getUserChannelDao().getChannel(CONFIG.serverstuff.postjoinchannel).getUsersNicks()));

										util.msg("<blue>IRC | "+names);

										return 1;
									}))







			);


			dispatcher.register(
							ClientCommands.literal("dm")
									.then(ClientCommands.argument("nick",StringArgumentType.word()).suggests(((commandContext, suggestionsBuilder) -> {
										if (bot==null||!bot.isConnected()){
											suggestionsBuilder.suggest("connect to irc first");
											return suggestionsBuilder.buildFuture();
										}
										bot.getUserChannelDao().getChannel(CONFIG.serverstuff.postjoinchannel).getUsersNicks().forEach(suggestionsBuilder::suggest);

										return suggestionsBuilder.buildFuture();
									})).then(
											ClientCommands.argument("msg",StringArgumentType.greedyString()).executes((commandContext -> {
												String nick = StringArgumentType.getString(commandContext,"nick");
												String msg = StringArgumentType.getString(commandContext,"msg");
												if (bot==null||!bot.isConnected()){
													util.msg("<blue>you're not connected to irc");
													return 1;
												}
												bot.getUserChannelDao().getUser(nick).send().message(msg);
												util.msg("<blue>IRC | DM to %s: %s".formatted(nick,msg));


												return 1;
											}))
									)));


		}));


	}

	public static void setupirc(){
		//CONFIG.load();
		if (ircthread!=null&&bot!=null&&bot.isConnected()){
			bot.sendIRC().quitServer("Restarting :3");

			try {
				ircthread.join(1000);
				ircthread.interrupt();
			} catch (InterruptedException ignored) {}

		}

		StringBuilder channelwithpass = new StringBuilder();

		channelwithpass.append(CONFIG.serverstuff.postjoinchannel);
		if (CONFIG.serverstuff.channelpass!=""){
			channelwithpass.append(" ").append(CONFIG.serverstuff.channelpass);
		}

		config = new Configuration.Builder()
              .setName(CONFIG.serverstuff.username) //Nick of the bot. CHANGE IN YOUR CODE
              .setLogin(CONFIG.serverstuff.username) //Login part of hostmask, eg name:login@host
				  .setRealName(CONFIG.serverstuff.username+" (ComplexIRC)")
				  .setAutoReconnect(CONFIG.serverstuff.autoreconnect)
				  .setAutoReconnectAttempts(5)
              .setAutoNickChange(true) //Automatically change nick when the current one is in use
              .addAutoJoinChannel(channelwithpass.toString())//Join #pircbotx channel on connect
              .addListener(new listener())
				  .addServer(CONFIG.serverstuff.serverip, CONFIG.serverstuff.port)
				.buildConfiguration();
		ircthread = new Thread(() -> {
			bot = new PircBotX(config);
         try {
            bot.startBot();
         } catch (IOException | IrcException _) {

         }
      },"ircthread");
		ircthread.start();
		Complexirc.LOGGER.info("starting irc thread..");


	}

	public enum channel{
		normal,
		irc,
		global
	}


}