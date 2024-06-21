package net.ssmalkk.handcuffmod.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HandcuffsItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsItem(Properties properties) {
        super(properties.maxStackSize(1)); // Limitar a 1 por pilha
    }

    @Override
    public void registerControllers(final AnimationData data) {
        // No need for any animation controllers in this simplified version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getHeldItem(hand);
        if (!world.isRemote) {
            // Adiciona Curse of Binding ao item
            if (!EnchantmentHelper.hasBindingCurse(itemStack)) {
                itemStack.addEnchantment(Enchantments.BINDING_CURSE, 1);
                player.sendStatusMessage(new StringTextComponent("The handcuffs have bound to you!"), true);
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack itemStack = context.getItem();
        PlayerEntity player = context.getPlayer();
        if (player != null && !context.getWorld().isRemote) {
            // Adiciona Curse of Binding ao item
            if (!EnchantmentHelper.hasBindingCurse(itemStack)) {
                itemStack.addEnchantment(Enchantments.BINDING_CURSE, 1);
                player.sendStatusMessage(new StringTextComponent("The handcuffs have bound to you!"), true);
            }
        }
        return ActionResultType.SUCCESS;
    }
}
