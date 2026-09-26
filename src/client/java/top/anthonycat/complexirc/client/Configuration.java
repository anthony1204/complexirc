package top.anthonycat.complexirc.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import me.shedaniel.math.Color;

import java.util.ArrayList;
import java.util.List;

@Config(name="complexirc")
public class Configuration implements ConfigData {

   @ConfigEntry.Gui.CollapsibleObject
   public server serverConfig = new server();
   @ConfigEntry.Gui.CollapsibleObject
   public colorconfig colorConfig = new colorconfig();
   @ConfigEntry.Gui.CollapsibleObject
   public preferences preferencesConfig = new preferences();

   public static class server {
      public String serverip = "irc.anthonycat.top";
      public Integer port = 6667;
      public String username = "complexircuser";
      public String postjoinchannel = "#channelname";
      public String channelpass = "";
      public boolean autoreconnect = true;
      public boolean autojoin = true;
   }

   public static class colorconfig {
      public String ircChatColor = "#07bad1";
      public int ircChatOpacity = 150;
   }

   public static class preferences {
      public boolean forcejoin = true;
      public boolean sendnotice = true;
      @Comment("for hansen, this disables everything related to cat lang encryption")
      public boolean disablecat = false;
   }
   //public Boolean fixtimestamps = false; //note: chat channels got removed

   public List<String> postcommand = new ArrayList<>();
   public String meowkey = "iNseRtSeCurESoMeThIngHerE";


}
