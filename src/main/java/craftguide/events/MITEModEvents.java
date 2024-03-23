package craftguide.events;

import com.google.common.eventbus.Subscribe;
import craftguide.block.Blocks;
import craftguide.item.Items;
import craftguide.CraftGuideMITEMod;
import craftguide.achievement.Achievements;
import net.minecraft.ChatMessageComponent;
import net.minecraft.EnumChatFormatting;
import net.xiaoyu233.fml.reload.event.*;

public class MITEModEvents {
    @Subscribe
    public void handleChatCommand(HandleChatCommandEvent event) {

    }

    @Subscribe
    public void onPacketRegister(PacketRegisterEvent event){

    }

    @Subscribe
    public void onItemRegister(ItemRegistryEvent event){
        Items.registerItems(event);
        Blocks.registerItemBlocks(event);
    }

    @Subscribe
    public void onRecipeRegister(RecipeRegistryEvent event){
        Items.registerRecipes(event);
        Blocks.registerRecipes(event);
    }

    @Subscribe
    public void onEnchantmentRegister(EnchantmentRegistryEvent event){
    }

    @Subscribe
    public void onAchievementRegister(AchievementRegistryEvent event){
        Achievements.registerAchievements();
    }
    
    @Subscribe
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        event.getPlayer().sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("[Server] ")
                .appendComponent(ChatMessageComponent.createFromTranslationKey(CraftGuideMITEMod.modName + "模组已加载,当前版本:")
                    .setColor(EnumChatFormatting.DARK_GREEN))
                .appendComponent(ChatMessageComponent.createFromText(CraftGuideMITEMod.modVerStr)
                    .setColor(EnumChatFormatting.DARK_RED)));

        event.getPlayer().sendChatToPlayer(ChatMessageComponent.createFromTranslationKey("[" + CraftGuideMITEMod.modName + "]:")
                .appendComponent(ChatMessageComponent.createFromTranslationKey(CraftGuideMITEMod.modName + " 由")
                .appendComponent(ChatMessageComponent.createFromTranslationKey("limingzxc")
                        .setColor(EnumChatFormatting.WHITE)).addText(" 制作,作者主页:")
                        .setColor(EnumChatFormatting.DARK_RED)
                .appendComponent(ChatMessageComponent.createFromTranslationKey("https://github.com/limingzxc")
                        .setColor(EnumChatFormatting.DARK_GREEN))));
    }

    @Subscribe
    public void onEntityRegister(EntityRegisterEvent event){

    }

    @Subscribe
    public void onEntityRendererRegister(EntityRendererRegistryEvent event){

    }
}
