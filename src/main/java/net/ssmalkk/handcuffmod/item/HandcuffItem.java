package net.ssmalkk.handcuffmod.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class HandcuffItem extends Item implements IAnimatable {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public HandcuffItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(final AnimationData data) {
        // No need for any animation controllers in this simplified version
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
