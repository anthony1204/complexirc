package top.anthonycat.complexirc.client.mixin;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.NOT_MY_CODE;
import top.anthonycat.complexirc.client.util;

@Mixin(ClientPacketListener.class)
public class ExampleClientMixin {

	private boolean allow = false;

	@Inject(at = @At("HEAD"), method = "sendChat", cancellable = true)
	private void init(String content,CallbackInfo info) {
		if (allow) {
			allow = false;
			return;
		}

		//kindly find a better way to update this when config is changed
		if (ComplexircClient.CONFIG.preferencesConfig.disablecat) ComplexircClient.cat = false;



		//was going to add the no cat chat thing here but its kinda useless as you cant even
		//ignore the previous comment i had a brainwave
		if (ComplexircClient.cat&&!ComplexircClient.talkinirc&&!ComplexircClient.CONFIG.preferencesConfig.disablecat){
			info.cancel();
			String neww = NOT_MY_CODE.encrypt(ComplexircClient.CONFIG.meowkey, content);
			if (neww.length()>=256){
				util.msg("<blue>C.A.T | encrypted message too long, will get you kicked");
				return;
			}
			allow = true;
//			if (ComplexircClient.cat) util.msg("<blue>you: "+content+" [dec]");
			Complexirc.LOGGER.info("your original message for logging purposes: "+content);
			Minecraft.getInstance().getConnection().sendChat(">w< "+neww);
			return;
		}

		if (content.equals("!")){
			util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to>" + "<blue>minecraft<gray> " + "<lang:text.chat.complexirc.chat>");
			ComplexircClient.talkinirc = false;
			info.cancel();
			return;
		}

		if (content.equals("#")){
			util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to>" + "<red>irc<gray> " + "<lang:text.chat.complexirc.chat>");
			ComplexircClient.talkinirc = true;
			info.cancel();
			return;
		}

		if (content.startsWith("!")){
			content = content.substring(1);
//			info.cancel();
//			allow = true;
//			Minecraft.getInstance().getConnection().sendChat(content);
			info.cancel();
			String neww = NOT_MY_CODE.encrypt(ComplexircClient.CONFIG.meowkey, content);
			if (neww.length()>=256){
				util.msg("<blue>C.A.T | encrypted message too long, will get you kicked");
				return;
			}
			//worst code ever, fix someone submitting a pr feels like it
			if (!ComplexircClient.cat) neww = content;

			allow = true;
			if (ComplexircClient.CONFIG.preferencesConfig.disablecat) neww = content;

			if (ComplexircClient.cat&&!ComplexircClient.CONFIG.preferencesConfig.disablecat) util.msg("<blue>you: "+content+" <gray><decrypted>");
			Minecraft.getInstance().getConnection().sendChat(ComplexircClient.cat?">w< ": neww);

			return;
		}

		if (content.startsWith("#")){
			info.cancel();

			if (ComplexircClient.bot!=null&&!ComplexircClient.bot.isConnected()){
				util.msg("<red>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
				ComplexircClient.setupirc();
				return;
			}
			content = content.substring(1);
			//			Component aug;
			//
			//			aug = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize("<blue>IRC | <reset><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverConfig.username(),content)));
			//

			//			ComplexircClient.ircmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
			//					aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
						//((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
			if (ComplexircClient.cat) {
				String neww = NOT_MY_CODE.encrypt(ComplexircClient.CONFIG.meowkey, content);
				ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverConfig.postjoinchannel, ">w< "+neww);
				util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.bot.getNick(), ">w< "+content+" <gray><encrypted>"));

			} else {
				ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverConfig.postjoinchannel, content);
				util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.bot.getNick(), content));
			}
			return;

		}

		if (!ComplexircClient.talkinirc) return;

		if (ComplexircClient.bot==null){
			util.msg("<red>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
			ComplexircClient.setupirc();
			return;
		}

		if (!ComplexircClient.bot.isConnected()){
			util.msg("<red>IRC | <gray><lang:text.chat.complexirc.not_connected_force>");
			ComplexircClient.setupirc();
			return;
		}
		//
		//		Component aug;
		//
		//		aug = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize("<blue>IRC | <reset><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverConfig.username(),content)));
		//

		//		ComplexircClient.ircmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
		//				aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
		//		((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
		if (ComplexircClient.cat) {
			String neww = NOT_MY_CODE.encrypt(ComplexircClient.CONFIG.meowkey, content);
			ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverConfig.postjoinchannel, ">w< "+neww);
			util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.bot.getNick(), content+" <gray><encrypted>"));

		} else {
			ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverConfig.postjoinchannel, content);
			util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.bot.getNick(), content));
		}

		info.cancel();
	}
}
