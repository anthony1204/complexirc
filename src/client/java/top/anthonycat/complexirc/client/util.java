package top.anthonycat.complexirc.client;

import net.kyori.adventure.platform.fabric.impl.client.AdventureFabricClient;
import net.kyori.adventure.platform.modcommon.MinecraftClientAudiences;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;

public enum util {;
   public static void msg(String message){
      if (Minecraft.getInstance().player==null) return;

      ComplexircClient.audience.sendMessage(ComplexircClient.mm.deserialize(message));

//      Component finalmsg = MinecraftClientAudiences.of().asNative(ComplexircClient.mm.deserialize(message));
//      GuiMessage guimsg = new GuiMessage(Minecraft.getInstance().gui.hud.getGuiTicks(), finalmsg,null, GuiMessageSource.SYSTEM_SERVER, GuiMessageTag.system());
//      ComplexircClient.globalmsg.add(guimsg);
//      ComplexircClient.mcmsg.add(guimsg);
//      ComplexircClient.ircmsg.add(guimsg);

      //((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$customrefresh();

   }

   public static void refresh(){
//      ((hiss) Minecraft.getInstance().gui.hud.getChat()).complexirc$refresh();
   }
}
