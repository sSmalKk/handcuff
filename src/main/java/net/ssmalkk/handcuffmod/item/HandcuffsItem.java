package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.util.NBTUtil;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HandcuffsItem extends GeoArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory((IAnimatable) this);

    public HandcuffsItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder.group(HandcuffMod.handcuffModItemGroup));
    }

    private <T extends PlayerEntity & IAnimatable> PlayState predicate(AnimationEvent<T> event) {
        PlayerEntity player = event.getAnimatable();
        ItemStack chestItem = player.getItemStackFromSlot(EquipmentSlotType.CHEST);

        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isLockedFromBehind = NBTUtil.isLockedFromBehind(chestItem);
            if (isLockedFromBehind) {
                applyStatusEffects(player);
                event.getController().setAnimation(new AnimationBuilder().addAnimation("handcuffsfront", true));
            } else {
                removeStatusEffects(player);
                event.getController().setAnimation(new AnimationBuilder().addAnimation("handcuffs", true));
            }
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }


    @Override
    public void registerControllers(AnimationData data) {
    }

    private void applyStatusEffects(PlayerEntity player) {
        player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, Integer.MAX_VALUE, 4, false, false));
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, Integer.MAX_VALUE, 3, false, false));
    }

    private void removeStatusEffects(PlayerEntity player) {
        player.removePotionEffect(Effects.MINING_FATIGUE);
        player.removePotionEffect(Effects.WEAKNESS);
    }
}
