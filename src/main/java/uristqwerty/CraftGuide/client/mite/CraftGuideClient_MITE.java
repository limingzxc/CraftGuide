package uristqwerty.CraftGuide.client.mite;

import java.lang.reflect.Field;

import craftguide.CraftGuide_MITE;
import net.minecraft.*;
//import net.minecraft.ITexturePack;
//import net.minecraft.TexturePackList;
import uristqwerty.CraftGuide.CommonUtilities;
import uristqwerty.CraftGuide.CraftGuide;
import uristqwerty.CraftGuide.CraftGuideLog;
import uristqwerty.CraftGuide.GuiCraftGuide;
import uristqwerty.CraftGuide.client.CraftGuideClient;


public class CraftGuideClient_MITE extends CraftGuideClient
{
	private static Field isDrawing = null;

	@Override
	public void initKeybind()
	{
		((CraftGuide_MITE)CraftGuide.loaderSide).initKeybind();
	}

	@Override
	public void openGUI(EntityPlayer player)
	{
		Minecraft.getMinecraft().displayGuiScreen(GuiCraftGuide.getInstance());
	}

	@Override
	public Minecraft getMinecraftInstance()
	{
		return Minecraft.getMinecraft();
	}

//	@Override
//	public ITexturePack getSelectedTexturePack()
//	{
//		ResourcePackRepository renderEngine = getMinecraftInstance().mcResourcePackRepository;
//
//		try
//		{
//			TexturePackList texturePackList = (TexturePackList)CommonUtilities.getPrivateValue(TextureManager.class, renderEngine, "g", "texturePack", "field_78366_k", "field_1981");
//			return texturePackList.getSelectedTexturePack();
//		}
//		catch(SecurityException | IllegalAccessException | NoSuchFieldException | IllegalArgumentException e)
//		{
//			CraftGuideLog.log(e, "", true);
//		}
//
//        return null;
//	}

	@Override
	public void stopTessellating()
	{
		if(isDrawing == null)
		{
			try
			{
				isDrawing = CommonUtilities.getPrivateField(Tessellator.class, "z", "isDrawing", "field_78415_z", "field_1970");
			}
			catch(NoSuchFieldException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			if(isDrawing != null && isDrawing.getBoolean(Tessellator.instance))
			{
				Tessellator.instance.draw();
			}
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			CraftGuideLog.log(e);
		}
    }
}
