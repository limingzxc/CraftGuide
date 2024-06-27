package craftguide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.xiaoyu233.fml.classloading.Mod;

@Mod
public class CraftGuideMITEMod implements ClientModInitializer, PreLaunchEntrypoint {
    public static String modId = "craftguide";
    public static String modName = "CraftGuide Mod";

    @Override
    public void onInitializeClient() {
    }

    @Override
    public void onPreLaunch() {
        System.out.println("[" + modName + "] Early riser registering chat formatting");
    }
}
