package net.ssmalkk.handcuffmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import java.net.Proxy;

@Mod(HandcuffMod.MOD_ID)
@EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class HandcuffMod {
	public static final Logger LOGGER = LogManager.getLogger();
	public static ItemGroup handcuffModItemGroup;
	public static final String MOD_ID = "handcuffmod";

	public HandcuffMod() {
		GeckoLib.initialize();
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

		handcuffModItemGroup = new ItemGroup(ItemGroup.getGroupCountSafe(), MOD_ID) {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(ItemRegistry.HANDCUFF.get());
			}
		};
	}

	@SubscribeEvent
	public static void onFMLClientSetupEvent(final FMLClientSetupEvent event) {
		try {
			YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
			YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) authService.createUserAuthentication(Agent.MINECRAFT);

			String username = "your_username_or_email";
			String password = "your_password";

			auth.setUsername(username);
			auth.setPassword(password);

			auth.logIn();

			String accessToken = auth.getAuthenticatedToken();
			String uuid = auth.getSelectedProfile().getId().toString();

			LOGGER.debug("Access Token: {}", accessToken);
			LOGGER.debug("UUID: {}", uuid);

			// Use the obtained token and UUID as needed
		} catch (AuthenticationException e) {
			LOGGER.error("Failed to authenticate with Minecraft services.", e);
		}
	}
}
