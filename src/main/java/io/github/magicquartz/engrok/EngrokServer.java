package io.github.magicquartz.engrok;

import io.github.magicquartz.engrok.command.TunnelCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import static io.github.magicquartz.engrok.Engrok.LOGGER;

@Environment(EnvType.SERVER)
public class EngrokServer implements DedicatedServerModInitializer {
    public static int port;
    @Override
    public void onInitializeServer() {
        LOGGER.info("Dedicated server detected!");

        Engrok.registerCommands();
    }
}
