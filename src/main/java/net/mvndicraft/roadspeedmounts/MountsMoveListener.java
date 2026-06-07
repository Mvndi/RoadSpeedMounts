package net.mvndicraft.roadspeedmounts;

import org.bukkit.attribute.Attributable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MountsMoveListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onMountsMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Entity mounts = player.getVehicle();
        if (mounts != null) {
            applyRoadSpeed(mounts, !RoadSpeed.isPassengersAffected(mounts.getPassengers()));
        } else {
            applyRoadSpeed(player, !RoadSpeed.isAffected(player));
        }
    }

    private void applyRoadSpeed(Entity entity, boolean toZero) {
        if (entity instanceof Attributable attributable) {
            RoadSpeedMountsPlugin.debug(() -> "Attributable affected: " + attributable);
            RoadSpeedMountsPlugin.debug(() -> "toZero: " + toZero);
            RoadSpeed.applyRoadSpeed(attributable, toZero ? 0 : RoadSpeed.getRoadSpeed(entity));
        }
    }
}
