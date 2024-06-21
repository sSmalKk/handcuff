package net.ssmalkk.handcuffmod.client.renderer.item;

import net.ssmalkk.handcuffmod.client.model.item.HandcuffsModel;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class HandcuffsRender extends GeoArmorRenderer<HandcuffsItem> {

	public HandcuffsRender() {
		super(new HandcuffsModel());

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
