package mod.ynovka.autoLiteFish;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;

public class TitleMessageEvents {
    public static final Event<TitleSetEvent> SET_TITLE = EventFactory.createArrayBacked(TitleSetEvent.class, handlers -> (text, subtitle) -> {
        for (TitleSetEvent handler : handlers) {
            handler.onTitleSet(text,subtitle);
        }
    });

    public static final Event<FadeConfigurationEvent> CONFIGURE_FADE = EventFactory.createArrayBacked(FadeConfigurationEvent.class, handlers -> (fadeInTime, displayTime, fadeOutTime) -> {
        for (FadeConfigurationEvent handler : handlers) {
            handler.onTitleFadeConfiguration(fadeInTime, displayTime, fadeOutTime);
        }
    });

    public static final Event<TitleClearedEvent> CLEAR_TITLE = EventFactory.createArrayBacked(TitleClearedEvent.class, handlers -> () -> {
        for (TitleClearedEvent handler : handlers) {
            handler.onTitleCleared();
        }
    });


    @FunctionalInterface
    public interface TitleSetEvent {
        void onTitleSet(Text text, boolean isSubTitle);
    }
    @FunctionalInterface
    public interface FadeConfigurationEvent {
        void onTitleFadeConfiguration(int fadeInTime, int displayTime, int fadeOutTime);
    }
    @FunctionalInterface
    public interface TitleClearedEvent {
        void onTitleCleared();
    }

}
