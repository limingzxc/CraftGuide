package uristqwerty.CraftGuide;

import net.minecraft.CreativeTabs;
import net.minecraft.EntityPlayer;
import net.minecraft.Item;
import net.minecraft.ItemStack;

public class ItemCraftGuide extends Item
{
	public ItemCraftGuide(int i, String texture)
	{
		super(i, texture);
		setUnlocalizedName("craftguide_item");
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
    public boolean onItemRightClick(EntityPlayer player, float partial_tick, boolean ctrl_is_down)
    {
		CraftGuide.side.openGUI(player);
        return true;
    }

	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		return 0x9999ff;
	}
}
