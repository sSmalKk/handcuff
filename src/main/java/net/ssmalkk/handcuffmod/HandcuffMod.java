package net.ssmalkk.handcuffmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.ssmalkk.handcuffmod.config.ConfigHandler;
import net.ssmalkk.handcuffmod.event.HandcuffEventHandler;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HandcuffMod.MOD_ID)
public class HandcuffMod {
    public static final Logger LOGGER = LogManager.getLogger();
    public static ItemGroup handcuffModItemGroup;
    public static final String MOD_ID = "handcuffmod";

    private static final boolean isDevelopmentEnvironment = true; // Change as needed
    private static final boolean DISABLE_IN_DEV = false; // Change as needed
    private static final String DISABLE_EXAMPLES_PROPERTY_KEY = "handcuffmod.disable_examples"; // Change as needed

    public HandcuffMod() {
        GeckoLib.initialize(); // Initializes GeckoLib
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if (shouldRegister()) {
            ItemRegistry.ITEMS.register(modEventBus); // Registers mod items

            modEventBus.addListener(this::setup);

            // Defines the mod item group
            handcuffModItemGroup = new ItemGroup(ItemGroup.getGroupCountSafe(), "handcuffMod") {
                @Override
                public ItemStack createIcon() {
                    // Sets the item group icon
                    return new ItemStack(ItemRegistry.HANDCUFFSOPEN.get());
                }
            };
        }

        // Register the event handler
        MinecraftForge.EVENT_BUS.register(new HandcuffEventHandler());

        // Register ClientListener to the client mod event bus
        modEventBus.register(ClientListener.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Mod setup
    }

    // Method to determine if extra resources should be registered
    static boolean shouldRegister() {
        return isDevelopmentEnvironment && !DISABLE_IN_DEV && !Boolean.getBoolean(DISABLE_EXAMPLES_PROPERTY_KEY);
    }
}
