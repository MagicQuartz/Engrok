package io.github.magicquartz.engrok.mixin;

import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.initialization.LoadWorldInvoker;
import io.github.magicquartz.engrok.config.EngrokConfig;
import io.github.magicquartz.engrok.integrations.GitHubGists;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Region;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class LoadWorldMixin implements LoadWorldInvoker {

	EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();

	@Inject(at = @At("HEAD"), method = "loadWorld")
	private void loadWorld(CallbackInfo callback) {
		if (config.enabled) { //if mod enabled in mod menu

			int localPort = ((MinecraftServer)(Object) this).getServerPort();
			if(localPort == 0 || localPort == -1)
				localPort = 25565;
			//Engrok.LOGGER.warn("Port: " + localPort)
			initialization(localPort, config.regionSelect);
		}
	}

	@Override
	public void initialization(int localPort ,EngrokConfig.regionSelectEnum region)
	{
		if(!Engrok.tunnelOpen)
		{
			switch (region) {
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
		//Defines a new threaded function to open the Ngrok tunnel, so that the "Open to LAN" button does not hitch - this thread runs in a separate process from the main game loop
		Engrok.canCommand = false;
		Thread thread = new Thread(() ->
		{
			if (config.ngrokAuthToken.equals("Insert your Ngrok auth token here") || config.ngrokAuthToken.isEmpty()) {
				// Check if authToken field has actually been changed, if not, print this text in chat
				Engrok.LOGGER.error("You need insert your Ngrok auth token in the config file in order for it to open a tunnel!\n The config file is located in the server folder, under config/engrok.json5\n You can do this in the mods folder, inside the Engrok mod config.\n You can obtain your Auth token from the Ngrok website after logging in.");
			} else {
				try {
					Engrok.LOGGER.info("Starting Ngrok Service...");

					// Java-ngrok wrapper code, to initiate the tunnel, with the auth token, region
					final JavaNgrokConfig javaNgrokConfig;

					if(region != null)
					{
						javaNgrokConfig = new JavaNgrokConfig.Builder()
								.withAuthToken(config.ngrokAuthToken)
								.withRegion(region)
								.withoutMonitoring()
								.build();
					}
					else {
						javaNgrokConfig = new JavaNgrokConfig.Builder()
								.withAuthToken(config.ngrokAuthToken)
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
					Engrok.LOGGER.info("Ngrok Service Initiated Successfully!");
					Engrok.LOGGER.info("Your server IP is - " + ngrok_url);
					new GitHubGists().setIpGist(ngrok_url);

					Engrok.tunnelOpen = true;
					Engrok.canCommand = true;

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