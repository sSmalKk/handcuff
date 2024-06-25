package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.LivingEntity;
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
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HandcuffsItem extends GeoArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder.group(HandcuffMod.handcuffModItemGroup));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.getAnimatable() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getAnimatable();
            ItemStack chestItem = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);

            if (chestItem.getItem() instanceof HandcuffsItem) {
                boolean isLockedFromBehind = NBTUtil.isLockedFromBehind(chestItem);
                if (isLockedFromBehind) {
                    applyStatusEffects(entity);
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.handcuffs.back", ILoopType.EDefaultLoopTypes.LOOP));
                } else {
                    removeStatusEffects(entity);
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.handcuffs.front", ILoopType.EDefaultLoopTypes.LOOP));
                }
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }


    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
    }

    private void applyStatusEffects(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, Integer.MAX_VALUE, 4, false, false));
            player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, Integer.MAX_VALUE, 3, false, false));
        } else {
            entity.addPotionEffect(new EffectInstance(Effects.SLOWNESS, Integer.MAX_VALUE, 4, false, false));
        }
    }

    private void removeStatusEffects(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            player.removePotionEffect(Effects.MINING_FATIGUE);
            player.removePotionEffect(Effects.WEAKNESS);
        } else {
            entity.removePotionEffect(Effects.SLOWNESS);
        }
    }
}