package net.mvndicraft.roadspeedmounts.handlers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;

public class TownyHandler {
    private TownyHandler() {}
    public static boolean isUnruinedTown(Location location) {
        Town town = TownyAPI.getInstance().getTown(location);
        return town != null && !town.isRuined();
    }
}
