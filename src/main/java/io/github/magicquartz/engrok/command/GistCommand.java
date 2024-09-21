package io.github.magicquartz.engrok.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.integrations.GitHubGists;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.io.IOException;

public class GistCommand {

    //CommandManager
    public GistCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        dispatcher.register(CommandManager.literal("gist").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("getUrl").executes(GistCommand::getUrl)));
        dispatcher.register(CommandManager.literal("gist").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("getIp").executes(GistCommand::getIp)));
    }

    private static int getUrl(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        String gistUrl;
        try {
            gistUrl = new GitHubGists().getGistUrl();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        if(!Engrok.tunnelOpen)
            sendMessage(context, "§cPlease keep in mind that the Ngrok tunnel is currently closed, so the provided ip in the file is almost certainly inaccurate.");

        if(sender != null)
        {
            sender.sendMessage(Text.literal("§l§9[Engrok] §r§7Gist url: §a").append(Text.literal(gistUrl).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, gistUrl)))));
            sender.sendMessage(Text.literal("Make sure to share this link with the server's players so they can enter if they're using §c§n").append(Text.literal("EverChanging").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://https://modrinth.com/mod/ever-changing")))));
        } else {
            Engrok.LOGGER.info("Gist url: " + gistUrl);
            Engrok.LOGGER.info("Make sure to share this link with the server's players so they can enter if they're using EverChanging!");
        }

        return 1;

    }
    private static int getIp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        if(Engrok.tunnelOpen)
        {
            String gistUrl;
            try {
                gistUrl = new GitHubGists().getGistContent();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            sendMessage(context, "Server Ip requested from Gist: §b" + gistUrl);
            return 1;
        } else
        {
            sendMessage(context, "§cThe Ngrok tunnel is closed, gist ip will be inaccurate!");
        }
        return 0;
    }

    private static void sendMessage(CommandContext<ServerCommandSource> context, String message)
    {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        if(sender != null)
            sender.sendMessage(Text.literal("§l§9[Engrok] §r§7" + message));
        else
            Engrok.LOGGER.info(message.replaceAll("§.", ""));
    }
}
