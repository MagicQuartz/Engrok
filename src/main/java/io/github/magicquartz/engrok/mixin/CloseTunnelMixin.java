package io.github.magicquartz.engrok.mixin;

import io.github.magicquartz.engrok.Engrok;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class CloseTunnelMixin {
    @Inject(at = @At("HEAD"), method = "shutdown")
    private void whenShutdownServer(CallbackInfo info)
    {
        if(Engrok.tunnelOpen)
        {
            Engrok.LOGGER.info("Closing tunnel.");
            Engrok.ngrokClient.kill();
            Engrok.tunnelOpen = false;
        }
    }
}
