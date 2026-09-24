package top.anthonycat.complexirc.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.anthonycat.complexirc.client.Configuration;

@Environment(EnvType.CLIENT)
class mm implements ModMenuApi {

   @Override
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return parent -> AutoConfigClient.getConfigScreen(Configuration.class, parent).get();
   }
}