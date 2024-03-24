package uristqwerty.CraftGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.ItemStack;

public class CraftType implements Comparable<CraftType>
{
	private static Map<Integer, Map<Integer, CraftType>> cache = new HashMap<Integer, Map<Integer, CraftType>>();
	private static Map<ArrayList, CraftType> arrayListCache = new HashMap<ArrayList, CraftType>();
	private int item, subtype;
	private Object stack;

	private CraftType(int itemID, int itemSubtype)
	{
		item = itemID;
		subtype = itemSubtype;
		stack = new ItemStack(item, 1, subtype);
	}

	private CraftType(ArrayList<ItemStack> items)
	{
		ItemStack itemStack = items.get(0);
		item = itemStack.itemID;
		subtype = CommonUtilities.getItemSubtype(itemStack);
		stack = items;
	}

	public static CraftType getInstance(Object stack)
	{
		if(stack instanceof ItemStack)
		{
			return getInstance((ItemStack)stack);
		}
		else if(stack instanceof ArrayList && !((ArrayList) stack).isEmpty())
		{
			return getInstance((ArrayList)stack);
		}
		else
		{
			return null;
		}
	}

	private static CraftType getInstance(ArrayList stack)
	{
		CraftType type = arrayListCache.get(stack);

		if(type == null)
		{
			type = new CraftType(stack);
			arrayListCache.put(stack, type);
		}

		return type;
	}

	private static CraftType getInstance(ItemStack stack)
	{
		Map<Integer, CraftType> map = cache.get(stack.itemID);

		if(map == null)
		{
			map = new HashMap<Integer, CraftType>();
			cache.put(stack.itemID, map);
		}

		CraftType type = map.get(CommonUtilities.getItemSubtype(stack));

		if(type == null)
		{
			type = new CraftType(stack.itemID, CommonUtilities.getItemSubtype(stack));
			map.put(CommonUtilities.getItemSubtype(stack), type);
		}

		return type;
	}

	public static boolean hasInstance(ItemStack stack)
	{
		if(!cache.containsKey(stack.itemID))
		{
			return false;
		}

		return cache.get(stack.itemID).containsKey(CommonUtilities.getItemSubtype(stack));
	}

	@Override
	public int compareTo(CraftType other)
	{
		if(this.item != other.item)
		{
			return this.item > other.item? 1 : -1;
		}
		else if(this.subtype != other.subtype)
		{
			return this.subtype > other.subtype? 1 : -1;
		}
		else if((this.stack instanceof ArrayList) != (other.stack instanceof ArrayList))
		{
			return (this.stack instanceof ArrayList)? -1 : 1;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj != null && obj instanceof CraftType)
		{
			CraftType type = (CraftType)obj;

			if(stack instanceof ItemStack && type.stack instanceof ItemStack)
			{
				return type.item == this.item && type.subtype == this.subtype;
			}
			else if(stack instanceof ArrayList && type.stack instanceof ArrayList)
			{
				return stack.equals(type.stack);
			}
			else
			{
				return false;
			}
		}

		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return subtype * 3571 + item;
	}

	public Object getStack()
	{
		return stack;
	}

	public ItemStack getDisplayStack()
	{
		if(stack instanceof ItemStack)
		{
			return (ItemStack)stack;
		}
		else if(stack instanceof ArrayList)
		{
			return (ItemStack)((ArrayList)stack).get(0);
		}
		else
		{
			return null;
		}
	}
}