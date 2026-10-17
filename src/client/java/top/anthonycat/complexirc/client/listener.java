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
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;
import top.anthonycat.complexirc.Complexirc;

public class listener extends ListenerAdapter {
   MiniMessage mm = MiniMessage.miniMessage();
   Audience a = MinecraftClientAudiences.of().audience();

   @Override
   public void onConnect(ConnectEvent event) {
      //Succesful connect
      Complexirc.LOGGER.info("Connected to irc server");
      if (Minecraft.getInstance().player!=null){
         util.msg("<green>IRC | <lang:text.chat.complexirc.connected> <gray>" + Configuration.serverip.get() + ":" + Configuration.port.get());
      }

      Configuration.postcommand.get().forEach(command -> {
               ComplexircClient.bot.sendRaw().rawLine(command);
               Complexirc.LOGGER.info("sending {}", command);
            });

//      StringBuilder channelwithpass = new StringBuilder();
//
//      //apparently hashtags dont work in dungeonconfig
//      channelwithpass.append(top.anthonycat.complexirc.client.Configuration.postjoinchannel);
//      if (top.anthonycat.complexirc.client.Configuration.channelpass.get()!=""){
//         channelwithpass.append(" ").append(top.anthonycat.complexirc.client.Configuration.channelpass.get());
//      }
//      Complexirc.LOGGER.info("channnel with pass = "+channelwithpass);


      //surely there is a better way
      if (Configuration.channelpass.get().isEmpty()){
         util.msg("<blue>IRC | attempting to join "+Configuration.postjoinchannel.get());
         event.getBot().sendIRC().joinChannel(Configuration.postjoinchannel.get());
      }else{
         util.msg("<blue>IRC | attempting to join "+Configuration.postjoinchannel.get());
         event.getBot().sendIRC().joinChannel(Configuration.postjoinchannel.get(),Configuration.channelpass.get());
      }







   }

   @Override
   public void onPrivateMessage(PrivateMessageEvent e){
      util.msg("<blue>IRC | <gray>DM from "+e.getUser().getNick()+": "+e.getMessage());
   }

   @Override
   public void onTopic(TopicEvent e){
      util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.channel_topic> "+e.getTopic());
   }

   @Override
   public void onMode(ModeEvent e){

      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<yellow>IRC | <gray>Mode changed in %s by %s to %s".formatted(e.getChannel().getName(),e.getUser().getNick(),e.getMode()));
   }

   @Override
   public void onNickChange(NickChangeEvent e){
      util.msg("<yellow>IRC | User <gold>'%s' <blue>changed their nickname to <gold>'%s'.".formatted(e.getOldNick(),e.getNewNick()));
   }

   @Override
   public void onDisconnect(DisconnectEvent e){
      util.msg("<red>IRC | <gray>Disconnected from IRC server; reason: "+e.getDisconnectException().getMessage());
   }

   //##  What is this info needed for? sk
   //why even comment it out.
   //its for /umode
    @Override
    public void onUserMode(UserModeEvent e){
       util.msg("<yellow>IRC | <gray>Mode for "+e.getUser().getNick()+" changed to <light_purple>"+e.getMode());
    }

   @Override
   public void onJoin(JoinEvent e){
      // if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
      //    return;
      // }
         if (e.getUser().getNick().equalsIgnoreCase(e.getBot().getNick())) {
            //why.
//            if (!e.getChannel().getName().equalsIgnoreCase(Configuration.postjoinchannel.get())) {
//               util.msg("<red>IRC | <gray><lang:text.chat.complexirc.wrong_channel>");
//               ComplexircClient.bot.stopBotReconnect();
//               return;
//            }
           // ComplexircClient.talkinirc = true;
            //this is not even the correct spot
//            Configuration.postcommand.get().forEach(command -> {
//               ComplexircClient.bot.sendRaw().rawLine(command);
//               Complexirc.LOGGER.info("sending {}", command);
//            });

            if (Configuration.joinchannelchatinirc.get()){
               ComplexircClient.talkinirc = true;
            }

            if (Minecraft.getInstance().player!=null){
               util.msg("<green>IRC | <gray><lang:text.chat.complexirc.channel_joined> " + e.getChannel().getName());
            }
         } else {
            //If connected user is not the bot itself, print join msg.
            util.msg("<green>IRC | +%s (%s) joined %s channel.".formatted(e.getUser().getNick(),e.getUser().getRealName(),e.getChannel().getName()));
         }
         return;
   }

   @Override
   public void onQuit(QuitEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<red>IRC | -%s (%s) quitting.".formatted(e.getUser().getNick(),e.getUser().getRealName()));
   }

   @Override
   public void onKick(KickEvent e){
      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)){
         return;
      }
      util.msg("<red>IRC | %s (%s) was kicked by %s from the %s channel.".formatted(e.getRecipient().getNick(),e.getRecipient().getRealName(),e.getUser().getNick(),e.getChannel().getName()));
   }

   @Override
   public void onNotice(NoticeEvent e){
      if (!Configuration.sendnotice.get()) return;
      util.msg("<yellow>IRC | Notice: "+e.getNotice());
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
      util.msg("<red>IRC | IRC connection failed, reason: %s".formatted(e.getConnectExceptions().toString()));
   }

   @Override
   public void onMessage(MessageEvent e){
      //##Depricated - Chat Channels removed. 
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

      String message = e.getMessage().contains(">w< ") ? e.getMessage().substring(e.getMessage().indexOf(">w< ") + 4):e.getMessage();

      if (message!=e.getMessage()){
         Complexirc.LOGGER.info("before decrypt: "+message);
         try {
            message = NOT_MY_CODE.decrypt(Configuration.meowkey.get(), message);
         } catch (Exception _) {}
         Complexirc.LOGGER.info("after decrypt: "+message);
         message = message + " [dec]";
         if (message.contains("failedtodecode")) message = e.getMessage() + " [failed to decode]";
      }

      util.msg("<blue>IRC | <white><<red>%s<white>> <white>%s".formatted(e.getUser().getNick(), message));
      //##Depricated - Chat Channels removed. 
      //      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.irc)) {
      //         util.msg("<blue>IRC | <white><<red>%s<reset>> %s".formatted(e.getUser().getNick(), e.getMessage()));
      //
      //      }
   }
}
