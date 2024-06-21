package net.ssmalkk.handcuffmod.client.renderer.item;

import net.ssmalkk.handcuffmod.client.model.item.HandcuffModel;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

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
