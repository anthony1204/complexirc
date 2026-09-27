package top.anthonycat.complexirc.client.mixin;


import com.mojang.blaze3d.platform.InputConstants;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.network.chat.Component;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.util;

import java.awt.*;

@Mixin(ChatScreen.class)
public class chatscreenmixin extends Screen {

   int green = Color.GREEN.getRGB();
   Audience a = MinecraftClientAudiences.of().audience();

   @Unique private Button complexirc$mcButton;
   @Unique private Button complexirc$ircButton;
   @Unique private Button complexirc$catButton;

   protected chatscreenmixin(Component title) {
      super(title);
   }

   @Shadow
   protected EditBox input;

   @Inject(method = "init", at = @At("TAIL"))
   private void ichatthingpls(CallbackInfo ci) {
      ChatScreen cs = (ChatScreen) (Object) this;

      //how hard is it to space 3 buttons equally
      //yes.

      //##  Encrypt button
      Button enc = Button.builder(Component.literal(ComplexircClient.cat ? "🔒" : "🔓"), (button) -> {
         ComplexircClient.cat = !ComplexircClient.cat;
         util.msg("<blue>IRC | "+(ComplexircClient.cat ? "Messages you sent are now encrypted.":"Messages you sent are no longer encrypted."));
            complexirc$updateButtonLabels();
      })
      .bounds(5, this.height - 38, 20, 20)
      .tooltip(Tooltip.create(Component.literal("Encrypts messages that are sent with a key. (configure the encyrption key in /irc openconfig)")))
      .build();

      //##  MC button
      Button mc = Button.builder(Component.literal(ComplexircClient.talkinirc ? " Minecraft" : ">Minecraft<"), (button) -> {
         util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<blue>minecraft<gray> " + "<gray><lang:text.chat.complexirc.chat>");
         ComplexircClient.talkinirc = false;
         ComplexircClient.currentchannel = ComplexircClient.channel.normal;
         complexirc$updateButtonLabels();
      })
      .bounds(30, this.height - 38, 96, 20)
      .tooltip(Tooltip.create(Component.literal("Switch to the Minecraft chat.")))
      .build();

      //##  IRC button
      Button irc = Button.builder(Component.literal(ComplexircClient.talkinirc ? ">IRC<" : "IRC"), (button) -> {
         ComplexircClient.currentchannel = ComplexircClient.channel.irc;

         if (ComplexircClient.bot==null || !ComplexircClient.bot.isConnected()){
            a.sendMessage(ComplexircClient.mm.deserialize("<yellow>IRC | <gray><lang:text.chat.complexirc.not_connected_force>"));
            ComplexircClient.setupirc();
         }

         util.msg("<blue>IRC | <gray><lang:text.chat.complexirc.switching_to> " + "<red>irc<gray> " + "<gray><lang:text.chat.complexirc.chat>");

         ComplexircClient.talkinirc = true;
         complexirc$updateButtonLabels();

      })
      .bounds(130, this.height - 38, 96, 20)
      .tooltip(Tooltip.create(Component.literal("Switch to the IRC chat.")))
      .build();

      this.addRenderableWidget(mc);
      this.addRenderableWidget(irc);
      if (!ComplexircClient.CONFIG.preferencesConfig.disablecat) {
         this.addRenderableWidget(enc);
      }
      complexirc$mcButton = mc;
      complexirc$ircButton = irc;
      complexirc$catButton = enc;
      //this.addRenderableWidget(global);
   }

   //Fixed button labels not updating when switching between chat screens.
   //sax i hate comments and capital letters
   //Software engineer sax mode enabled
   @Unique
   private void complexirc$updateButtonLabels() {
//      complexirc$mcButton.setMessage(Component.literal(ComplexircClient.talkinirc ? " Minecraft" : ">Minecraft<"));
//      complexirc$ircButton.setMessage(Component.literal(ComplexircClient.talkinirc ? ">IRC<" : "IRC"));
//      complexirc$catButton.setMessage(Component.literal(ComplexircClient.cat ? ">Mrewcrypted<" : "Mrewcrypted"));
      this.onClose();
      Minecraft.getInstance().gui.setScreen(new ChatScreen("",false));
   }

   @Inject(method = "extractRenderState", at = @At("HEAD"))
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a, CallbackInfo ci) {
      if (ComplexircClient.talkinirc) {
         graphics.fill(2, this.height - 14, this.width - 2, this.height - 2, 0x703D8EFF);
      }
   }

   @Inject(method = "keyPressed", at = @At("TAIL"))
   public void keyPressed(final KeyEvent event, CallbackInfoReturnable<Boolean> ci) {
      if (event.key() == InputConstants.KEY_UP || event.key() == InputConstants.KEY_DOWN) {
         this.setFocused(this.input);
      }
     // ci.setReturnValue(false);
   }
}
