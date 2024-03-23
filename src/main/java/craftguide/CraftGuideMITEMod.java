package craftguide;

import craftguide.config.Configs;
import craftguide.events.EventListeners;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.xiaoyu233.fml.AbstractMod;
import net.xiaoyu233.fml.classloading.Mod;
import net.xiaoyu233.fml.config.ConfigRegistry;
import net.xiaoyu233.fml.config.InjectionConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@Mod
public class CraftGuideMITEMod extends AbstractMod implements ModInitializer, PreLaunchEntrypoint {

    public static String modId = "modId";
    public static int modVerNum = 1000;
    public static String modVerStr = "v1.0.0";
    public static String modName = "Example Mod";

    private transient final ConfigRegistry configRegistry = new ConfigRegistry(Configs.ROOT,Configs.CONFIG_FILE);
    @Override
    public void preInit() {

    }

    @Nullable
    @Override
    public ConfigRegistry getConfigRegistry() {
        return configRegistry;
    }

    @Nonnull
    @Override
    public InjectionConfig getInjectionConfig() {
        return null;
    }

    @Override
    public void postInit() {
        EventListeners.registerAllEvents();
    }

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
    public void onInitialize() {
        EventListeners.registerAllEvents();
    }

    @Override
    public Optional<ConfigRegistry> createConfig() {
        return Optional.of(configRegistry);
    }

    @Override
    public void onPreLaunch() {

    }
}
