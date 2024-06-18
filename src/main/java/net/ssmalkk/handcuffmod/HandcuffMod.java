package net.ssmalkk.handcuffmod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.ssmalkk.handcuffmod.registry.ItemRegistry;
import software.bernie.geckolib3.GeckoLib;

@Mod(HandcuffMod.MOD_ID)
public class HandcuffMod {
	public static ItemGroup handcuffModItemGroup;
	public static final String MOD_ID = "handcuffmod";

	public HandcuffMod() {
		GeckoLib.initialize();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ItemRegistry.ITEMS.register(bus);

		handcuffModItemGroup = new ItemGroup(ItemGroup.getGroupCountSafe(), "handcuffMod") {
			@Override
			public ItemStack createIcon() {
				return new ItemStack(ItemRegistry.HANDCUFF.get());
			}
		};
	}
}
