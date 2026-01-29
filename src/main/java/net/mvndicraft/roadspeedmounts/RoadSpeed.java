package net.mvndicraft.roadspeedmounts;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class RoadSpeed {
    private static final NamespacedKey ROAD_SPEED_KEY = new NamespacedKey(RoadSpeedMountsPlugin.getInstance(), "road_speed");
    private RoadSpeed() {}

    public static void applyRoadSpeed(Attributable entity, double speedBoost) {
        AttributeInstance attributeInstance = entity.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attributeInstance != null) {
            AttributeModifier attributeModifier = attributeInstance.getModifier(ROAD_SPEED_KEY);
            if (attributeModifier != null && speedBoost == 0) {
                // if it was set but is now 0
                if (attributeModifier != null) {
                    RoadSpeedMountsPlugin.debug("Road speed modifier removed");
                    attributeInstance.removeModifier(ROAD_SPEED_KEY);
                }
            } else if (attributeModifier == null || attributeModifier.getAmount() != speedBoost) {
                RoadSpeedMountsPlugin.debug(() -> "Road speed modifier set to " + speedBoost);
                if (attributeModifier != null) {
                    attributeInstance.removeModifier(ROAD_SPEED_KEY);
                }
                // If it was never set or does not match the current speed.
                attributeInstance
                        .addModifier(new AttributeModifier(ROAD_SPEED_KEY, speedBoost, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
            }
        }
    }

    public static boolean isAffected(@Nullable Player player) {
        return player != null && RoadSpeedMountsPlugin.getInstance().getConfig().getObject("enabledGameModeEnum", EnumSet.class)
                .contains(player.getGameMode());
    }

    /**
     * Is there at least one player with the right gamemode in the passengers?
     */
    public static boolean isPassengersAffected(List<Entity> passengers) {
        return passengers != null && passengers.stream().anyMatch(entity -> (entity instanceof Player player) && isAffected(player));
    }

    public static double getRoadSpeed(Entity mounts) {
        Material material = mounts.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        EntityType entityType = mounts.getType();

        double speed = RoadSpeedMountsPlugin.getInstance().getMaterialSpeedBonus(entityType, material);
        RoadSpeedMountsPlugin.debug(() -> "Road speed for " + entityType + " and " + material + " is " + speed);
        return speed;
    }
}
