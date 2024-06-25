package net.ssmalkk.handcuffmod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ssmalkk.handcuffmod.HandcuffMod;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = HandcuffMod.MOD_ID, value = Dist.CLIENT)
public class RenderHandler {

    @SubscribeEvent
    public static void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.world == null || mc.player == null) {
            return;
        }

        for (LivingEntity entity : mc.world.getEntitiesWithinAABB(LivingEntity.class, mc.player.getBoundingBox().grow(32.0))) {
            ItemStack chestItem = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            if (chestItem.getItem() instanceof HandcuffItem) {
                String lockedUUIDStr = String.valueOf(NBTUtil.getLockedEntity(chestItem));
                Vector3d lockerPos = NBTUtil.getLockerPos(chestItem);
                if (!lockedUUIDStr.isEmpty() && lockerPos != null) {
                    try {
                        UUID lockedUUID = UUID.fromString(lockedUUIDStr);
                        LivingEntity lockedEntity = findEntityByUUID(mc.world, lockedUUID);
                        if (lockedEntity != null) {
                            double distance = entity.getPositionVec().distanceTo(lockedEntity.getPositionVec());
                            if (distance <= 2.0) {
                                renderLeash(entity, lockedEntity, event.getPartialTicks());
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        // Handle invalid UUID
                    }
                }
            }
        }
    }

    private static LivingEntity findEntityByUUID(ClientWorld world, UUID uuid) {
        return world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)).stream().filter(entity -> entity.getUniqueID().equals(uuid)).findFirst().orElse(null);
    }

    private static void renderLeash(LivingEntity entity, LivingEntity target, float partialTicks) {
        Vector3d entityPos = entity.getEyePosition(partialTicks);
        Vector3d targetPos = target.getEyePosition(partialTicks);

        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();

        IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());

        Matrix4f matrix = matrixStack.getLast().getMatrix();

        builder.pos(matrix, (float) entityPos.x, (float) entityPos.y, (float) entityPos.z)
                .color(255, 255, 255, 255)
                .endVertex();
        builder.pos(matrix, (float) targetPos.x, (float) targetPos.y, (float) targetPos.z)
                .color(255, 255, 255, 255)
                .endVertex();

        buffer.finish(RenderType.getLines());
        matrixStack.pop();
    }
}
