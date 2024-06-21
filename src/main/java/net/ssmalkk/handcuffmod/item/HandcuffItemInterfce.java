package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public interface HandcuffItemInterfce {
    void onArmorTick(ItemStack stack, ServerWorld world, PlayerEntity player);
}
