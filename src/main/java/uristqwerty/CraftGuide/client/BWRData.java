package uristqwerty.CraftGuide.client;

import net.minecraft.*;
import uristqwerty.CraftGuide.CraftGuide;

import java.io.ByteArrayInputStream;

public class BWRData
{
	public static boolean hasRecipes;

	private static Object[][][] millstone;
	private static Object[][][] crucible;
	private static Object[][][] crucibleStoked;
	private static Object[][][] cauldron;
	private static Object[][][] cauldronStoked;

	public static void readPacket(byte data[])
	{
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		stream.skip(1); //data[0] identifies this as NBT-encoded BWR recipes. The rest is the NBT.

        NBTTagCompound tag = CompressedStreamTools.readCompressed(stream);
        readTag(tag);
    }

	public static boolean hasRecipes()
	{
		return hasRecipes;
	}

	public static Object[][][] getMillstoneRecipes()
	{
		return millstone;
	}

	public static Object[][][] getCauldronRecipes()
	{
		return cauldron;
	}

	public static Object[][][] getStokedCauldronRecipes()
	{
		return cauldronStoked;
	}

	public static Object[][][] getCrucibleRecipes()
	{
		return crucible;
	}

	public static Object[][][] getStokedCrucibleRecipes()
	{
		return crucibleStoked;
	}

	private static void readTag(NBTTagCompound tag)
	{
//		millstone = readTag(tag, "millstone");
//		cauldron = readTag(tag, "cauldron");
//		cauldronStoked = readTag(tag, "cauldronStoked");
//		crucible = readTag(tag, "crucible");
//		crucibleStoked = readTag(tag, "crucibleStoked");

		hasRecipes = true;
	}

	private static Object[][][] readTag(NBTTagCompound tag, String key)
	{
		if(!tag.hasKey(key) || tag.getTagList(key).tagCount() < 1)
		{
			return null;
		}

		NBTTagList tagList = tag.getTagList(key);
		int length = tagList.tagCount();
		Object[][][] recipes = new Object[length][][];

		for(int i = 0; i < length; i++)
		{
			recipes[i] = readRecipe(tagList.tagAt(i));
		}

		return recipes;
	}

	private static Object[][] readRecipe(NBTBase tag)
	{
		if(!(tag instanceof NBTTagCompound tagCompound))
		{
			return null;
		}

        return new Object[][]{
				readItems(tagCompound.getTagList("input")),
				readItems(tagCompound.getTagList("output")),
		};
	}

	private static Object[] readItems(NBTTagList tag)
	{
		int length = tag.tagCount();
		Object[] items = new Object[length];

		for(int i = 0; i < length; i++)
		{
			items[i] = readItem(tag.tagAt(i));
		}

		return items;
	}

	private static Object readItem(NBTBase tag)
	{
		if(!(tag instanceof NBTTagCompound tagCompound))
		{
			return null;
		}

        if(tagCompound.getBoolean("isArray"))
		{
			return readItems(tagCompound.getTagList("contents"));
		}

		int itemID = getInt(tagCompound, "id", 0);
		int itemCount = getInt(tagCompound, "count", 1);
		int itemSubtype = getInt(tagCompound, "subtype", CraftGuide.Subtype_WILDCARD);

		ItemStack stack = new ItemStack(itemID, itemCount, itemSubtype);

		if(tagCompound.hasKey("tagData"))
		{
			stack.setTagCompound(tagCompound.getCompoundTag("tagData"));
		}

		return stack;
	}

	private static int getInt(NBTTagCompound tag, String key, int defaultValue)
	{
		if(tag.hasKey(key))
		{
			return tag.getInteger(key);
		}
		else
		{
			return defaultValue;
		}
	}
}
