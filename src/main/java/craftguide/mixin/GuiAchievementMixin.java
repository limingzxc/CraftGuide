package craftguide.mixin;

import craftguide.CraftGuide_MITE;
import net.minecraft.GuiAchievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiAchievement.class)
public class GuiAchievementMixin {

    @Inject(method = "updateAchievementWindow()V", at = @At("HEAD"))
    private void updateAchievementWindow(CallbackInfo info) {
        CraftGuide_MITE instance = CraftGuide_MITE.getInstance();
        instance.checkKeybind();
    }
}