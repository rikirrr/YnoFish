package mod.ynovka.autoLiteFish;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import mod.ynovka.autoLiteFish.config.AutoLiteFishConfig;

public class AutoLiteFishModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable("title.autolitefish.config"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            AutoLiteFishConfig config = AutoConfig.getConfigHolder(AutoLiteFishConfig.class).getConfig();

            builder.getOrCreateCategory(Text.translatable("category.autolitefish.general"))
                    .addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.autolitefish.autoFishEnabled"), config.autoFishEnabled)
                            .setDefaultValue(false)
                            .setTooltip(Text.translatable("tooltip.autolitefish.autoFishEnabled"))
                            .setSaveConsumer(newValue -> config.autoFishEnabled = newValue)
                            .build())
                    .addEntry(entryBuilder.startBooleanToggle(Text.translatable("option.autolitefish.sendFeedbackEnabled"), config.sendFeedbackEnabled)
                            .setDefaultValue(true)
                            .setTooltip(Text.translatable("tooltip.autolitefish.sendFeedbackEnabled"))
                            .setSaveConsumer(newValue -> config.sendFeedbackEnabled = newValue)
                            .build());

            builder.setSavingRunnable(() -> {
                AutoConfig.getConfigHolder(AutoLiteFishConfig.class).save();
            });

            return builder.build();
        };
    }
}
