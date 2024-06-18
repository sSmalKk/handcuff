// src/main/java/com/example/handcuffs/Handcuffs.java
package net.ssmalkk.handcuffmod;

public class Handcuffs {
    private HandcuffsState state;

    public Handcuffs() {
        this.state = new HandcuffsOpen(); // Estado inicial
    }

    public void setState(HandcuffsState state) {
        this.state = state;
    }

    public HandcuffsState getState() {
        return state;
    }

    public void applyEffect(PlayerEntity player) {
        state.applyEffect(player);
    }

    public void removeEffect(PlayerEntity player) {
        state.removeEffect(player);
    }

    public String getAnimation() {
        return state.getAnimation();
    }
}
