package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStation;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class TransitStationPlatform extends SingleTileEdgePoint {
    public WeakReference<TransitStation.Platform> platform;
    public WeakReference<Train> nearestTrain;
    @Nullable
    public Pair<UUID,UUID> belong;


    public TransitStationPlatform() {
        belong = null;
        nearestTrain = new WeakReference<>(null);
        platform = new WeakReference<>(null);
    }

    @Override
    public void tileAdded(BlockEntity tile, boolean front) {
        super.tileAdded(tile, front);
        // TODO
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        if(nbt.getBoolean("Bound")){
            belong = Pair.of(nbt.getUUID("StationID"),nbt.getUUID("PlatformID"));
            fetchPlatform();
        }
        nearestTrain = new WeakReference<Train>(null);
    }



    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
        if(buffer.readBoolean()){
            belong = Pair.of(buffer.readUUID(),buffer.readUUID());
            fetchPlatform();
        }
        if (buffer.readBoolean())
            tilePos = buffer.readBlockPos();
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);
        nbt.putBoolean("Bound",belong!=null);
        if(belong!=null){
            nbt.putUUID("StationID",belong.getFirst());
            nbt.putUUID("PlatformID",belong.getSecond());
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
        buffer.writeBoolean(tilePos != null);
        if (tilePos != null)
            buffer.writeBlockPos(tilePos);
        buffer.writeBoolean(belong == null);
    }

    private void fetchPlatform(){
        // TODO
    }

    private boolean stationValid(){
        // TODO
    }

    public void reserveFor(Train train) {
        Train nearestTrain = getNearestTrain();
        if (nearestTrain == null
                || nearestTrain.navigation.distanceToDestination > train.navigation.distanceToDestination)
            this.nearestTrain = new WeakReference<>(train);
    }

    public void cancelReservation(Train train) {
        if (nearestTrain.get() == train)
            nearestTrain = new WeakReference<>(null);
    }

    public void trainDeparted(Train train) {
        cancelReservation(train);
    }

    @Nullable
    public Train getPresentTrain() {
        // TODO
        Train nearestTrain = getNearestTrain();
        if (nearestTrain == null || nearestTrain.getCurrentStation() != this)
            return null;
        return nearestTrain;
    }

    @Nullable
    public Train getImminentTrain() {
        // TODO
        Train nearestTrain = getNearestTrain();
        if (nearestTrain == null)
            return nearestTrain;
        if (nearestTrain.getCurrentStation() == this)
            return nearestTrain;
        if (!nearestTrain.navigation.isActive())
            return null;
        if (nearestTrain.navigation.distanceToDestination > 30)
            return null;
        return nearestTrain;
    }

    @Nullable
    public Train getNearestTrain() {
        return this.nearestTrain.get();
    }

    @Nullable
    public TransitStation.Platform getPlatform() {
        return this.platform.get();
    }
}
