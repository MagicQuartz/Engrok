package io.github.magicquartz.engrok.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class EngrokCommand {



    //CommandManager
    public EngrokCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        dispatcher.register(CommandManager.literal("engrok").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("setNgrokAuth").then(argument("token", StringArgumentType.string())).executes(EngrokCommand::setNgrokAuth)));
        dispatcher.register(CommandManager.literal("engrok").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("setGitHubAuth").then(argument("token", StringArgumentType.string())).executes(EngrokCommand::setGithubAuth)));
        dispatcher.register(CommandManager.literal("engrok").requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("setGistId").then(argument("gistId", StringArgumentType.string())).executes(EngrokCommand::setGistId)));

    }

    private static int setNgrokAuth(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();
        String newToken = StringArgumentType.getString(context, "token");
        config.ngrokAuthToken = newToken;
        if(!newToken.isEmpty())
        {
            sendMessage(context, "Successfully set Ngrok token to " + newToken);
            sendMessage(context, "Please make sure to restart the ngrok tunnel using the command /tunnel for the changes to take effect.");
        } else
        {
            sendMessage(context, "Successfully reset Ngrok token");
        }
        Engrok.configHolder.save();
        return 1;
    }

    private static int setGithubAuth(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();
        String newToken = StringArgumentType.getString(context, "token");
        config.gitHubAuthToken = newToken;
        if(!newToken.isEmpty())
        {
            sendMessage(context, "Successfully set GitHub token to " + newToken);
            sendMessage(context, "Please make sure to restart the ngrok tunnel using the command /tunnel for the changes to take effect.");
        } else
        {
            sendMessage(context, "Successfully reset GitHub token");
        }
        Engrok.configHolder.save();
        return 1;
    }

    private static int setGistId(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();
        String newId = StringArgumentType.getString(context, "gistId");
        config.gistId = newId;
        if(!newId.isEmpty())
        {
            sendMessage(context, "Successfully set Gist ID to " + newId);
            sendMessage(context, "Please make sure to restart the ngrok tunnel using the command /tunnel for the changes to take effect.");
        } else
        {
            sendMessage(context, "Successfully reset Gist ID. A new file will be created the next time the tunnel opens. To get its URL check the console after the tunnel opens or type /gist getUrl, it will be automatically saved to the config.");
        }
        Engrok.configHolder.save();
        return 1;
    }

    private static void sendMessage(CommandContext<ServerCommandSource> context, String message)
    {
        ServerPlayerEntity sender = context.getSource().getPlayer();
        if(sender != null)
            sender.sendMessage(Text.literal("§l§9[Engrok] §r§7" + message));
        else
        {
            Engrok.LOGGER.info(message.replaceAll("§", ""));
        }
    }
}
