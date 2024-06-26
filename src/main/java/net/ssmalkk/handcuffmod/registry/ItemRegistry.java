package net.ssmalkk.handcuffmod.registry;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.client.renderer.item.HandcuffRender;
import net.ssmalkk.handcuffmod.client.renderer.item.HandcuffsOpenRender;
import net.ssmalkk.handcuffmod.client.renderer.item.HandcuffsRender;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import net.ssmalkk.handcuffmod.item.HandcuffsOpenItem;
import net.ssmalkk.handcuffmod.item.KeyItem;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffMod.MOD_ID);

	// Registering custom items specific to another mod
	public static final RegistryObject<HandcuffsItem> HANDCUFFS = ITEMS.register("handcuffs",
			() -> new HandcuffsItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties()));

	public static final RegistryObject<HandcuffsOpenItem> HANDCUFFSOPEN = ITEMS.register("handcuffsopen",
			() -> new HandcuffsOpenItem(new Item.Properties().group(HandcuffMod.handcuffModItemGroup).setISTER(() -> HandcuffsOpenRender::new)));

	public static final RegistryObject<HandcuffItem> HANDCUFF = ITEMS.register("handcuff",
			() -> new HandcuffItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties()));

	public static final RegistryObject<Item> KEY = ITEMS.register("key",
			() -> new KeyItem(new Item.Properties().group(HandcuffMod.handcuffModItemGroup).maxStackSize(1)));
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, HandcuffMod.MOD_ID);

	public static final RegistryObject<BasicParticleType> CHAIN_PARTICLE = PARTICLES.register("chain_particle", () -> new BasicParticleType(true));

}
