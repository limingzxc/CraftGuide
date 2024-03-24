package craftguide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class CraftGuideMITEMod implements ClientModInitializer, PreLaunchEntrypoint {
    public static String modId = "craftguide";
    public static int modVerNum = 1000;
    public static String modVerStr = "v1.0.0";
    public static String modName = "CraftGuide Mod";

    @Override
    public void onInitializeClient() {}

    @Override
    public void onPreLaunch() {
        System.out.println("[" + modName + "] Early riser registering chat formatting");
    }
}
