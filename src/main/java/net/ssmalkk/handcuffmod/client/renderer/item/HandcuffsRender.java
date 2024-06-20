package net.ssmalkk.handcuffmod.client.renderer.item;

import net.ssmalkk.handcuffmod.client.model.item.HandcuffsModel;
import net.ssmalkk.handcuffmod.item.HandcuffsItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HandcuffsRender extends GeoItemRenderer<HandcuffsItem> {
	public HandcuffsRender() {
		super(new HandcuffsModel());
	}
}
