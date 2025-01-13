package net.ssmalkk.handcuffmod.client.renderer.item;

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
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.ssmalkk.handcuffmod.client.model.item.HandcuffModel;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import net.ssmalkk.handcuffmod.util.NBTUtil;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.UUID;

public class HandcuffRender extends GeoArmorRenderer<HandcuffItem> {

	public HandcuffRender() {
		super(new HandcuffModel());

		this.headBone = "helmet";
		this.bodyBone = "chestplate";
		this.rightArmBone = "rightArm";
		this.leftArmBone = "leftArm";
		this.rightLegBone = "rightLeg";
		this.leftLegBone = "leftLeg";
		this.rightBootBone = "rightBoot";
		this.leftBootBone = "leftBoot";
	}

}
