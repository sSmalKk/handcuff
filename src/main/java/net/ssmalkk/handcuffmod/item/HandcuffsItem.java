package net.ssmalkk.handcuffmod.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.util.NBTUtil;

public class HandcuffsItem extends GeoArmorItem implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffsItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
        super(materialIn, slot, builder.group(HandcuffMod.handcuffModItemGroup));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        LivingEntity livingEntity = event.getExtraDataOfType(LivingEntity.class).get(0);

        // Configurar a animação apropriada
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.handcuffs.back", EDefaultLoopTypes.LOOP));

        // Sempre continuar se for um ArmorStand
        if (livingEntity instanceof ArmorStandEntity) {
            return PlayState.CONTINUE;
        }

        // Verificar se o item está no slot correto
        ItemStack chestItem = livingEntity.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (chestItem.getItem() instanceof HandcuffsItem) {
            boolean isLockedFromBehind = NBTUtil.isLockedFromBehind(chestItem);
            if (isLockedFromBehind) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.handcuffs.back", EDefaultLoopTypes.LOOP));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.handcuffs.front", EDefaultLoopTypes.LOOP));
            }
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<HandcuffsItem>(this, "controller", 20, this::predicate));
    }
}
