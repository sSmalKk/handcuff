package net.ssmalkk.handcuffmod.registry;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.ssmalkk.handcuffmod.client.renderer.item.HandcuffRender;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import software.bernie.geckolib3.GeckoLib;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GeckoLib.ModID);



	public static final RegistryObject<HandcuffItem> HANDCUFF = ITEMS.register("handcuff",
			() -> new HandcuffItem(new Item.Properties().setISTER(() -> HandcuffRender::new)));


}
