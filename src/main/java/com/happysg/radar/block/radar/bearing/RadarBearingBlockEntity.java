package com.happysg.radar.block.radar.bearing;

import com.happysg.radar.CreateRadar;
import com.happysg.radar.block.radar.behavior.IRadar;
import com.happysg.radar.block.radar.behavior.RadarScanningBlockBehavior;
import com.happysg.radar.block.radar.track.RadarTrack;
import com.happysg.radar.compat.vs2.VS2Utils;
import com.happysg.radar.config.RadarConfig;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.kinetics.BlockStressValues;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RadarBearingBlockEntity extends MechanicalBearingBlockEntity implements IRadar {
    private int dishCount;
    private boolean creative;
    private Direction receiverFacing = Direction.NORTH;
    private RadarScanningBlockBehavior scanningBehavior;

    public RadarBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        movementMode.setValue(MovementMode.MOVE_NEVER_PLACE.ordinal());
        scanningBehavior = new RadarScanningBlockBehavior(this);
        behaviours.add(scanningBehavior);
    }

    @Override
    public void tick() {
        super.tick();
        if (running)
            scanningBehavior.setAngle(getGlobalAngle());
    }

    public float getGlobalAngle() {
        return (receiverFacing.toYRot() - angle + 360) % 360;
    }

    public float getAngularSpeed() {
        if (!RadarConfig.server().gearRadarBearingSpeed.get())
            return super.getAngularSpeed();

        float speed = convertToAngular(getSpeed());
        if (getSpeed() == 0)
            speed = 0;
        if (level.isClientSide) {
            speed *= ServerSpeedProvider.get();
            speed += clientAngleDiff / 3f;
        }

        return speed / (4f + getDishCount() / 10);
    }


    //code copied in order to replace with radar contraption and radar advancements
    @Override
    public void assemble() {
        if (!(level.getBlockState(getBlockPos())
                .getBlock() instanceof RadarBearingBlock))
            return;

        RadarContraption contraption = createContraption();
        if (contraption == null)
            return;

        //replace with radar advancements
        if (isWindmill())
            award(AllAdvancements.WINDMILL);
        if (contraption.getSailBlocks() >= 16 * 8)
            award(AllAdvancements.WINDMILL_MAXED);

        updateGeneratedRotation();
        updateContraptionData();
        notifyUpdate();
    }


    @Override
    public void disassemble() {
        super.disassemble();
        updateContraptionData();
    }

    @Override
    public float calculateStressApplied() {
        float impact = (float) BlockStressValues.getImpact(getStressConfigKey()) + getDishCount();
        this.lastStressApplied = impact;
        return impact;
    }

    private RadarContraption createContraption() {
        RadarContraption contraption = new RadarContraption();
        try {
            if (!contraption.assemble(level, getBlockPos()))
                return null;

            lastException = null;
        } catch (AssemblyException e) {
            lastException = e;
            sendData();
            return null;
        }
        contraption.removeBlocksFromWorld(level, BlockPos.ZERO);
        movedContraption = ControlledContraptionEntity.create(level, this, contraption);
        BlockPos anchor = getBlockPosition().above();
        movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
        movedContraption.setRotationAxis(Direction.Axis.Y);
        level.addFreshEntity(movedContraption);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, getBlockPos());

        running = true;
        angle = 0;
        return contraption;
    }

    //capturing from radar contraption to save on client side BE, contraption data only server side
    private void updateContraptionData() {
        dishCount = getContraption().map(RadarContraption::getDishCount).orElse(0);
        receiverFacing = getContraption().map(RadarContraption::getReceiverFacing).orElse(Direction.NORTH);
        creative = getContraption().map(RadarContraption::isCreative).orElse(false);
        scanningBehavior.setRange(getRange());
        scanningBehavior.setScanPos(VS2Utils.getWorldVec(this));
        scanningBehavior.setRunning(running);
        scanningBehavior.setAngle(getGlobalAngle());
        notifyUpdate();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.translatable(CreateRadar.MODID + ".radar.dish_count", dishCount));
        tooltip.add(Component.translatable(CreateRadar.MODID + ".radar.range", getRange()));
        return true;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        dishCount = compound.getInt("dishCount");
        creative = compound.getBoolean("creative");
        if (compound.contains("receiverFacing"))
            receiverFacing = Direction.from3DDataValue(compound.getInt("receiverFacing"));
    }

    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("dishCount", dishCount);
        compound.putBoolean("creative", creative);
        if (receiverFacing != null)
            compound.putInt("receiverFacing", receiverFacing.get3DDataValue());

    }

    public int getDishCount() {
        return dishCount;
    }

    public Optional<RadarContraption> getContraption() {
        return Optional.ofNullable(movedContraption)
                .map(ControlledContraptionEntity::getContraption)
                .filter(c -> c instanceof RadarContraption)
                .map(c -> (RadarContraption) c);
    }

    public float getAngle() {
        return angle;
    }

    public Direction getReceiverFacing() {
        return receiverFacing;
    }

    public float getRange() {
        if (creative)
            return RadarConfig.server().maxRadarRange.get();
        return Math.min(RadarConfig.server().radarBaseRange.get() + dishCount * RadarConfig.server().dishRangeIncrease.get(), RadarConfig.server().maxRadarRange.get());
    }

    public Collection<RadarTrack> getTracks() {
        return scanningBehavior.getRadarTracks();
    }
}
