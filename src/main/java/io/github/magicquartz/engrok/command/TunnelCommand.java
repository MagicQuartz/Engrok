package io.github.magicquartz.engrok.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.config.EngrokConfig;
import io.github.magicquartz.engrok.initialization.LoadWorldInvoker;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class TunnelCommand {
    private static final SimpleCommandExceptionType ALREADY_OPEN_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.start.failed"));

    //CommandManager
    public TunnelCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        dispatcher.register(CommandManager.literal("tunnel").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("open").executes(TunnelCommand::open)));
        dispatcher.register(CommandManager.literal("tunnel").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("close").executes(TunnelCommand::close)));
    }

    public static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();

        MinecraftServer server = context.getSource().getServer();
        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.SERVER)
        {
            if(Engrok.canCommand|| !Engrok.tunnelOpen)
            {
                Engrok.LOGGER.info("Ngrok tunnel opening on port " + server.getServerPort());
                ((LoadWorldInvoker) server).initialization(server.getServerPort(), config.regionSelect);
                return 1; // Success
            }
        }
        return 0;
    }

    public static int close(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getServer();
        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.SERVER)
        {
            if(Engrok.tunnelOpen && Engrok.canCommand)
            {
                Engrok.LOGGER.info("Closing tunnel.");
                Engrok.ngrokClient.kill();
                Engrok.tunnelOpen = false;

            }
            return 1; // Success
        }
        return 0;
    }
}
