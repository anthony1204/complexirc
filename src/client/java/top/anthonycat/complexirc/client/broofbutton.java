package top.anthonycat.complexirc.client;

import io.wispforest.owo.ui.util.FocusHandler;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class broofbutton extends Button.Plain {

   public broofbutton(int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration createNarration) {
      super(x, y, width, height, message, onPress, createNarration);
   }

   @Override
   public ComponentPath nextFocusPath(FocusNavigationEvent event) {
      return null;
   }

}
