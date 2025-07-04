package me.marzeq.crossbowenhanced.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.marzeq.crossbowenhanced.CrossbowEnhanced;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ConfigScreen implements ModMenuApi {
    private net.minecraft.client.gui.screen.Screen screen(net.minecraft.client.gui.screen.Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.of("Crossbow Enhanced Config"));

        Config config = CrossbowEnhanced.config;

        ConfigEntryBuilder entry = builder.entryBuilder();

        var projectileManagementRequirement = new Requirement() {
            @Override
            public boolean check() {
                return config.enableProjectileManagementFeature;
            }
        };

        builder.getOrCreateCategory(Text.of("Projectile management"))
                .addEntry(entry
                        .startBooleanToggle(Text.of("Enable feature"), config.enableProjectileManagementFeature)
                        .setDefaultValue(Defaults.enableProjectileManagementFeature)
                        .setTooltip(Text.of("This effectively replaces the vanilla projectile drawing process by automatically putting desired projectiles in your off-hand.\n" +
                                            "This has the added bonus of not having to worry about putting charged fireworks in your off-hand."))
                        .setSaveConsumer(v -> config.enableProjectileManagementFeature = v)
                        .build()
                )
                .addEntry(entry
                        .startEnumSelector(Text.of("Preferred projectile type"), Config.PREFERRED_PROJECTILE.class, config.preferredProjectile)
                        .setDefaultValue(Defaults.preferredProjectile)
                        .setEnumNameProvider(value -> switch (value) {
                            case Config.PREFERRED_PROJECTILE.FIREWORKS -> Text.of("Fireworks");
                            case Config.PREFERRED_PROJECTILE.TIPPED_ARROWS -> Text.of("Tipped arrows");
                            case Config.PREFERRED_PROJECTILE.REGULAR_ARROWS -> Text.of("Regular arrows");
                            default -> Text.of(value.toString());
                        })
                        .setTooltip(Text.of("The preferred projectile type. The mod will try and shoot with it first, and only when there's none will it move on to other ones"))
                        .setSaveConsumer(v -> config.preferredProjectile = v)
                        .setDisplayRequirement(projectileManagementRequirement)
                        .build()
                )
                .addEntry(entry
                        .startEnumSelector(Text.of("Drawing order"), Config.DRAW_ORDER.class, config.drawOrder)
                        .setDefaultValue(Defaults.order)
                        .setEnumNameProvider(value -> switch (value) {
                            case Config.DRAW_ORDER.FROM_TOP_LEFT -> Text.of("Top left to bottom right");
                            case Config.DRAW_ORDER.FROM_BOTTOM_RIGHT -> Text.of("Bottom right to top left");
                            default -> Text.of(value.toString());
                        })
                        .setTooltip(Text.of("If there are multiple slots of projectiles with equal priority, what is the order they should be drawn from"))
                        .setSaveConsumer(v -> config.drawOrder = v)
                        .setDisplayRequirement(projectileManagementRequirement)
                        .build()
                )
                .addEntry(entry
                        .startBooleanToggle(Text.of("Prioritise stacks with lower count"), config.prioritiseStacksWithLowerCount)
                        .setDefaultValue(Defaults.prioritiseStacksWithLowerCount)
                        .setTooltip(Text.of("If there are multiple slots of projectiles with equal priority, but one has a lower count, should the drawing order be ignored and it be picked instead"))
                        .setSaveConsumer(v -> config.prioritiseStacksWithLowerCount = v)
                        .setDisplayRequirement(projectileManagementRequirement)
                        .build()
                );


        builder.getOrCreateCategory(Text.of("Auto shoot"))
                .addEntry(entry
                    .startBooleanToggle(Text.of("Enable feature"), config.enableAutoShootFeature)
                    .setDefaultValue(Defaults.enableAutoShootFeature)
                    .setTooltip(Text.of("Automatically shoot the crossbow when it is fully charged and the player releases the right mouse button"))
                    .setSaveConsumer(v -> config.enableAutoShootFeature= v)
                    .build()
                );


        builder.setSavingRunnable(config::save);

        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            try {
                return screen(parent);
            } catch (Exception e) {
                Config config = CrossbowEnhanced.config;
                config.reset();
                return screen(parent);
            }
        };
    }
}