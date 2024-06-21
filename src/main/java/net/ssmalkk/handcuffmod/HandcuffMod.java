package net.ssmalkk.handcuffmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Mod(HandcuffMod.MOD_ID)
public class HandcuffMod {
	public static final Logger LOGGER = LogManager.getLogger();
	public static ItemGroup handcuffModItemGroup;
	public static final String MOD_ID = "handcuffmod";

	private static final boolean isDevelopmentEnvironment = true; // Defina conforme necessário
	private static final boolean DISABLE_IN_DEV = false; // Defina conforme necessário
	private static final String DISABLE_EXAMPLES_PROPERTY_KEY = "handcuffmod.disable_examples"; // Defina conforme necessário

	public HandcuffMod() {
		GeckoLib.initialize(); // Inicializa o GeckoLib

		if (shouldRegister()) {
			IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
			ItemRegistry.ITEMS.register(bus); // Registra os itens do mod
		}

		// Define o grupo de itens do mod
		handcuffModItemGroup = new ItemGroup(ItemGroup.getGroupCountSafe(), "handcuffMod") {
			@Override
			public ItemStack createIcon() {
				// Define o ícone do grupo de itens
				return new ItemStack(ItemRegistry.HANDCUFFSOPEN.get());
			}
		};
	}

	// Método para determinar se deve registrar recursos extras
	static boolean shouldRegister() {
		return isDevelopmentEnvironment && !DISABLE_IN_DEV && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
	}
}
