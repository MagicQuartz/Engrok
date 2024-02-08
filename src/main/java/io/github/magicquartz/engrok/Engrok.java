package io.github.magicquartz.engrok;

import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import com.github.alexdlaird.ngrok.NgrokClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Engrok implements ModInitializer {
	public static final String MOD_ID = "engrok";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static NgrokClient ngrokClient;
	public static boolean tunnelOpen = false;
	@Override
	public void onInitialize() {
		LOGGER.info("Minecraft Server started with Engrok");
		AutoConfig.register(EngrokConfig.class, JanksonConfigSerializer::new);
	}
}