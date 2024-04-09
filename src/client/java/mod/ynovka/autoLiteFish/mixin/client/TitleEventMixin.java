package mod.ynovka.autoLiteFish.mixin.client;

import mod.ynovka.autoLiteFish.TitleMessageEvents;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class TitleEventMixin {
    @Shadow private int titleFadeInTicks;

    @Shadow private int titleRemainTicks;

    @Shadow private int titleFadeOutTicks;

    @Inject(method="setSubtitle", at = @At("RETURN"))
    private void setSubtitleEvent(Text subtitle, CallbackInfo ci) {
        TitleMessageEvents.SET_TITLE.invoker().onTitleSet(subtitle,true);
    }
    @Inject(method="setTitle", at = @At("RETURN"))
    private void setTitleEvent(Text subtitle, CallbackInfo ci) {
        TitleMessageEvents.SET_TITLE.invoker().onTitleSet(subtitle,false);
    }
    @Inject(method="setTitleTicks", at = @At("RETURN"))
    private void configureFadeEvent(int fadeInTicks, int stayTicks, int fadeOutTicks, CallbackInfo ci) {
        TitleMessageEvents.CONFIGURE_FADE.invoker().onTitleFadeConfiguration(titleFadeInTicks,titleRemainTicks,titleFadeOutTicks);
    }
    @Inject(method="setDefaultTitleFade", at=@At("RETURN"))
    private void configureDefaultFadeEvent(CallbackInfo ci) {
        TitleMessageEvents.CONFIGURE_FADE.invoker().onTitleFadeConfiguration(titleFadeInTicks,titleRemainTicks,titleFadeOutTicks);
    }

    @Inject(method="clearTitle", at = @At("RETURN"))
    private void clearTitleEvent(CallbackInfo ci) {
        TitleMessageEvents.CLEAR_TITLE.invoker().onTitleCleared();
    }


}
