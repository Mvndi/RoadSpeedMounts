package net.mvndicraft.roadspeedmounts;

import org.bukkit.attribute.Attributable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MountsMoveListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onMountsMove(PlayerMoveEvent event) {
        Entity mounts = event.getPlayer().getVehicle();
        if (mounts != null && RoadSpeed.isPassengersAffected(mounts.getPassengers())) {
            RoadSpeedMountsPlugin.debug(() -> "Passenger affected: " + mounts.getPassengers());
            if (mounts instanceof Attributable attributable) {
                RoadSpeed.applyRoadSpeed(attributable, RoadSpeed.getRoadSpeed(mounts));
            }
        }
    }
}
