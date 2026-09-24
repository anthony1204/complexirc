package top.anthonycat.complexirc.client;




import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.util.ArrayList;
import java.util.List;

@Config(name="complexirc")
public class Configuration implements ConfigData {

   @ConfigEntry.Gui.CollapsibleObject
   public server serverstuff = new server();
   public static class server {
      public String serverip = "baseduser.eu.org";
      public Integer port = 6667;
      public String username = "meowmrrp";
      public String postjoinchannel = "#channelname";
      public String channelpass = "channel password leave empty if none";
   }

   public Boolean fixtimestamps = false;

   public List<String> postcommand = new ArrayList<>();


}
