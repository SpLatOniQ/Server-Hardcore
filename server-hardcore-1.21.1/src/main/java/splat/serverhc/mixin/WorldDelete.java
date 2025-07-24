package splat.serverhc.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import splat.serverhc.config.ServerHCConfig;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

@Mixin(ServerPlayerEntity.class)
public class WorldDelete {
    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathMixin(CallbackInfo ci) {
        ServerPlayerEntity PLAYER = (ServerPlayerEntity) (Object) this;
        MinecraftServer SERVER = PLAYER.getServer();

        if (!SERVER.getSaveProperties().isHardcore()) {
            LOGGER.warn("World is not in hardcore! Check level.dat or server properties.");
            return;
        }

        String PLAYER_NAME = PLAYER.getName().getString();
        String KICK_MESSAGE;

        if (ServerHCConfig.ANONYMOUS_DEATHS) {
            KICK_MESSAGE = "A Player" + ServerHCConfig.CUSTOM_KICK_MESSAGE;
        }
        else {
            KICK_MESSAGE = PLAYER_NAME + ServerHCConfig.CUSTOM_KICK_MESSAGE;
        }

        Text TEXT_KICK_MSG = Text.of(KICK_MESSAGE);

        for (ServerPlayerEntity onlinePlayer : SERVER.getPlayerManager().getPlayerList()) {
            onlinePlayer.networkHandler.disconnect(TEXT_KICK_MSG);
        }

        LOGGER.info(PLAYER_NAME + " has died! Restarting server...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                String LEVEL_NAME = SERVER.getSaveProperties().getLevelName();
                File WORLD_FOLDER = new File(LEVEL_NAME);

                if (WORLD_FOLDER.exists()) {
                    FileUtils.deleteDirectory(WORLD_FOLDER);
                }

                if (!ServerHCConfig.START_BATCH_PATH.isEmpty()) {
                    String OS_NAME = System.getProperty("os.name").toLowerCase();
                    String COMMAND;

                    if (OS_NAME.contains("win")) {
                        if (ServerHCConfig.NEW_TERMINAL_ON_RESTART) {
                            COMMAND = "cmd /c start " + ServerHCConfig.START_BATCH_PATH;
                        } else {
                            COMMAND = ServerHCConfig.START_BATCH_PATH;
                        }
                    }
                    else {
                        if (ServerHCConfig.NEW_TERMINAL_ON_RESTART) {
                            COMMAND = "gnome-terminal -- " + ServerHCConfig.START_BATCH_PATH;
                        } else {
                            COMMAND = "sh " + ServerHCConfig.START_BATCH_PATH;
                        }
                    }

                    Runtime.getRuntime().exec(COMMAND);
                    LOGGER.info("Server restarted!");
                }
                else {
                    LOGGER.warn("No start script configured. Server stopped without restarting.");
                }
            } catch (IOException e) {
                LOGGER.error("Failed to restart the server!", e);
            }
        }));

        SERVER.stop(false);
    }
}