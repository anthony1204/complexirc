package top.anthonycat.complexirc.client.mixin;


import io.wispforest.owo.ui.core.UIComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.broofbutton;
import top.anthonycat.complexirc.client.hiss;
import top.anthonycat.complexirc.client.util;

import java.awt.*;

@Mixin(ChatScreen.class)
public class chatscreenmixin extends Screen {

   int green = Color.GREEN.getRGB();

   protected chatscreenmixin(Component title) {
      super(title);
   }

   @Shadow
   protected EditBox input;

   @Inject(method = "init", at = @At("TAIL"))
   private void ichatthingpls(CallbackInfo ci) {
      ChatScreen cs = (ChatScreen) (Object) this;

      Button mc = Button.builder(Component.literal(ComplexircClient.currentchannel == ComplexircClient.channel.normal ? ">minecraft<" : "minecraft"), (button) -> {
         util.msg("<blue>IRC | changed channel to minecraft chat only");
         ComplexircClient.talkinirc = false;
         ComplexircClient.currentchannel = ComplexircClient.channel.normal;

         Minecraft.getInstance().gui.hud.getChat().setVisibleMessageFilter(msg -> {
            boolean decision = true;
            if (msg.content().getString().startsWith("IRC | ")) decision = false;

            return decision;


         });



         double mx = Minecraft.getInstance().mouseHandler.xpos();
         double my = Minecraft.getInstance().mouseHandler.ypos();

         cs.onClose();

         Minecraft.getInstance().gui.setScreen(new ChatScreen("", false));

         GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), mx, my);
        // ((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
      }).bounds(12, this.height - 38, 96, 20).build();

      Button global = Button.builder(Component.literal(ComplexircClient.currentchannel == ComplexircClient.channel.global ? ">global<" : "global"), (button) -> {
         util.msg("<blue>IRC | changed channel to global (both)");
         ComplexircClient.currentchannel = ComplexircClient.channel.global;

         Minecraft.getInstance().gui.hud.getChat().setVisibleMessageFilter(msg -> true);


         double mx = Minecraft.getInstance().mouseHandler.xpos();
         double my = Minecraft.getInstance().mouseHandler.ypos();

         cs.onClose();

         Minecraft.getInstance().gui.setScreen(new ChatScreen("", false));

         GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), mx, my);
      //   ((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
      }).bounds(116, this.height - 38, 96, 20).build();


      Button irc = Button.builder(Component.literal(ComplexircClient.currentchannel == ComplexircClient.channel.irc ? ">irc<" : "irc"), (button) -> {
         util.msg("<blue>IRC | irc only mode (server msges exempt)");

         ComplexircClient.currentchannel = ComplexircClient.channel.irc;

         if (ComplexircClient.bot==null){
            util.msg("<blue>IRC | you are not connected to irc, connecting now.");
            ComplexircClient.setupirc();
            return;
         }
         if (!ComplexircClient.bot.isConnected()){
            util.msg("<blue>IRC | you are not connected to irc, connecting now.");
            ComplexircClient.setupirc();
            return;
         }

         ComplexircClient.talkinirc = true;
         Minecraft.getInstance().gui.hud.getChat().setVisibleMessageFilter(msg -> {
            boolean decision = false;
            if (msg.content().getString().startsWith("IRC | ")) decision = true;
            if (msg.source()!= GuiMessageSource.PLAYER) decision = true;

            return decision;
         });



         double mx = Minecraft.getInstance().mouseHandler.xpos();
         double my = Minecraft.getInstance().mouseHandler.ypos();

         cs.onClose();

         Minecraft.getInstance().gui.setScreen(new ChatScreen("", false));
         GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().handle(), mx, my);
        // ((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();
      }).bounds(220, this.height - 38, 96, 20).build();

      this.addRenderableWidget(mc);
      this.addRenderableWidget(irc);
      this.addRenderableWidget(global);

   }

   @Inject(method = "extractRenderState", at = @At("HEAD"))
   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a, CallbackInfo ci) {
      if (ComplexircClient.talkinirc) {
         graphics.fill(2, this.height - 14, this.width - 2, this.height - 2, 0x703D8EFF);
      }

   }

   @Inject(method = "keyPressed", at = @At("TAIL"))
   public void keyPressed(final KeyEvent event, CallbackInfoReturnable<Boolean> ci) {
      if (event.key() == GLFW.GLFW_KEY_UP || event.key() == GLFW.GLFW_KEY_DOWN) {
         this.setFocused(this.input);
      }
     // ci.setReturnValue(false);

   }



}
