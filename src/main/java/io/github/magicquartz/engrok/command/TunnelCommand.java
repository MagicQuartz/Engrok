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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class TunnelCommand {

    //CommandManager
    public TunnelCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        dispatcher.register(CommandManager.literal("tunnel").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("open").executes(TunnelCommand::open)));
        dispatcher.register(CommandManager.literal("tunnel").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("close").executes(TunnelCommand::close)));
        dispatcher.register(CommandManager.literal("tunnel").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("status").executes(TunnelCommand::status)));
    }

    private static int open(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();
        ServerPlayerEntity sender = context.getSource().getPlayer();

        MinecraftServer server = context.getSource().getServer();
        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.SERVER)
        {
            if(Engrok.canCommand && !Engrok.tunnelOpen)
            {
                sendMessage(context, "Ngrok tunnel opening on port §b" + server.getServerPort());
                ((LoadWorldInvoker) server).initialization(server.getServerPort(), config.regionSelect);
                return 1; // Success
            } else
                sendMessage(context, "§cTunnel already open!");
        }
        return 0;
    }

    private static int close(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        MinecraftServer server = context.getSource().getServer();
        if(MixinEnvironment.getCurrentEnvironment().getSide() == MixinEnvironment.Side.SERVER)
        {
            if(Engrok.tunnelOpen && Engrok.canCommand)
            {
                sendMessage(context, "Closing tunnel.");

                Engrok.ngrokClient.kill();
                Engrok.tunnelOpen = false;
                return 1; // Success
            }
        }

        sendMessage(context, "§cThere's no tunnel to close!");
        return 0;
    }

    private static int status(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        if(Engrok.tunnelOpen)
        {
            String ngrokIp = Engrok.ngrokClient.getTunnels().get(0).getPublicUrl().substring(6);

            sendMessage(context, "Tunnel Status: §aOpen§7.");
            sendMessage(context, "Server Ip requested from Ngrok: §b" + ngrokIp);
            return 1;
        }
        sendMessage(context, "Tunnel Status: §cClosed§7.");
        return 0;
    }

    private static void sendMessage(CommandContext<ServerCommandSource> context, String message)
    {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        if(sender != null)
            sender.sendMessage(Text.literal("§l§9[Engrok] §r§7" + message));
        else
            Engrok.LOGGER.info(message.replaceAll("§", ""));
    }
}
