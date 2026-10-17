package top.anthonycat.complexirc.client;

import com.frikinjay.dungeonconfig.api.ConfigEntry;
import com.frikinjay.dungeonconfig.api.DungeonConfig;

public class ConfigV2 {
   //omg its so clean gui
   public static final DungeonConfig CONFIG = DungeonConfig.builder("complexirc")
           .displayName("Complexirc")              // optional, defaults to the name in your mod metadata
           .build();
   public static final ConfigEntry<String> test =
           CONFIG.addString("test11!!", "test", "test value pls ignore", "whats this for");

   public static void init() {
      CONFIG.load();   // after every entry exists: reads the file, creating it with defaults
   }
}
