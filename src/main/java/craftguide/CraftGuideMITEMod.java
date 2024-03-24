package craftguide;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.classloading.Mod;
import net.xiaoyu233.fml.config.InjectionConfig;

import javax.annotation.Nonnull;

@Mod
public class CraftGuideMITEMod extends AbstractMod implements ModInitializer, PreLaunchEntrypoint {
    public static String modId = "craftguide";
    public static int modVerNum = 1000;
    public static String modVerStr = "v1.0.0";
    public static String modName = "CraftGuide Mod";

    @Override
    public void preInit() {

    }

    @Nonnull
    @Override
    public InjectionConfig getInjectionConfig() {
        return null;
    }

    @Override
    public void postInit() {}

    @Override
    public String modId() {
        return modId;
    }

    @Override
    public int modVerNum() {
        return modVerNum;
    }

    @Override
    public String modVerStr() {
        return modVerStr;
    }

    @Override
    public void onInitialize() {}

    @Override
    public void onPreLaunch() {
        System.out.println("[" + modName + "] Early riser registering chat formatting");
    }
}
