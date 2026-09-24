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
      public String serverip = "irc.anthonycat.top";
      public Integer port = 6667;
      public String username = "complexircuser";
      public String postjoinchannel = "#channelname";
      public String channelpass = "";
      public boolean autoreconnect = true;
   }

//   public Boolean fixtimestamps = false; //note: chat channels got removed

   public List<String> postcommand = new ArrayList<>();
   public String meowkey = "iNseRtSeCurESoMeThIngHerE";


}
