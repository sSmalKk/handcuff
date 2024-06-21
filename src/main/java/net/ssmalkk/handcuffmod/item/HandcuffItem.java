package net.ssmalkk.handcuffmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.ssmalkk.handcuffmod.HandcuffMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;

public class HandcuffItem extends GeoArmorItem implements HandcuffItemInterfce, IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory((IAnimatable) this);

    public HandcuffItem(ArmorMaterial diamond, EquipmentSlotType chest, Properties builder) {
        super(HandcuffMod.handcuffMaterial, EquipmentSlotType.CHEST, builder.group(HandcuffMod.handcuffModItemGroup));
    }

    @Override
    public void onArmorTick(ItemStack stack, ServerWorld world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);

        if (!world.isRemote()) {
            BlockPos blockPos = player.getPosition();
            Vector3d lookVec = player.getLookVec();
            BlockPos knotPos = blockPos.offset(player.getHorizontalFacing());

            // Check if there is already a leash knot at the targeted position
            if (!world.isAirBlock(knotPos) || !world.isAirBlock(knotPos.up())) {
                return;
            }

            // Find the entity to be leashed (for example, a nearby animal)
            Entity leashedEntity = findEntityToLeash(world, blockPos, player);

            if (leashedEntity != null) {
                // Create a new leash knot entity
                LeashKnotEntity leashKnot = LeashKnotEntity.create(world, knotPos);

                // Leash the entity to the leash knot
                leashedEntity.startRiding(leashKnot, true);

                // Teleport the player 1.5 blocks ahead in the facing direction
                Vector3d newPos = player.getPositionVec().add(lookVec.scale(1.5));
                player.setPosition(newPos.x, newPos.y, newPos.z);

                // Spawn the leash knot entity in the world
                world.addEntity(leashKnot);
            }
        }
    }

    // Example method to find a nearby entity to leash
    private Entity findEntityToLeash(ServerWorld world, BlockPos blockPos, PlayerEntity player) {
        // Replace this with your own logic to find the appropriate entity
        // For example, find a nearby animal entity
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos).grow(10.0));
        for (Entity entity : entities) {
            if (entity instanceof AnimalEntity && entity.isAlive()) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {

    }

    @Override
    public AnimationFactory getFactory() {
        return null;
    }
}
