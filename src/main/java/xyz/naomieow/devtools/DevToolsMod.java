package xyz.naomieow.devtools;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevToolsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("devtools");

	@Override
	public void onInitialize() {
		LOGGER.info("DevTools initialized!");
	}
}
