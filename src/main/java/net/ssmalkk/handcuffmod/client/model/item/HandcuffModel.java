package net.ssmalkk.handcuffmod.client.model.item;

import net.minecraft.util.ResourceLocation;
import net.ssmalkk.handcuffmod.client.EntityResources;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HandcuffModel extends AnimatedGeoModel<HandcuffItem> {
	@Override
	public ResourceLocation getModelLocation(HandcuffItem object) {
		return EntityResources.HANDCUFFS_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(HandcuffItem object) {
		return EntityResources.HANDCUFFS_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(HandcuffItem animatable) {
		return EntityResources.HANDCUFFS_ANIMATION;
	}
}
