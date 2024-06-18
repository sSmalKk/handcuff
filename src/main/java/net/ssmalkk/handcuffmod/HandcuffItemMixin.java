package net.ssmalkk.handcuffmod;
z
import net.minecraft.entity.player.PlayerEntity;
import net.ssmalkk.handcuffmod.item.HandcuffItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HandcuffItem.class)
public class HandcuffItemMixin {

    @Unique
    private String modelState = "closed"; // Valores possíveis: "open", "closed", "divided"

    @Unique
    private PlayerEntity linkedPlayer;

    public String getModelState() {
        return modelState;
    }

    public void setModelState(String state) {
        this.modelState = state;
    }

    public PlayerEntity getLinkedPlayer() {
        return linkedPlayer;
    }

    public void setLinkedPlayer(PlayerEntity player) {
        this.linkedPlayer = player;
    }
}
