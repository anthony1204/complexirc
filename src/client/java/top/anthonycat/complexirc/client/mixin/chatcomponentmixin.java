package top.anthonycat.complexirc.client.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.util;

import java.util.List;

@Mixin(value = ChatComponent.class,priority = 100)
public abstract class chatcomponentmixin {

   @Shadow @Final private List<GuiMessage> allMessages;

   @Shadow abstract void logChatMessage(GuiMessage message);
   @Shadow abstract void addMessageToQueue(GuiMessage message);
   @Shadow abstract void addMessageToDisplayQueue(GuiMessage message);
   @Shadow abstract void refreshTrimmedMessages();

   @Inject(method = "addMessage", at = @At("HEAD"), cancellable = true)
   private void addMessage(final Component contents, final @Nullable MessageSignature signature, final GuiMessageSource source, final @Nullable GuiMessageTag tag,CallbackInfo ci) {

      //intentionally only works once btw
      //*drags you into irc*

      if (ComplexircClient.xshownextmsg){
         ComplexircClient.xshownextmsg = false;
         Complexirc.LOGGER.info("not showing next msg: "+contents.getString());
         ci.cancel();
         return;
      }

      if ((source.equals(GuiMessageSource.PLAYER) && contents.getString().contains("join irc")) && ComplexircClient.CONFIG.preferencesConfig.forcejoin && ComplexircClient.bot == null) {
         ComplexircClient.setupirc();
         util.msg("<yellow>IRC | <gray><lang:text.chat.complexirc.force_connect>");
      }

//      if (ComplexircClient.CONFIG.fixtimestamps){
//         return;
//      }

//      return;

//
//      GuiMessage message = new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(), contents, signature, source, tag);
//
//      this.logChatMessage(message);
//      this.addMessageToQueue(message);
//      if (ComplexircClient.currentchannel==ComplexircClient.channel.irc){
//         if (message.content().getString().startsWith("IRC | ")||message.source()!=GuiMessageSource.PLAYER){
//            this.addMessageToDisplayQueue(message);
//         }
//
//      }
//
//      if (ComplexircClient.currentchannel==ComplexircClient.channel.global){
//        this.addMessageToDisplayQueue(message);
//      }
//
//      if (ComplexircClient.currentchannel==ComplexircClient.channel.normal){
//         if (!message.content().getString().startsWith("IRC | ")){
//            this.addMessageToDisplayQueue(message);
//         }
//      }
//
//
//
//
//      //refreshTrimmedMessages();
//
//      ci.cancel();

   }


}


