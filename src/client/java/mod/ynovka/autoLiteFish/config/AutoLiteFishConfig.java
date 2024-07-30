package mod.ynovka.autoLiteFish.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "autolitefish")
public class AutoLiteFishConfig implements ConfigData {
    public boolean autoFishEnabled = false;
    public boolean sendFeedbackEnabled = false;
}