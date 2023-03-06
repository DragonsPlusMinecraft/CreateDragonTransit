package plus.dragons.createdragontransit.content.logistics.transit;

import com.simibubi.create.content.logistics.trains.DimensionPalette;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SingleTileEdgePoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;

public class TransitStationPlatform extends SingleTileEdgePoint {

    private UUID id;
    public String name;
    public WeakReference<Train> nearestTrain;
    @Nullable
    public UUID station;


    public TransitStationPlatform() {
        id = UUID.randomUUID();
        name = "Unnamed Platform";
        station = null;
        nearestTrain = new WeakReference<>(null);
    }

    @Override
    public void tileAdded(BlockEntity tile, boolean front) {
        super.tileAdded(tile, front);
        // TODO
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        id = nbt.getUUID("ID");
        name = nbt.getString("Name");
        station = nbt.contains("StationID")? nbt.getUUID("StationID"): null;
        nearestTrain = new WeakReference<>(null);
    }



    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
        id = buffer.readUUID();
        name = buffer.readUtf();
        if(buffer.readBoolean()){
            station = buffer.readUUID();
        }
        if (buffer.readBoolean())
            tilePos = buffer.readBlockPos();
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);;
        nbt.putUUID("ID",id);
        nbt.putString("Name",name);
        if(station !=null){
            nbt.putUUID("StationID", station);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
        buffer.writeUUID(id);
        buffer.writeUtf(name);
        buffer.writeBoolean(station != null);
        if(station != null)
            buffer.writeUUID(station);
        buffer.writeBoolean(tilePos != null);
        if (tilePos != null)
            buffer.writeBlockPos(tilePos);

    }

    @Override
    public UUID getId() {
        return id;
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

}
