package top.anthonycat.complexirc.client.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.anthonycat.complexirc.Complexirc;
import top.anthonycat.complexirc.client.ComplexircClient;
import top.anthonycat.complexirc.client.NOT_MY_CODE;
import top.anthonycat.complexirc.client.util;

@Mixin(ClientPacketListener.class)
public class clientpacketlistenermixin {
   @Unique
   private boolean allow = false;

   @Inject(method = "handlePlayerChat", at = @At("HEAD"), cancellable = true)
   public void handlePlayerChat(final ClientboundPlayerChatPacket packet, CallbackInfo ci) {
//      if (!ComplexircClient.currentchannel.equals(ComplexircClient.channel.global)&&!ComplexircClient.currentchannel.equals(ComplexircClient.channel.normal)){
//       //  ci.cancel();
//      }

      if (allow){
         allow= false;
         return;
      }


      String msg = packet.body().content();
      if (msg.isEmpty()) {
         if (packet.unsignedContent() != null) {
            msg = packet.unsignedContent().getString();
         } else {
            return;
         }

      }


      String ifcat = msg.contains(">w< ") ? msg.substring(msg.indexOf(">w< ") + 4) : msg;

      if (ifcat != msg) {
         Complexirc.LOGGER.info("before decrypt: " + ifcat);
         ifcat = NOT_MY_CODE.decrypt(ComplexircClient.CONFIG.meowkey, ifcat);
         Complexirc.LOGGER.info("after decrypt: " + ifcat);

         ifcat = ifcat + " [dec]";

         //StringBuilder f = new StringBuilder();
         if (Minecraft.getInstance().getConnection() == null) {
            Complexirc.LOGGER.info("unable to decode message due to connection being null");
            return;
         }
        // f.append("<").append(Minecraft.getInstance().getConnection().getPlayerInfo(packet.sender()).getProfile().name()).append("> ").append(ifcat).append(" [dec]");
        // Complexirc.LOGGER.info("final: " + f);
//         util.msg(f.toString());

         //new ClientboundPlayerChatPacket(packet.globalIndex(),packet.sender(),packet.index(),packet.signature(),new SignedMessageBody.Packed(ifcat,packet.body().timeStamp(),packet.body().salt(),packet.body().lastSeen()), Component.literal(ifcat),packet.filterMask(),packet.chatType());
       if (ifcat.contains("failedtodecode")) return;
       allow = true;
       ci.cancel();
        Minecraft.getInstance().getConnection().handlePlayerChat(new ClientboundPlayerChatPacket(packet.globalIndex(), packet.sender(), packet.index(), packet.signature(), new SignedMessageBody.Packed(ifcat, packet.body().timeStamp(), packet.body().salt(), packet.body().lastSeen()), Component.literal(ifcat), packet.filterMask(), packet.chatType()));
         //ci.cancel();
      }
   }
}
