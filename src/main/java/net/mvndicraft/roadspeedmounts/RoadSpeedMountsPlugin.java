package net.mvndicraft.roadspeedmounts;

import co.aikar.commands.PaperCommandManager;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bstats.bukkit.Metrics;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class RoadSpeedMountsPlugin extends JavaPlugin {
    private Map<EntityType, Map<Material, Double>> speedBonusMap;
    @Override
    public void onEnable() {
        new Metrics(this, 29145);

        // Save config in our plugin data folder if it does not exist.
        saveDefaultConfig();

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new RoadSpeedMountsCommand());

        getServer().getPluginManager().registerEvents(new MountsMoveListener(), this);
    }

    public static RoadSpeedMountsPlugin getInstance() { return getPlugin(RoadSpeedMountsPlugin.class); }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        getConfig().set("enabledGameModeEnum", getConfigGameMode("enabled_gamemode"));
        speedBonusMap = getSpeedBonusMap("speed_bonus");
        getConfig().set("speedBonusMap", speedBonusMap);
        debug("enabledGameModeEnum: " + getConfig().get("enabledGameModeEnum"));
        debug("speedBonusMap: " + getConfig().get("speedBonusMap"));
    }

    public double getMaterialSpeedBonus(EntityType entityType, Material material) {
        return speedBonusMap.getOrDefault(entityType, Map.of()).getOrDefault(material, 0.0);
    }

    private Set<GameMode> getConfigGameMode(String key) {
        return getConfig().getStringList(key).stream().map(gm -> safeMatchGameMode(gm, key)).filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(GameMode.class)));
    }
    private Map<Material, Integer> getConfigMaterialsMap(String key) {
        return getConfig().getConfigurationSection(key).getKeys(false).stream().map(name -> {
            Material mat = safeMatchMaterial(name, key);
            return mat == null ? null : Map.entry(mat, getConfig().getInt(key + "." + name));
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
