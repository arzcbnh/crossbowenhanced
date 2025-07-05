package me.marzeq.crossbowenhanced.config;

import me.marzeq.crossbowenhanced.CrossbowEnhanced;
import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.Toml;
import me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;

public class Config {
    public boolean enableProjectileManagementFeature = Defaults.enableProjectileManagementFeature;
    public boolean enableAutoShootFeature = Defaults.enableAutoShootFeature;
    public DRAW_ORDER drawOrder = Defaults.order;
    public PREFERRED_PROJECTILE preferredProjectile = Defaults.preferredProjectile;
    public boolean prioritiseStacksWithLowerCount = Defaults.prioritiseStacksWithLowerCount;

    private transient File file;

    public enum DRAW_ORDER {
        FROM_TOP_LEFT,
        FROM_BOTTOM_RIGHT
    }

    public enum PREFERRED_PROJECTILE {
        FIREWORKS,
        TIPPED_ARROWS,
        REGULAR_ARROWS
    }

    private Config() { }

    public static Config load() {
        File file = new File(
            FabricLoader.getInstance().getConfigDir().toString(),
            CrossbowEnhanced.MOD_ID + ".toml"
        );

        Config config;
        if (file.exists()) {
            Toml configTOML = new Toml().read(file);
            config = configTOML.to(Config.class);
            config.file = file;
        } else {
            config = new Config();
            config.file = file;
            config.save();
        }

        return config;
    }

    public void save() {
        TomlWriter writer = new TomlWriter();
        try {
            writer.write(this, file);
        } catch (IOException e) {
            CrossbowEnhanced.LOGGER.error("Failed to write to config file");
        }
    }

    public void reset() {
        enableProjectileManagementFeature = Defaults.enableProjectileManagementFeature;
        enableAutoShootFeature = Defaults.enableAutoShootFeature;
        drawOrder = Defaults.order;
        preferredProjectile = Defaults.preferredProjectile;
        prioritiseStacksWithLowerCount = Defaults.prioritiseStacksWithLowerCount;
        save();
    }
}