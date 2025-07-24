package splat.serverhc;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import splat.serverhc.config.ServerHCConfig;

public class ServerHardcore implements DedicatedServerModInitializer {
	public static final String MOD_ID = "serverhc";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeServer() {
		ServerHCConfig.loadConfig();
		LOGGER.info("Server Hardcore started");
		LOGGER.info("May your frustrations because of your friends begin!");
	}
}