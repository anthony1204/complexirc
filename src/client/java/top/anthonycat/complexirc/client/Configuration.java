package top.anthonycat.complexirc.client;

import com.frikinjay.dungeonconfig.api.ConfigEntry;
import com.frikinjay.dungeonconfig.api.DungeonConfig;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
   //omg its so clean gui
   public static final DungeonConfig CONFIG = DungeonConfig.builder("complexirc")
           .displayName("Complexirc")              // optional, defaults to the name in your mod metadata
           .noAppearance()
           .build();

 //used to be in server class
   public static ConfigEntry<String> serverip = CONFIG.addString("serverIp", "server stuff", "irc.anthonycat.top", "the irc server to connect to");
   //kis with needing to do this for not slider
   public static ConfigEntry<Integer> port = CONFIG.add(new ConfigEntry<>("port", "the irc server port to connect to", "server stuff", 6667, Integer.class));
   public static ConfigEntry<String> username = CONFIG.addString("userName", "server stuff", "complexircirc_user", "your irc username");
   public static ConfigEntry<String> postjoinchannel = CONFIG.addString("postChannel", "server stuff", "#channelname", "channel to join after connecting to irc network");
   public static ConfigEntry<String> channelpass = CONFIG.addString("channelPass", "server stuff", "", "channel password for post join channel (+k), leave empty if none");
   public static ConfigEntry<Boolean> autoreconnect = CONFIG.addBoolean("autoReconnect", "server stuff", true, "auto reconnect to irc if you get disconnected somehow (max 5 attempts");
   public static ConfigEntry<Boolean> joinchannelchatinirc = CONFIG.addBoolean("chatInIrcAfterConnecting", "other", true, "talk in irc after connecting to irc channel");




   //used to be in prefenrences class
      public static ConfigEntry<Boolean> forcejoin = CONFIG.addBoolean("otherPeopleCanDragYouIn", "other", true, "allows people to type 'join irc' in chat to drag you in (only works if you didn't disconnect manually)");
      public static ConfigEntry<Boolean> sendnotice = CONFIG.addBoolean("sendNotices", "other", true, "sets if irc notices show up");
      public static ConfigEntry<Boolean> disablecat = CONFIG.addBoolean("disableMrewcryption", "other", false, "disable Mrewcryption feature fully");



   public static ConfigEntry<List<String>> postcommand = CONFIG.add(new ConfigEntry<List<String>>("postJoinCommands",
           "commands to run in irc after joining (advanced users only), seperate with commas",
                   "server stuff", new ArrayList<>(), (Class) List.class)
                   .withElementType(String.class));

   public static ConfigEntry<String> meowkey = CONFIG.addString("mrewcryptionKey","other","iNseRtSeCurESoMeThIngHerE","key to be used for Mrewcryption (TM)");




   public static void init() {
      CONFIG.load();   // after every entry exists: reads the file, creating it with defaults

   }
}
