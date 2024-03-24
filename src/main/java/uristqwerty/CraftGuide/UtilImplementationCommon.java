package uristqwerty.CraftGuide;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.EnumRarity;
import net.minecraft.ItemStack;
import uristqwerty.CraftGuide.api.ItemFilter;
import uristqwerty.CraftGuide.api.Util;
import uristqwerty.CraftGuide.client.ui.GuiRenderer;

public abstract class UtilImplementationCommon extends Util
{
	public float partialTicks;

	@Override
	public ItemFilter getCommonFilter(Object stack)
	{
		if(stack == null)
		{
			return null;
		}
		else if(stack instanceof ItemStack)
		{
			return new SingleItemFilter((ItemStack)stack);
		}
		else if(stack instanceof List && !((List) stack).isEmpty())
		{
			return new MultipleItemFilter((List)stack);
		}
		else if(stack instanceof String)
		{
			return new StringItemFilter((String)stack);
		}
		else
		{
			return null;
		}
	}

	@Override
	public List<String> getItemStackText(ItemStack stack)
	{
		if(stack.getItem() == null)
		{
			List<String> text = new ArrayList<String>(1);
			text.add("\247" + Integer.toHexString(15) + "Error: Item #" + Integer.toString(stack.itemID) + " does not exist");
			return text;
		}

		try
		{
			List list = ((GuiRenderer)GuiRenderer.instance).getItemNameandInformation(stack);

			if(CommonUtilities.getItemSubtype(stack) == CraftGuide.Subtype_WILDCARD && (list.size() < 1 || (list.size() == 1 && (list.get(0) == null || (list.get(0) instanceof String && ((String)list.get(0)).isEmpty())))))
			{
				list = ((GuiRenderer)GuiRenderer.instance).getItemNameandInformation(GuiRenderer.fixedItemStack(stack));
			}

			List<String> text = new ArrayList<String>(list.size());
			boolean first = true;

			for(Object o: list)
			{
				if(o instanceof String)
				{
					if(first)
					{
						EnumRarity rarity = null;

						try
						{
							rarity = stack.getRarity();
						}
						catch(NullPointerException ignored)
						{
						}

						if(rarity == null)
						{
							rarity = EnumRarity.common;
						}

						text.add("§" + Integer.toHexString(rarity.rarityColor) + (String)o);

						if(CraftGuide.alwaysShowID)
						{
							text.add("§7" + "ID: " + stack.itemID + "; data: " + CommonUtilities.getItemSubtype(stack));
						}

						first = false;
					}
					else
					{
						text.add("§7" + (String)o);
					}
				}
			}

			return text;
		}
		catch(Exception e)
		{
			CraftGuideLog.log(e);

			List<String> text = new ArrayList<String>(1);
			text.add("\247" + Integer.toHexString(15) + "Item #" + Integer.toString(stack.itemID) + " data " + Integer.toString(CommonUtilities.getItemSubtype(stack)));
			return text;
		}
	}

	@Override
	public void reloadRecipes()
	{
		CraftGuide.side.reloadRecipes();
	}

	@Override
	public float getPartialTicks()
	{
		return partialTicks;
	}
}
