package io.github.magicquartz.engrok.mixin;

import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.EngrokServer;
import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Region;
import com.github.alexdlaird.ngrok.protocol.Tunnel;

import static io.github.magicquartz.engrok.Engrok.LOGGER;

@Mixin(MinecraftServer.class)
public class LoadWorldMixin {

	EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();

	@Inject(at = @At("TAIL"), method = "loadWorld")
	private void loadWorld(CallbackInfo info) {
		if (config.enabled) { //if mod enabled in mod menu

			int localPort = ((MinecraftServer)(Object)this).getServerPort();
			/*ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				localPort = server.getServerPort();
			});*/
			Engrok.LOGGER.warn("Port: " + localPort);
			switch (config.regionSelect) {
				case EU -> ngrokInit(localPort, Region.EU);
				case AP -> ngrokInit(localPort, Region.AP);
				case AU -> ngrokInit(localPort, Region.AU);
				case SA -> ngrokInit(localPort, Region.SA);
				case JP -> ngrokInit(localPort, Region.JP);
				case IN -> ngrokInit(localPort, Region.IN);
				case US -> ngrokInit(localPort, Region.US);
				default -> ngrokInit(localPort, null);
			}


		}
	}

	private void ngrokInit(int port, Region region) {

		//Defines a new threaded function to oepn the Ngrok tunnel, so that the "Open to LAN" button does not hitch - this thread runs in a seperate process from the main game loop
		Thread thread = new Thread(() ->
		{
			if (config.authToken.equals("Insert your Ngrok auth token here")) {
				// Check if authToken field has actually been changed, if not, print this text in chat
				Engrok.LOGGER.error("You need insert your Ngrok auth token in the config file in order for it to open a tunnel!\n The config file is located in the server folder, under config/engrok.json5\n You can do this in the mods folder, inside the Engrok mod config.\n You can obtain your Auth token from the Ngrok website after logging in.");
			} else {
				try {
					Engrok.LOGGER.info("Starting Ngrok Service...");

					// Java-ngrok wrapper code, to initiate the tunnel, with the authoken, region
					final JavaNgrokConfig javaNgrokConfig;

					if(region != null)
					{
						javaNgrokConfig = new JavaNgrokConfig.Builder()
								.withAuthToken(config.authToken)
								.withRegion(region)
								.withoutMonitoring()
								.build();
					}
					else {
						javaNgrokConfig = new JavaNgrokConfig.Builder()
								.withAuthToken(config.authToken)
								.withoutMonitoring()
								.build();
					}

					Engrok.ngrokClient = new NgrokClient.Builder()
							.withJavaNgrokConfig(javaNgrokConfig)
							.build();

					final CreateTunnel createTunnel = new CreateTunnel.Builder()
							.withProto(Proto.TCP)
							.withAddr(port)
							.build();

					final Tunnel tunnel = Engrok.ngrokClient.connect(createTunnel);

					//Engrok.LOGGER.info(tunnel.getPublicUrl());

					var ngrok_url = tunnel.getPublicUrl().substring(6);

					String ipText = ngrok_url;
					Engrok.LOGGER.info("Ngrok Service Initiated Successfully!");
					Engrok.LOGGER.info("Your server IP is - " + ipText);

					Engrok.tunnelOpen = true;

				} catch (Exception error) {
					error.printStackTrace();
					Engrok.LOGGER.warn(error.getMessage());
					Engrok.LOGGER.error("Ngrok Service Initiation Failed!");
					//ngrokInitiated = false;
					throw new RuntimeException("Ngrok Service Failed to Start" + error.getMessage());
				}
			}
		});

		// This starts the thread defined above
		thread.start();

	}
}