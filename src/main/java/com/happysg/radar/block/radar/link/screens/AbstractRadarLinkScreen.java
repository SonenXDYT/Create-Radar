package com.happysg.radar.block.radar.link.screens;

import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.block.radar.link.RadarSource;
import com.happysg.radar.block.radar.link.RadarTarget;
import com.happysg.radar.registry.AllRadarBehaviors;
import com.happysg.radar.registry.ModGuiTextures;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AbstractRadarLinkScreen extends AbstractSimiScreen {

    private static final ItemStack FALLBACK = new ItemStack(Items.BARRIER);

    protected ModGuiTextures background;
    private final RadarLinkBlockEntity blockEntity;
    private IconButton confirmButton;

    BlockState sourceState;
    BlockState targetState;
    List<RadarSource> sources;
    RadarTarget target;

    public AbstractRadarLinkScreen(RadarLinkBlockEntity be) {
        this.blockEntity = be;
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;


        initGathererOptions();

        confirmButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);
    }

    @SuppressWarnings("deprecation")
    private void initGathererOptions() {
        ClientLevel level = minecraft.level;
        sourceState = level.getBlockState(blockEntity.getSourcePosition());
        targetState = level.getBlockState(blockEntity.getTargetPosition());

        ItemStack asItem;
        int x = guiLeft;
        int y = guiTop;

        Block sourceBlock = sourceState.getBlock();
        Block targetBlock = targetState.getBlock();

        asItem = sourceBlock.getCloneItemStack(level, blockEntity.getSourcePosition(), sourceState);
        ItemStack sourceIcon = asItem == null || asItem.isEmpty() ? FALLBACK : asItem;
        asItem = targetBlock.getCloneItemStack(level, blockEntity.getTargetPosition(), targetState);
        ItemStack targetIcon = asItem == null || asItem.isEmpty() ? FALLBACK : asItem;

        sources = AllRadarBehaviors.sourcesOf(level, blockEntity.getSourcePosition());
        target = AllRadarBehaviors.targetOf(level, blockEntity.getTargetPosition());

    }


    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

    }
}
