package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.content.contraptions.components.structureMovement.ITransformableTE;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createdragontransit.DragonTransit;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import java.util.List;

public class TransitStationPlatformBlockEntity extends SmartTileEntity implements ITransformableTE {

    public TrackTargetingBehaviour<TransitStationPlatform> edgePoint;

    public TransitStationPlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void transform(StructureTransform transform) {
        edgePoint.transform(transform);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, DragonTransit.PLATFORM));
    }
}
