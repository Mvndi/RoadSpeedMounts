package net.mvndicraft.roadspeedmounts;

import co.aikar.commands.PaperCommandManager;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.mvndicraft.roadspeedmounts.handlers.TownyHandler;
import net.mvndicraft.roadspeedmounts.handlers.TownyRoadsHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class RoadSpeedMountsPlugin extends JavaPlugin {
    private Map<EntityType, Map<Material, Double>> speedBonusMap;
    private Map<EntityType, Map<Material, Double>> townySpeedBonusMap;
    private Map<EntityType, Map<Material, Double>> townyRoadsSpeedBonusMap;
    private boolean townyEnabled;
    private boolean townyRoadsEnabled;
    private boolean yLevelTestEnabled;
    @Override
    public void onEnable() {
        new Metrics(this, 29145);
        initPluginBoolean();

        // Save config in our plugin data folder if it does not exist.
        saveDefaultConfig();


        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new RoadSpeedMountsCommand());

        getServer().getPluginManager().registerEvents(new MountsMoveListener(), this);
    }

    private void initPluginBoolean() {
        Plugin towny = getServer().getPluginManager().getPlugin("Towny");
        townyEnabled = (towny != null && towny.isEnabled());
        debug("townyEnabled: " + townyEnabled);

        if (townyEnabled) {
            Plugin townyRoads = getServer().getPluginManager().getPlugin("TownyRoads");
            townyRoadsEnabled = (townyRoads != null && townyRoads.isEnabled());
        }
        debug("townyRoadsEnabled: " + townyRoadsEnabled);
    }

    public static RoadSpeedMountsPlugin getInstance() { return getPlugin(RoadSpeedMountsPlugin.class); }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        initPluginBoolean();
        getConfig().set("enabledGameModeEnum", getConfigGameMode("enabled_gamemode"));
        speedBonusMap = getSpeedBonusMap("speed_bonus");
        if (townyEnabled) {
            townySpeedBonusMap = getSpeedBonusMap("towny_speed_bonus");
            if (townyRoadsEnabled) {
                townyRoadsSpeedBonusMap = getSpeedBonusMap("towny_roads_speed_bonus");
            }
        }
        yLevelTestEnabled = getMinYLevel() != Integer.MIN_VALUE || getMaxYLevel() != Integer.MAX_VALUE;
        debug("enabledGameModeEnum: " + getConfig().get("enabledGameModeEnum"));
        debug("speedBonusMap: " + speedBonusMap);
        debug("townySpeedBonusMap: " + townySpeedBonusMap);
        debug("townyRoadsSpeedBonusMap: " + townyRoadsSpeedBonusMap);
        debug("yLevelTestEnabled: " + yLevelTestEnabled);
        debug("minYLevel: " + getMinYLevel());
        debug("maxYLevel: " + getMaxYLevel());
        debug("isSpeedBonusWhenJumping: " + isSpeedBonusWhenJumping());
    }

    /**
     * Get the speed bonus for the first found material or 0 if not found
     */
    public double getMaterialSpeedBonus(EntityType entityType, List<Material> materials, Location location) {
        Map<Material, Double> speedMap = getSpeedMap(entityType, location);
        for (Material material : materials) {
            Double value = speedMap.get(material);
            if (value != null) {
                return value;
            }
        }
        return 0.0D;
    }
    public Map<Material, Double> getSpeedMap(EntityType entityType, Location location) {
        if (townyEnabled && townySpeedBonusMap != null && TownyHandler.isUnruinedTown(location)) {
            debug("isUnruinedTown: true");
            return townySpeedBonusMap.getOrDefault(entityType, Map.of());
        } else if (townyEnabled && townyRoadsEnabled && townyRoadsSpeedBonusMap != null && TownyRoadsHandler.isValidRoad(location)) {
            debug("isValidRoad: true");
            return townyRoadsSpeedBonusMap.getOrDefault(entityType, Map.of());
        } else {
            debug("wilderness speed: true");
            return speedBonusMap.getOrDefault(entityType, Map.of());
        }
    }

    public double getMaterialSpeedBonus(EntityType entityType, Material material, Location location) {
        return getMaterialSpeedBonus(entityType, List.of(material), location);
    }

    private Set<GameMode> getConfigGameMode(String key) {
        return getConfig().getStringList(key).stream().map(gm -> safeMatchGameMode(gm, key)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(GameMode.class)));
    }
    private Map<Material, Double> getConfigMaterialsMap(String key) {
        return getConfig().getConfigurationSection(key).getKeys(false).stream().map(name -> {
            Material mat = safeMatchMaterial(name, key);
            return mat == null ? null : Map.entry(mat, getConfig().getDouble(key + "." + name));
        }).filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, () -> new EnumMap<>(Material.class)));
    }

    @Nullable
    private Material safeMatchMaterial(String name, String key) {
        Material mat = Material.matchMaterial(name);
        if (mat == null) {
            getLogger().warning(() -> "Invalid material in config at '" + key + "': " + name);
        }
        return mat;
    }
    @Nullable
    private GameMode safeMatchGameMode(String name, String key) {
        try {
            return GameMode.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            getLogger().warning(() -> "Invalid GameMode in config at '" + key + "': " + name);
            return null;
        }
    }

    private Map<EntityType, Map<Material, Double>> getSpeedBonusMap(String key) {
        Map<EntityType, Map<Material, Double>> result = new EnumMap<>(EntityType.class);

        ConfigurationSection root = getConfig().getConfigurationSection(key);
        if (root == null) {
            warning("Invalid config section: " + key);
            return result;
        }

        for (String entityKey : root.getKeys(false)) {
            entityKey = entityKey.toUpperCase();
            EntityType entityType;

            try {
                entityType = EntityType.valueOf(entityKey);
            } catch (IllegalArgumentException e) {
                warning("Invalid entity type in config: " + entityKey);
                continue;
            }

            result.put(entityType, getConfigMaterialsMap(key + "." + entityKey));
        }

        return result;
    }

    public boolean getYLevelTestEnabled() { return yLevelTestEnabled; }
    public int getMaxYLevel() { return getConfig().getInt("max_y", Integer.MAX_VALUE); }
    public int getMinYLevel() { return getConfig().getInt("min_y", Integer.MIN_VALUE); }
    public boolean isSpeedBonusWhenJumping() { return getConfig().getBoolean("speed_bonus_when_jumping", true); }


    // Usual log with debug level
    public static void log(Level level, String message) { getInstance().getLogger().log(level, message); }
    public static void log(Level level, Supplier<String> messageProvider) { getInstance().getLogger().log(level, messageProvider); }
    public static void log(Level level, String message, Throwable e) { getInstance().getLogger().log(level, message, e); }
    public static void debug(String message) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, message);
        }
    }
    public static void debug(Supplier<String> messageProvider) {
        if (getInstance().getConfig().getBoolean("debug", false)) {
            log(Level.INFO, messageProvider);
        }
    }
    public static void info(String message) { log(Level.INFO, message); }
    public static void info(String message, Throwable e) { log(Level.INFO, message, e); }
    public static void warning(String message) { log(Level.WARNING, message); }
    public static void warning(String message, Throwable e) { log(Level.WARNING, message, e); }
    public static void error(String message) { log(Level.SEVERE, message); }
    public static void error(String message, Throwable e) { log(Level.SEVERE, message, e); }
}
