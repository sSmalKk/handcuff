package net.ssmalkk.handcuffmod.client.renderer.item;

import net.ssmalkk.handcuffmod.client.model.item.HandcuffsOpenModel;
import net.ssmalkk.handcuffmod.item.HandcuffsOpenItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HandcuffsOpenRender extends GeoItemRenderer<HandcuffsOpenItem> {
	public HandcuffsOpenRender() {
		super(new HandcuffsOpenModel());
	}
}
