package me.eldodebug.soar.gui.mainmenu.impl.welcome;

import me.eldodebug.soar.Glide;
import me.eldodebug.soar.gui.mainmenu.GuiGlideMainMenu;
import me.eldodebug.soar.gui.mainmenu.MainMenuScene;
import me.eldodebug.soar.management.color.AccentColor;
import me.eldodebug.soar.management.color.Theme;
import me.eldodebug.soar.management.color.palette.ColorType;
import me.eldodebug.soar.management.language.Language;
import me.eldodebug.soar.management.language.LanguageManager;
import me.eldodebug.soar.management.nanovg.NanoVGManager;
import me.eldodebug.soar.management.nanovg.font.Fonts;
import me.eldodebug.soar.utils.animation.normal.Animation;
import me.eldodebug.soar.utils.animation.normal.Direction;
import me.eldodebug.soar.utils.animation.normal.other.DecelerateAnimation;
import me.eldodebug.soar.utils.buffer.ScreenAlpha;
import me.eldodebug.soar.utils.mouse.MouseUtils;
import me.eldodebug.soar.utils.mouse.Scroll;
import me.eldodebug.soar.utils.render.BlurUtils;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

public class LanguageSelectScene extends MainMenuScene {

	private int x, y, width, height;

	private Animation fadeAnimation;
	private ScreenAlpha screenAlpha = new ScreenAlpha();
	private Scroll scroll = new Scroll();
	LanguageManager languageManager = Glide.getInstance().getLanguageManager();
	private Language currentLanguage = languageManager.getCurrentLanguage();

	public LanguageSelectScene(GuiGlideMainMenu parent) {
		super(parent);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		ScaledResolution sr = new ScaledResolution(mc);
		
		width = 280;
		height = 146;
		x = sr.getScaledWidth() / 2 - (width / 2);
		y = sr.getScaledHeight() / 2 - (height / 2);
		
		if(fadeAnimation == null) {
			fadeAnimation = new DecelerateAnimation(800, 1);
			fadeAnimation.setDirection(Direction.FORWARDS);
			fadeAnimation.reset();
		}
		
		BlurUtils.drawBlurScreen(14);
		
		screenAlpha.wrap(() -> drawNanoVG(), fadeAnimation.getValueFloat());
		
		if(fadeAnimation.isDone(Direction.BACKWARDS)) {
			this.setCurrentScene(this.getSceneByClass(ThemeSelectScene.class));
		}
	}
	
	private void drawNanoVG() {
		
		Glide instance = Glide.getInstance();
		NanoVGManager nvg = instance.getNanoVGManager();
		AccentColor currentColor = instance.getColorManager().getCurrentColor();
		
		int offsetX = 0;
		int index = 1;
		
		nvg.drawRoundedRect(x, y, width, height, 8, this.getBackgroundColor());
		nvg.drawCenteredText("Choose a Language", x + (width / 2), y + 10, Color.WHITE, 16, Fonts.MEDIUM);
		nvg.drawRect(x, y + 27, width, 1, Color.WHITE);
		
		scroll.onScroll();
		scroll.onAnimation();
		
		nvg.save();
		nvg.scissor(x, y + 27, width, height - 27);
		nvg.translate(scroll.getValue(), 0);

		for(Language lang : Language.values()) {
			nvg.drawRoundedImage(lang.getFlag(), x + offsetX + 14, y + 42, 90, 56, 4);
			nvg.drawCenteredText(lang.getName(), x + offsetX + 14 + (90 / 2), y + 104, Color.WHITE, 7F, Fonts.REGULAR);
			if(lang == currentLanguage) {
				nvg.drawGradientOutlineRoundedRect(x + offsetX + 14, y + 42, 90, 56, 6, 2, currentColor.getColor1(), currentColor.getColor2());
			}
			offsetX+=102;
			index++;
		}
		
		scroll.setMaxScroll((index - 3.58F) * 102);
		
		nvg.restore();
		
		nvg.drawRoundedRect(x + width - 86, y + height - 26, 80, 20, 6, this.getBackgroundColor());
		nvg.drawCenteredText("Next", x + width - 86 + (80 / 2), y + height - 20, Color.WHITE, 10, Fonts.REGULAR);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		
		float offsetX = scroll.getValue();
		
		for(Language lang : Language.values()) {
			
			if(MouseUtils.isInside(mouseX, mouseY, x + offsetX + 14, y + 42, 90, 56) && mouseButton == 0) {
				currentLanguage = lang;
			}
			
			offsetX+=102;
		}
		
		if(MouseUtils.isInside(mouseX, mouseY, x + width - 86, y + height - 26, 80, 20) && mouseButton == 0) {
			Glide.getInstance().getLanguageManager().setCurrentLanguage(currentLanguage);
			fadeAnimation.setDirection(Direction.BACKWARDS);
		}
	}
}
