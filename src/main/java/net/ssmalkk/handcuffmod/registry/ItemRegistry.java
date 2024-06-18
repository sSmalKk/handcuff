package net.ssmalkk.handcuffmod.registry;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.client.renderer.item.HandcuffRender;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.item.KeyItem;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffMod.MOD_ID);

	public static final RegistryObject<HandcuffItem> HANDCUFF = ITEMS.register("handcuffs",
			() -> new HandcuffItem(new Item.Properties().group(HandcuffMod.handcuffModItemGroup).setISTER(() -> HandcuffRender::new)));

	public static final RegistryObject<Item> KEY = ITEMS.register("key",
			() -> new KeyItem(new Item.Properties().group(HandcuffMod.handcuffModItemGroup).maxStackSize(1)));
}
