package io.github.magicquartz.engrok;

import io.github.magicquartz.engrok.command.EngrokCommand;
import io.github.magicquartz.engrok.command.GistCommand;
import io.github.magicquartz.engrok.command.TunnelCommand;
import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import com.github.alexdlaird.ngrok.NgrokClient;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engrok implements ModInitializer {
	public static final String MOD_ID = "engrok";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static NgrokClient ngrokClient;
	public static boolean tunnelOpen = false;
	public static boolean canCommand = false;

	public static ConfigHolder<EngrokConfig> configHolder;
	@Override
	public void onInitialize() {
		LOGGER.info("Minecraft Server started with Engrok");
		configHolder = AutoConfig.register(EngrokConfig.class, JanksonConfigSerializer::new);
	}

	public static void registerCommands()
	{
		CommandRegistrationCallback.EVENT.register(TunnelCommand::register);
		CommandRegistrationCallback.EVENT.register(GistCommand::register);
		CommandRegistrationCallback.EVENT.register(EngrokCommand::register);
	}
}