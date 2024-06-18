package net.ssmalkk.handcuffmod.client.renderer.item;

import net.ssmalkk.handcuffmod.client.model.item.HandcuffModel;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HandcuffRender extends GeoItemRenderer<HandcuffItem> {
	public HandcuffRender() {
		super(new HandcuffModel());
	}
}
