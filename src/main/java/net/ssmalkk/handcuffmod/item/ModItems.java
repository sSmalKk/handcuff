package net.ssmalkk.handcuffmod.item;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.ssmalkk.handcuffmod.HandcuffMod;

public class ModItems {

    public static final DeferredRegister<Item>
            ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HandcuffMod.MOD_ID);


    // Registrar o item "key"
    public static final RegistryObject<Item> KEY = ITEMS.register("key",
            () -> new Item(new Item.Properties()
                    .group(ItemGroup.TOOLS)
                    .maxStackSize(1)  // Define o máximo de itens como 1
            ));

    // Registrar o item "handcuff"
    public static final RegistryObject<Item> HANDCUFF = ITEMS.register("handcuff",
            () -> new Item(new Item.Properties()
                    .group(ItemGroup.TOOLS)
                    .maxStackSize(1)  // Define o máximo de itens como 1
            ));



    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
