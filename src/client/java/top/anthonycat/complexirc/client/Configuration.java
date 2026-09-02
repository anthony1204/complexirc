package top.anthonycat.complexirc.client;


import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.SectionHeader;

import java.util.ArrayList;
import java.util.List;

@io.wispforest.owo.config.annotation.Config(name="complex-irc",wrapperName = "Config")
@Modmenu(modId = "complexirc")
public class Configuration {

   @Nest
   public server serverstuff = new server();
   public static class server {
      public String serverip = "baseduser.eu.org";
      public Integer port = 6667;
      public String username = "meowmrrp";
      public String postjoinchannel = "#channelname";
      public String channelpass = "channel password leave empty if none";
   }

   public List<String> postcommand = new ArrayList<>();


}
