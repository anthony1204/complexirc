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
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.Config;
import top.anthonycat.complexirc.client.hiss;
import top.anthonycat.complexirc.client.util;

@Mixin(ClientPacketListener.class)
public class ExampleClientMixin {

	private boolean allow = false;

	@Inject(at = @At("HEAD"), method = "sendChat",cancellable = true)
	private void init(String content,CallbackInfo info) {
		if (allow) {
			allow = false;
			return;
		}
		if (content.equals("!")){
			util.msg("<blue>IRC | you are now talking in minecraft chat");
			ComplexircClient.talkinirc = false;
			info.cancel();
			return;
		}
		if (content.equals("#")){
			util.msg("<blue>IRC | you are now talking in irc");
			ComplexircClient.talkinirc = true;
			info.cancel();
			return;
		}
		if (content.startsWith("!")){
			content = content.substring(1);
			info.cancel();
			allow = true;
			Minecraft.getInstance().getConnection().sendChat(content);
			return;
		}
		if (content.startsWith("#")){
			info.cancel();

			if (ComplexircClient.bot!=null&&!ComplexircClient.bot.isConnected()){
				util.msg("<red>IRC | you are not connected to irc, connecting now");
				ComplexircClient.setupirc();
				return;
			}
			content = content.substring(1);
//			Component aug;
//
//			aug = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize("<blue>IRC | <reset><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverstuff.username(),content)));
//

//			ComplexircClient.ircmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
//					aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
			//((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();

			ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverstuff.postjoinchannel(),content);
			util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverstuff.username(),content));
			return;
		}


		if (!ComplexircClient.talkinirc) return;


		if (ComplexircClient.bot==null){
			util.msg("<red>IRC | you are not connected to irc, connecting now");
			ComplexircClient.setupirc();
			return;
		}


		if (!ComplexircClient.bot.isConnected()){
			util.msg("<red>IRC | you are not connected to irc, connecting now");
			ComplexircClient.setupirc();
			return;
		}
//
//		Component aug;
//
//		aug = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize("<blue>IRC | <reset><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverstuff.username(),content)));
//

//		ComplexircClient.ircmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
//				aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
//		((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();


		ComplexircClient.bot.sendIRC().message(ComplexircClient.CONFIG.serverstuff.postjoinchannel(),content);
		util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(ComplexircClient.CONFIG.serverstuff.username(),content));
		info.cancel();
	}
}
