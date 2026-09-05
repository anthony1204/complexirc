package top.anthonycat.complexirc.client;

import net.kyori.adventure.Adventure;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import top.anthonycat.complexirc.Complexirc;

public class listener extends ListenerAdapter {
   MiniMessage mm = MiniMessage.miniMessage();
   Audience a = MinecraftClientAudiences.of().audience();


   @Override
   public void onConnect(ConnectEvent event) {
      Complexirc.LOGGER.info("connected to irc server");
      ComplexircClient.talkinirc = true;
      ComplexircClient.CONFIG.postcommand().forEach((e) -> {
         ComplexircClient.bot.sendRaw().rawLine(e);
         Complexirc.LOGGER.info("sending {}", e);
      });
      if (Minecraft.getInstance().player!=null){
         MinecraftClientAudiences.of().audience().sendMessage(MiniMessage.miniMessage().deserialize("<blue>IRC | connected to irc successfully"));
      }
   }

   @Override
   public void onMode(ModeEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | mode changed in %s by %s to %s".formatted(e.getChannel().getName(),e.getUser().getNick(),e.getMode()));
   }

   @Override
   public void onDisconnect(DisconnectEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<red>IRC | disconnected from irc server; reason: "+e.getDisconnectException().toString());
   }


   @Override
   public void onUserMode(UserModeEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | mode for "+e.getUser().getNick()+" changed to "+e.getMode());
   }

   @Override
   public void onJoin(JoinEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | +%s (%s)".formatted(e.getUser().getNick(),e.getUser().getRealName()));
   }

   @Override
   public void onQuit(QuitEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | -%s (%s) | quitting".formatted(e.getUser().getNick(),e.getUser().getRealName()));
   }

   @Override
   public void onKick(KickEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | %s (%s) was kicked by %s".formatted(e.getRecipient().getNick(),e.getRecipient().getRealName(),e.getUser().getNick()));
   }

   @Override
   public void onPart(PartEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<blue>IRC | -%s (%s) | %s".formatted(e.getUser().getNick(),e.getUser().getRealName(),e.getReason()));
   }

   @Override
   public void onConnectAttemptFailed(ConnectAttemptFailedEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      a.sendMessage(mm.deserialize("<red>IRC | irc connection failed, errors: %s".formatted(e.getConnectExceptions().toString())));
   }


   @Override
   public void onMessage(MessageEvent e){
//      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
//       return;
//      }
//
//      Component aug;
//
//      aug = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize("<blue>IRC | <<red>%s<reset>> %s".formatted(e.getUser().getNick(),e.getMessage())));
//
//
//      ComplexircClient.ircmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
//              aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
//      ComplexircClient.globalmsg.add(new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(),
//              aug, null, GuiMessageSource.PLAYER, GuiMessageTag.chatNotSecure()));
//      ((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
//      if (ComplexircClient.currentchannel== ComplexircClient.channel.irc||ComplexircClient.currentchannel== ComplexircClient.channel.global) {
//         util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(e.getUser().getNick(), e.getMessage()));
//      }
      util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(e.getUser().getNick(), e.getMessage()));
//      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)) {
//         util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(e.getUser().getNick(), e.getMessage()));
//
//      }
   }
}
