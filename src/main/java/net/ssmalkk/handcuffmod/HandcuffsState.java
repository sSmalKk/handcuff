package net.ssmalkk.handcuffmod;

public enum HandcuffsState {
    OPEN, FRONT, BACK;

    public void applyEffect(PlayerEntity player) {
        switch (this) {
            case OPEN:
                // No effect
                break;
            case FRONT:
                // Apply front state effects
                break;
            case BACK:
                // Apply back state effects
                break;
        }
    }

    public String getAnimation() {
        switch (this) {
            case OPEN:
                return "open_animation";
            case FRONT:
                return "front_animation";
            case BACK:
                return "back_animation";
            default:
                return "";
        }
    }
}
