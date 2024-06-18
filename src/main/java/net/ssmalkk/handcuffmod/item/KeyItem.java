package net.ssmalkk.handcuffmod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.ssmalkk.handcuffmod.HandcuffMod;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyItem extends Item {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffMod.MOD_ID);

    // Registrar o item "key"
    public static final RegistryObject<Item> KEY = ITEMS.register("key",
            () -> new Item(new Item.Properties()
                    .group(ItemGroup.TOOLS)
                    .maxStackSize(1)  // Define o máximo de itens como 1
            ));

    public KeyItem(Properties properties) {
        super(properties);
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event) {
        // Não é necessário fazer nada aqui, pois o registro é feito via DeferredRegister
    }
}