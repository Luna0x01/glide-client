package me.eldodebug.soar;

import java.io.File;
import java.util.Arrays;

import me.eldodebug.soar.gui.mainmenu.GuiGlideMainMenu;
import me.eldodebug.soar.gui.modmenu.GuiModMenu;
import me.eldodebug.soar.management.remote.discord.DiscordStats;
import me.eldodebug.soar.management.remote.discord.DiscordManager;
import me.eldodebug.soar.management.remote.update.Update;
import me.eldodebug.soar.management.remote.update.UpdateManager;
import org.apache.commons.lang3.ArrayUtils;

import me.eldodebug.soar.injection.mixin.GlideTweaker;
import me.eldodebug.soar.logger.GlideLogger;
import me.eldodebug.soar.management.cape.CapeManager;
import me.eldodebug.soar.management.remote.changelog.ChangelogManager;
import me.eldodebug.soar.management.color.ColorManager;
import me.eldodebug.soar.management.command.CommandManager;
import me.eldodebug.soar.management.event.EventManager;
import me.eldodebug.soar.management.file.FileManager;
import me.eldodebug.soar.management.language.LanguageManager;
import me.eldodebug.soar.management.mods.ModManager;
import me.eldodebug.soar.management.mods.impl.GlobalSettingsMod;
import me.eldodebug.soar.management.nanovg.NanoVGManager;
import me.eldodebug.soar.management.notification.NotificationManager;
import me.eldodebug.soar.management.profile.ProfileManager;
import me.eldodebug.soar.management.quickplay.QuickPlayManager;
import me.eldodebug.soar.management.screenshot.ScreenshotManager;
import me.eldodebug.soar.management.security.SecurityFeatureManager;
import me.eldodebug.soar.management.waypoint.WaypointManager;
import me.eldodebug.soar.utils.OptifineUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Glide {

	private static Glide instance = new Glide();
	private Minecraft mc = Minecraft.getMinecraft();
	private boolean updateNeeded;
	private String name, version;
	private int verIdentifier;
	
	private NanoVGManager nanoVGManager;
	private FileManager fileManager;
	private LanguageManager languageManager;
	private EventManager eventManager;
	private ModManager modManager;
	private CapeManager capeManager;
	private ColorManager colorManager;
	private ProfileManager profileManager;
	private CommandManager commandManager;
	private ScreenshotManager screenshotManager;
	private NotificationManager notificationManager;
	private SecurityFeatureManager securityFeatureManager;
	private QuickPlayManager quickPlayManager;
	private ChangelogManager changelogManager;
	private DiscordStats discordStats;
    private WaypointManager waypointManager;
	private GuiModMenu modMenu;
	private GuiGlideMainMenu mainMenu;
	private long launchTime;
	private File firstLoginFile;
	private Update update;
	
	public Glide() {
		name = "Glide";
		version = "7.2";
		verIdentifier = 7199;
	}
	
	public void start() {
		OptifineUtils.disableFastRender();
		this.removeOptifineZoom();
		
		fileManager = new FileManager();
		update = new Update();
		firstLoginFile = new File(fileManager.getCacheDir(), "first.tmp");
		languageManager = new LanguageManager();
		eventManager = new EventManager();
		modManager = new ModManager();
		
		modManager.init();
		new UpdateManager();
		
		capeManager = new CapeManager();
		colorManager = new ColorManager();
		profileManager = new ProfileManager();

		modMenu = new GuiModMenu();
		mainMenu = new GuiGlideMainMenu();
		launchTime = System.currentTimeMillis();

		commandManager = new CommandManager();
		screenshotManager = new ScreenshotManager();
		notificationManager = new NotificationManager();
		securityFeatureManager = new SecurityFeatureManager();
		quickPlayManager = new QuickPlayManager();
		changelogManager = new ChangelogManager();
		discordStats = new DiscordStats();
		new DiscordManager();
		waypointManager = new WaypointManager();

		eventManager.register(new GlideHandler());

		GlobalSettingsMod.getInstance().setToggled(true);
		mc.updateDisplay();
	}
	
	public void stop() {
		profileManager.save();
	}
	
	private void removeOptifineZoom() {
		if(GlideTweaker.hasOptifine) {
			try {
				this.unregisterKeybind((KeyBinding) GameSettings.class.getField("ofKeyBindZoom").get(mc.gameSettings));
			} catch(Exception e) {
				GlideLogger.error("Failed to unregister zoom key", e);
			}
		}
	}
	
    private void unregisterKeybind(KeyBinding key) {
        if (Arrays.asList(mc.gameSettings.keyBindings).contains(key)) {
            mc.gameSettings.keyBindings = ArrayUtils.remove(mc.gameSettings.keyBindings, Arrays.asList(mc.gameSettings.keyBindings).indexOf(key));
            key.setKeyCode(0);
        }
    }
    
	public static Glide getInstance() {
		return instance;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {return version;}

	public int getVersionIdentifier() {return verIdentifier;}

	public FileManager getFileManager() {
		return fileManager;
	}

	public ModManager getModManager() {
		return modManager;
	}

	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public NanoVGManager getNanoVGManager() {
		return nanoVGManager;
	}

	public ColorManager getColorManager() {
		return colorManager;
	}

	public ProfileManager getProfileManager() {
		return profileManager;
	}

	public CapeManager getCapeManager() {
		return capeManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public ScreenshotManager getScreenshotManager() {
		return screenshotManager;
	}

	public void setNanoVGManager(NanoVGManager nanoVGManager) {
		this.nanoVGManager = nanoVGManager;
	}

	public NotificationManager getNotificationManager() {
		return notificationManager;
	}

	public SecurityFeatureManager getSecurityFeatureManager() {
		return securityFeatureManager;
	}

	public QuickPlayManager getQuickPlayManager() {
		return quickPlayManager;
	}

	public ChangelogManager getChangelogManager() {
		return changelogManager;
	}

	public DiscordStats getDiscordStats() {
		return discordStats;
	}

	public WaypointManager getWaypointManager() {
		return waypointManager;
	}

	public GuiModMenu getModMenu() {
		return modMenu;
	}

	public GuiGlideMainMenu getMainMenu() {
		return mainMenu;
	}

	public long getLaunchTime() {
		return launchTime;
	}

	public void createFirstLoginFile() {
		Glide.getInstance().getFileManager().createFile(firstLoginFile);
	}

	public boolean isFirstLogin() {return !firstLoginFile.exists();}

	public Update getUpdateInstance(){
		return update;
	}
	public void setUpdateNeeded(boolean in) {updateNeeded = in;}
	public boolean getUpdateNeeded() {return updateNeeded;}
}
