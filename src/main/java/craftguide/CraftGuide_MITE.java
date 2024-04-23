package craftguide;

import net.minecraft.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import uristqwerty.CraftGuide.CommonUtilities;
import uristqwerty.CraftGuide.CraftGuide;
import uristqwerty.CraftGuide.CraftGuideLoaderSide;
import uristqwerty.CraftGuide.GuiCraftGuide;
import uristqwerty.CraftGuide.client.mite.CraftGuideClient_MITE;

import java.io.File;
import java.util.Arrays;

public class CraftGuide_MITE implements CraftGuideLoaderSide
{
    public static CraftGuide_MITE instance;
    private CraftGuide craftguide;
    public KeyBinding keyBinding;
    public static RenderEngine renderEngine = new RenderEngine();

    public static CraftGuide_MITE getInstance() {
        if(instance == null) {
            instance = new CraftGuide_MITE();
            instance.load();
        }
        return instance;
    }

    @Override
    public boolean isModLoaded(String name)
    {
        if(name.equals("mite"))
        {
            return true;
//			return isClassLoaded(getClassPackagePrefix()+"FCBetterThanWolves");
        }
        else if(name.equals("Forge"))
        {
            return isClassLoaded("net.minecraftforge.oredict.ShapedOreRecipe");
        }

        return false;
    }

    private String getClassPackagePrefix() {
        try {
            Class.forName("FCBetterThanWolves");
            return "";
        }
        catch(Throwable ignored) {}
        try {
            Class.forName("net.minecraft.src.FCBetterThanWolves");
            return "net.minecraft.src.";
        }
        catch(Throwable ignored) {}
        return "";
    }

    private boolean isClassLoaded(String classname)
    {
        try
        {
            Class c = Class.forName(classname);
            return c != null;
        }
        catch(ClassNotFoundException e)
        {
            return false;
        }
    }

    @Override
    public File getConfigDir()
    {
        return new File(Minecraft.theMinecraft.mcDataDir, "configs");
    }

    @Override
    public void addRecipe(ItemStack itemStack, Object[] objects)
    {
        CraftingManager.getInstance().addRecipe(itemStack, objects);
    }

    @Override
    public void addName(Item item, String name)
    {
        //ModLoader.addName(item, name);
    }

    @Override
    public void logConsole(String text)
    {
        System.out.println(text);
    }

    @Override
    public void logConsole(String text, Throwable e)
    {
        System.out.println(text);
        e.printStackTrace();
    }

    public void load()
    {
        CraftGuide.loaderSide = this;
        CraftGuide.side = new CraftGuideClient_MITE();
        craftguide = new CraftGuide();
        craftguide.preInit();
        craftguide.init();
    }

    @Override
    public void initClientNetworkChannels()
    {
        //ModLoader.registerPacketChannel(this, "BWR|VC");
        //ModLoader.registerPacketChannel(this, "craftguide");
        //ModLoader.registerPacketChannel(this, "CftGde");
        //ModLoader.registerPacketChannel(this, "CG");
    }

    public void initKeybind()
    {
        keyBinding = new KeyBinding(StatCollector.translateToLocal("key.craftguide.open"), Keyboard.KEY_G);
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        KeyBinding[] keyBindings = settings.keyBindings;
        keyBindings = Arrays.copyOf(keyBindings, keyBindings.length + 1);
        keyBindings[keyBindings.length - 1] = keyBinding;
        settings.keyBindings = keyBindings;
    }

    public void checkKeybind()
    {
        if(Keyboard.isKeyDown(keyBinding.keyCode) && CraftGuide.enableKeybind)
        {
            Minecraft mc = Minecraft.getMinecraft();
            GuiScreen screen = mc.currentScreen;

            if(screen == null) {
                CraftGuide.side.openGUI(mc.thePlayer);
            }
            else if(screen instanceof GuiContainer)
            {
                try
                {
                    int x = Mouse.getX() * screen.width / mc.displayWidth;
                    int y = screen.height - (Mouse.getY() * screen.height / mc.displayHeight) - 1;
                    int left = (Integer)CommonUtilities.getPrivateValue(GuiContainer.class, (GuiContainer)screen, "e", "n", "field_74198_m", "guiLeft", "field_1350");
                    int top = (Integer)CommonUtilities.getPrivateValue(GuiContainer.class, (GuiContainer)screen, "o", "field_74197_n", "guiTop", "field_1351");
                    openRecipe((GuiContainer)screen, x - left, y - top);
                }
                catch(IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void openRecipe(GuiContainer screen, int x, int y)
    {
        Container container = screen.inventorySlots;

        for(int i = 0; i < container.inventorySlots.size(); i++)
        {
            Slot slot = (Slot)container.inventorySlots.get(i);
            if(x > slot.xDisplayPosition - 2 && x < slot.xDisplayPosition + 17 && y > slot.yDisplayPosition - 2 && y < slot.yDisplayPosition + 17)
            {
                ItemStack item = slot.getStack();

                if(item != null)
                {
                    Minecraft mc = Minecraft.getMinecraft();
                    GuiCraftGuide.getInstance().setFilterItem(item);
                    CraftGuide.side.openGUI(mc.thePlayer);
                }

                break;
            }
        }
    }
}