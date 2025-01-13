package net.ssmalkk.handcuffmod.client.model.item;

import net.minecraft.util.ResourceLocation;
import net.ssmalkk.handcuffmod.client.EntityResources;
import net.ssmalkk.handcuffmod.item.HandcuffsOpenItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HandcuffsOpenModel extends AnimatedGeoModel<HandcuffsOpenItem> {
	@Override
	public ResourceLocation getModelLocation(HandcuffsOpenItem object) {
		return EntityResources.HANDCUFFSOPEN_MODEL;
	}

	@Override
	public ResourceLocation getTextureLocation(HandcuffsOpenItem object) {
		return EntityResources.HANDCUFFSOPEN_TEXTURE;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(HandcuffsOpenItem animatable) {
		return EntityResources.HANDCUFFSOPEN_ANIMATION;
	}
}
