package uristqwerty.CraftGuide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import uristqwerty.CraftGuide.RecipeGeneratorImplementation.RecipeGeneratorForgeExtension;
import uristqwerty.CraftGuide.api.ItemSlot;

public class CraftGuide
{
	public static CraftGuideSide side;
	public static CraftGuideLoaderSide loaderSide;

	public static ItemCraftGuide itemCraftGuide;
	private static final Properties config = new Properties();

	public static int resizeRate;
	public static int mouseWheelScrollRate;
	public static boolean pauseWhileOpen = true;
	public static boolean gridPacking = true;
	public static boolean alwaysShowID = false;
	public static boolean textSearchRequiresShift = false;
	public static boolean enableKeybind = true;
	public static boolean newerBackgroundStyle = false;
	public static boolean hideMundanePotionRecipes = true;
	public static boolean insertBetterWithRenewablesRecipes = false;

	public static boolean betterWithRenewablesDetected = false;
	public static boolean needsRecipeRefresh = false;

	private int itemCraftGuideID = 23361;

	public static final int Subtype_WILDCARD = 32767;

	public void preInit()
	{
		CraftGuideLog.init(new File(configDirectory(), "CraftGuide.log"));

		if(loaderSide.isModLoaded("Forge"))
		{
			try
			{
				RecipeGeneratorImplementation.forgeExt = (RecipeGeneratorForgeExtension)Class.forName("uristqwerty.CraftGuide.ForgeStuff").newInstance();
			}
			catch(InstantiationException | ClassNotFoundException | IllegalAccessException e)
			{
				CraftGuideLog.log(e);
			}
        }

		side.preInit();
		ItemSlot.implementation = new ItemSlotImplementationImplementation();

		loadProperties();

		if(enableKeybind)
		{
			side.initKeybind();
		}
	}

	public void init()
	{
		addItems();

		try
		{
			Class.forName("uristqwerty.CraftGuide.recipes.DefaultRecipeProvider").getDeclaredConstructor().newInstance();
			Class.forName("uristqwerty.CraftGuide.recipes.BrewingRecipes").getDeclaredConstructor().newInstance();
		}
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			CraftGuideLog.log(e);
		} catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

//		loadModRecipes("MITE", "uristqwerty.CraftGuide.recipes.MITERecipes");
//      loadModRecipes("BTW", "uristqwerty.CraftGuide.recipes.BTWRecipes");
//		loadModRecipes("IC2", "uristqwerty.CraftGuide.recipes.IC2Recipes");
//		loadModRecipes("GregTech_Addon", "uristqwerty.CraftGuide.recipes.GregTechRecipes");
//		loadModRecipes("extendedWorkbench", "uristqwerty.CraftGuide.recipes.ExtendedWorkbench");

		side.initNetworkChannels();
	}

	private void loadModRecipes(String modID, String recipeClass)
	{
		if(loaderSide.isModLoaded(modID))
		{
			try
			{
				Class.forName(recipeClass).getDeclaredConstructor().newInstance();
			}
			catch(ClassNotFoundException e)
			{
				CraftGuideLog.log(e);
			}
			catch(Exception e)
			{
				CraftGuideLog.log(e, "", true);
			}
		}
	}

	private void addItems()
	{
		itemCraftGuide = new ItemCraftGuide(itemCraftGuideID, "A");
		loaderSide.addName(itemCraftGuide, "Crafting Guide");

		loaderSide.addRecipe(new ItemStack(itemCraftGuide), new Object[] {"pbp",
				"bcb", "pbp", 'c', Block.workbench,
                'p', Item.paper, 'b',
				Item.book });
	}

	private void setConfigDefaults()
	{
		config.setProperty("itemCraftGuideID", "23361");
		config.setProperty("RecipeList_mouseWheelScrollRate", "3");
		config.setProperty("PauseWhileOpen", Boolean.toString(true));
		config.setProperty("resizeRate", "0");
		config.setProperty("gridPacking", Boolean.toString(true));
		config.setProperty("alwaysShowID", Boolean.toString(false));
		config.setProperty("textSearchRequiresShift", Boolean.toString(false));
		config.setProperty("enableKeybind", Boolean.toString(true));
		config.setProperty("newerBackgroundStyle", Boolean.toString(false));
		config.setProperty("hideMundanePotionRecipes", Boolean.toString(true));
		config.setProperty("insertBetterWithRenewablesRecipes", Boolean.toString(false));
	}

	/**
	 * Load configuration. If a configuration file exists in the new
	 * location, load from there. If not, but one exists in the old
	 * location, use that instead. If neither exists, just use the
	 * defaults.
	 *
	 * Afterwards, save it back to the new configuration directory
	 * (to create it if it doesn't exist, or to update it if it was
	 * created by an earlier version of CraftGuide that didn't have
	 * exactly the same set of properties).
	 */
	private void loadProperties()
	{
		File oldConfigDir = loaderSide.getConfigDir();
		File oldConfigFile = new File(oldConfigDir, "CraftGuide.cfg");
		File newConfigDir = configDirectory();
		File newConfigFile = newConfigDir == null? null : new File(newConfigDir, "CraftGuide.cfg");
		File configFile = null;

		if(newConfigFile != null && newConfigFile.exists())
		{
			configFile = newConfigFile;
		}
		else if(oldConfigFile.exists() && oldConfigFile.canRead())
		{
			configFile = oldConfigFile;
		}

		setConfigDefaults();

		if(configFile != null && configFile.exists() && configFile.canRead())
		{
			try
			{
				config.load(new FileInputStream(configFile));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			itemCraftGuideID = Integer.parseInt(config.getProperty("itemCraftGuideID"));
		}
		catch(NumberFormatException ignored)
		{
		}

		try
		{
			resizeRate = Integer.parseInt(config.getProperty("resizeRate"));
		}
		catch(NumberFormatException ignored)
		{
		}

		try
		{
			mouseWheelScrollRate = Integer.parseInt(config
					.getProperty("RecipeList_mouseWheelScrollRate"));
		}
		catch(NumberFormatException ignored)
		{
		}

		pauseWhileOpen = Boolean.parseBoolean(config.getProperty("PauseWhileOpen"));
		gridPacking = Boolean.parseBoolean(config.getProperty("gridPacking"));
		alwaysShowID = Boolean.parseBoolean(config.getProperty("alwaysShowID"));
		textSearchRequiresShift = Boolean.parseBoolean(config.getProperty("textSearchRequiresShift"));
		enableKeybind = Boolean.parseBoolean(config.getProperty("enableKeybind"));
		newerBackgroundStyle = Boolean.parseBoolean(config.getProperty("newerBackgroundStyle"));
		hideMundanePotionRecipes = Boolean.parseBoolean(config.getProperty("hideMundanePotionRecipes"));
		insertBetterWithRenewablesRecipes = Boolean.parseBoolean(config.getProperty("insertBetterWithRenewablesRecipes"));

		if(newConfigFile != null && !newConfigFile.exists())
		{
			try
			{
				newConfigFile.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		if(newConfigFile != null && newConfigFile.exists() && newConfigFile.canWrite())
		{
			try
			{
				config.store(new FileOutputStream(newConfigFile), "");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static File configDirectory()
	{
		File dir = new File(loaderSide.getConfigDir(), "CraftGuide");

		if(!dir.exists() && !dir.mkdirs())
		{
			return null;
		}

		return dir;
	}
}
