package uristqwerty.CraftGuide;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import craftguide.api.PinyinMatch;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import org.jetbrains.annotations.NotNull;
import uristqwerty.CraftGuide.api.StackInfo;
import uristqwerty.CraftGuide.api.StackInfoSource;
import uristqwerty.CraftGuide.api.Util;
import uristqwerty.CraftGuide.client.CraftGuideClient;

public class CommonUtilities
{
	public static Field getPrivateField(Class fromClass, String... names) throws NoSuchFieldException
	{
		Field field = null;

		for(String name: names)
		{
			try
			{
				field = fromClass.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			}
			catch(NoSuchFieldException ignored)
			{
			}
		}

		if(names.length == 1)
		{
			throw new NoSuchFieldException("Could not find a field named " + names[0]);
		}
		else
		{
			StringBuilder nameStr = new StringBuilder("[" + names[0]);

			for(int i = 1; i < names.length; i++)
			{
				nameStr.append(", ").append(names[i]);
			}

			throw new NoSuchFieldException("Could not find a field with any of the following names: " + nameStr + "]");
		}
	}

	public static <T> Object getPrivateValue(Class<T> objectClass, T object, String... names) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		Field field = getPrivateField(objectClass, names);
		return field.get(object);
	}

	public static String itemName(ItemStack item)
	{
		String idText = "";

		if(((CraftGuideClient)CraftGuide.side).getMinecraftInstance().gameSettings.advancedItemTooltips)
		{
			if(item.getHasSubtypes())
			{
				idText = String.format(" (#%04d/%d)", item.itemID, getItemSubtype(item));
			}
			else
			{
				idText = String.format(" (#%04d)", item.itemID);
			}
		}

		return item.getDisplayName() + idText;
	}

	public static List<String> itemNames(ItemStack item)
	{
		ArrayList<String> list = new ArrayList<>();

		if(getItemSubtype(item) == CraftGuide.Subtype_WILDCARD && item.getHasSubtypes())
		{
			ArrayList<ItemStack> subItems = new ArrayList<>();
			item.getItem().getSubItems(item.itemID, null, subItems);

			for(ItemStack stack: subItems)
			{
				list.add(itemName(stack));
			}
		}
		else
		{
			list.add(itemName(item));
		}

		return list;
	}

	public static int countItemNames(ItemStack item)
	{
		if(getItemSubtype(item) == CraftGuide.Subtype_WILDCARD && item.getHasSubtypes())
		{
			ArrayList<ItemStack> temp = new ArrayList<>();
			item.getItem().getSubItems(item.itemID, null, temp);

			return temp.size();
		}
		else
		{
			return 1;
		}
	}

	public static int countItemNames(Object item)
	{
		if(item instanceof ItemStack)
		{
			return countItemNames((ItemStack)item);
		}
		else if(item instanceof List)
		{
			int count = 0;
			for(Object o: (List)item)
			{
				count += countItemNames(o);
			}

			return count;
		}
		else
		{
			return 0;
		}
	}

	public static List<String> getExtendedItemStackText(Object item)
	{
		List<String> text = getItemStackText(item);

		if(item instanceof ItemStack || (item instanceof List && ((List)item).get(0) instanceof ItemStack))
		{
			ItemStack stack = item instanceof ItemStack? (ItemStack)item : (ItemStack)((List)item).get(0);

			Iterator<StackInfoSource> iterator = StackInfo.sources.iterator();
			while(iterator.hasNext())
			{
				StackInfoSource infoSource = iterator.next();
				try
				{
					String info = infoSource.getInfo(stack);

					if(info != null)
					{
						text.add(info);
					}
				}
				catch(LinkageError e)
				{
					CraftGuideLog.log(e);
					iterator.remove();
				}
				catch(Exception e)
				{
					CraftGuideLog.log(e);
				}
			}
		}

		if(item instanceof List && ((List)item).size() > 1)
		{
			int count = CommonUtilities.countItemNames(item);
			text.add("§7" + (count - 1) + " other type" + (count > 2? "s" : "") + " of item accepted");
		}

		return text;
	}

	public static boolean searchExtendedItemStackText(Object item, String text)
	{
		for(String line: getExtendedItemStackText(item))
		{
			if(line != null)
			{
				if (Objects.equals(Minecraft.theMinecraft.gameSettings.language, "zh_CN") &&
						PinyinMatch.contains(line.toLowerCase(), text)) {
					return true;
				}
				else if(line.toLowerCase().contains(text)) {
					return true;
				}
			}
		}

		return false;
	}

	private static List<String> getItemStackText(Object item)
	{
		if(item instanceof List)
		{
			return getItemStackText(((List)item).get(0));
		}
		else if(item instanceof ItemStack)
		{
			return Util.instance.getItemStackText((ItemStack)item);
		}
		else
		{
			return null;
		}
	}

	static Field itemSubtypeField = null;

	static
	{
		try
		{
			itemSubtypeField = getPrivateField(ItemStack.class, "subtype");
		}
		catch(NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public static int getItemSubtype(ItemStack stack)
	{
		if(stack.getItem() != null)
		{
			return stack.getItemSubtype();
		}
		else
		{
			if(itemSubtypeField != null)
			{
				try
				{
					return itemSubtypeField.getInt(stack);
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
            }

			return 0;
		}
	}

	public static boolean checkItemStackMatch(ItemStack first, ItemStack second)
	{
		if(first == null || second == null)
		{
			return first == second;
		}

		return first.itemID == second.itemID
			&& (
				getItemSubtype(first) == CraftGuide.Subtype_WILDCARD ||
				getItemSubtype(second) == CraftGuide.Subtype_WILDCARD ||
				getItemSubtype(first) == getItemSubtype(second)
			);
	}

	public static Method getPrivateMethod(Class fromClass, String[] names, Class... params) throws NoSuchMethodException
	{
		Method method = null;

		for(String name: names)
		{
			try
			{
				method = fromClass.getDeclaredMethod(name, params);
				method.setAccessible(true);
				return method;
			}
			catch(NoSuchMethodException ignored)
			{
			}
		}

		if(names.length == 1)
		{
			throw new NoSuchMethodException("Could not find a method named " + names[0]);
		}
		else
		{
			StringBuilder nameStr = new StringBuilder("[" + names[0]);

			for(int i = 1; i < names.length; i++)
			{
				nameStr.append(", ").append(names[i]);
			}

			throw new NoSuchMethodException("Could not find a method with any of the following names: " + nameStr + "]");
		}
	}
}
