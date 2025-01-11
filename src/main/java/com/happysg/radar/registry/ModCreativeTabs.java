package com.happysg.radar.registry;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.compat.Mods;
import com.happysg.radar.compat.cbc.CBCItemsCompat;
import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.happysg.radar.CreateRadar.REGISTRATE;

public class ModCreativeTabs {
    public static DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateRadar.MODID);

    public static final RegistryObject<CreativeModeTab> RADAR_CREATIVE_TAB = addTab("radar", "Create: Radars",
            ModBlocks.MONITOR::asStack);


    public static RegistryObject<CreativeModeTab> addTab(String id, String name, Supplier<ItemStack> icon) {
        String itemGroupId = "itemGroup." + CreateRadar.MODID + "." + id;
        REGISTRATE.addRawLang(itemGroupId, name);
        CreativeModeTab.Builder tabBuilder = CreativeModeTab.builder()
                .icon(icon)
                .displayItems(ModCreativeTabs::displayItems)
                .title(Components.translatable(itemGroupId))
                .withTabsBefore(AllCreativeModeTabs.PALETTES_CREATIVE_TAB.getKey());
        return CREATIVE_TABS.register(id, tabBuilder::build);
    }

    private static void displayItems(CreativeModeTab.ItemDisplayParameters pParameters, CreativeModeTab.Output pOutput) {
        pOutput.accept(ModBlocks.MONITOR);
        pOutput.accept(ModBlocks.RADAR_LINK);
        pOutput.accept(ModBlocks.RADAR_BEARING_BLOCK);
        pOutput.accept(ModBlocks.RADAR_RECEIVER_BLOCK);
        pOutput.accept(ModBlocks.RADAR_PLATE_BLOCK);
        pOutput.accept(ModBlocks.RADAR_DISH_BLOCK);
        pOutput.accept(ModBlocks.AUTO_YAW_CONTROLLER_BLOCK);
        pOutput.accept(ModBlocks.AUTO_PITCH_CONTROLLER_BLOCK);
        if (Mods.CREATEBIGCANNONS.isLoaded()) {
            pOutput.accept(CBCItemsCompat.GUIDED_FUZE);
        }
    }


    public static void register(IEventBus eventBus) {
        CreateRadar.getLogger().info("Registering CreativeTabs!");
        CREATIVE_TABS.register(eventBus);
    }
}
