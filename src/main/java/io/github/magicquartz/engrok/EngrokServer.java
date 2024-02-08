package io.github.magicquartz.engrok;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import static io.github.magicquartz.engrok.Engrok.LOGGER;

@Environment(EnvType.SERVER)
public class EngrokServer implements DedicatedServerModInitializer {
    public static int port;
    @Override
    public void onInitializeServer() {
        LOGGER.info("Dedicated server detected!");

        /*ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            port = server.getServerPort();
            LOGGER.info("PORT AAAA " + port);
        });*/
    }


}
