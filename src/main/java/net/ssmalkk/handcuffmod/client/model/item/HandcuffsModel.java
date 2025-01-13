package net.ssmalkk.handcuffmod.client.model.item;

import net.minecraft.util.ResourceLocation;
import net.ssmalkk.handcuffmod.client.EntityResources;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HandcuffsModel extends AnimatedGeoModel<HandcuffsItem> {
	@Override
	public ResourceLocation getModelLocation(HandcuffsItem object) {
		return EntityResources.HANDCUFFS_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(HandcuffsItem object) {
		return EntityResources.HANDCUFFS_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(HandcuffsItem animatable) {
		return EntityResources.HANDCUFFS_ANIMATION;
	}
}
