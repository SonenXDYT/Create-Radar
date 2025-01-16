package com.happysg.radar.block.radar.link.screens;

import com.happysg.radar.block.monitor.MonitorFilter;
import com.happysg.radar.block.radar.link.RadarLinkBlockEntity;
import com.happysg.radar.registry.ModGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class RadarFilterScreen extends AbstractRadarLinkScreen {

    boolean player;
    boolean vs2;
    boolean contraption;
    boolean mob;
    boolean projectile;

    protected IconButton playerButton;
    protected Indicator playerIndicator;
    protected IconButton vs2Button;
    protected Indicator vs2Indicator;
    protected IconButton contraptionButton;
    protected Indicator contraptionIndicator;
    protected IconButton mobButton;
    protected Indicator mobIndicator;
    protected IconButton projectileButton;
    protected Indicator projectileIndicator;

    public RadarFilterScreen(RadarLinkBlockEntity be) {
        super(be);
        this.background = ModGuiTextures.RADAR_FILTER;
        MonitorFilter monitorFilter = MonitorFilter.DEFAULT;
        if (be.getSourceConfig().contains("filter")) {
            monitorFilter = MonitorFilter.fromTag(be.getSourceConfig().getCompound("filter"));
        }
        player = monitorFilter.player();
        vs2 = monitorFilter.vs2();
        contraption = monitorFilter.contraption();
        mob = monitorFilter.mob();
        projectile = monitorFilter.projectile();
    }


    @Override
    protected void init() {
        super.init();
        playerButton = new IconButton(guiLeft + 42, guiTop + 32, ModGuiTextures.PLAYER_BUTTON);
        playerIndicator = new Indicator(guiLeft + 42, guiTop + 25, Component.empty());
        playerIndicator.state = player ? Indicator.State.GREEN : Indicator.State.RED;
        playerButton.withCallback((x, y) -> {
            player = !player;
            playerIndicator.state = player ? Indicator.State.GREEN : Indicator.State.RED;
        });
        addRenderableWidget(playerButton);
        addRenderableWidget(playerIndicator);

        vs2Button = new IconButton(guiLeft + 70, guiTop + 32, ModGuiTextures.VS2_BUTTON);
        vs2Indicator = new Indicator(guiLeft + 70, guiTop + 25, Component.empty());
        vs2Indicator.state = vs2 ? Indicator.State.GREEN : Indicator.State.RED;
        vs2Button.withCallback((x, y) -> {
            vs2 = !vs2;
            vs2Indicator.state = vs2 ? Indicator.State.GREEN : Indicator.State.RED;
        });
        addRenderableWidget(vs2Button);
        addRenderableWidget(vs2Indicator);

        contraptionButton = new IconButton(guiLeft + 98, guiTop + 32, ModGuiTextures.CONTRAPTION_BUTTON);
        contraptionIndicator = new Indicator(guiLeft + 98, guiTop + 25, Component.empty());
        contraptionIndicator.state = contraption ? Indicator.State.GREEN : Indicator.State.RED;
        contraptionButton.withCallback((x, y) -> {
            contraption = !contraption;
            contraptionIndicator.state = contraption ? Indicator.State.GREEN : Indicator.State.RED;
        });
        addRenderableWidget(contraptionButton);
        addRenderableWidget(contraptionIndicator);

        mobButton = new IconButton(guiLeft + 126, guiTop + 32, ModGuiTextures.MOB_BUTTON);
        mobIndicator = new Indicator(guiLeft + 126, guiTop + 25, Component.empty());
        mobIndicator.state = mob ? Indicator.State.GREEN : Indicator.State.RED;
        mobButton.withCallback((x, y) -> {
            mob = !mob;
            mobIndicator.state = mob ? Indicator.State.GREEN : Indicator.State.RED;
        });
        addRenderableWidget(mobButton);
        addRenderableWidget(mobIndicator);

        projectileButton = new IconButton(guiLeft + 154, guiTop + 32, ModGuiTextures.PROJECTILE_BUTTON);
        projectileIndicator = new Indicator(guiLeft + 154, guiTop + 25, Component.empty());
        projectileIndicator.state = projectile ? Indicator.State.GREEN : Indicator.State.RED;
        projectileButton.withCallback((x, y) -> {
            projectile = !projectile;
            projectileIndicator.state = projectile ? Indicator.State.GREEN : Indicator.State.RED;
        });
        addRenderableWidget(projectileButton);
        addRenderableWidget(projectileIndicator);


    }

    @Override
    public void onClose(CompoundTag tag) {
        super.onClose(tag);
        MonitorFilter monitorFilter = new MonitorFilter(player, vs2, contraption, mob, projectile);
        tag.put("filter", monitorFilter.toTag());
    }
}
