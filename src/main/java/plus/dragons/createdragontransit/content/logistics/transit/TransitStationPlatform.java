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

    public String code;
    public WeakReference<Train> nearestTrain;
    @Nullable
    public UUID station;


    public TransitStationPlatform() {
        code = "None";
        station = null;
        nearestTrain = new WeakReference<>(null);
    }

    @Override
    public void tileAdded(BlockEntity tile, boolean front) {
        super.tileAdded(tile, front);
    }

    @Override
    public void read(CompoundTag nbt, boolean migration, DimensionPalette dimensions) {
        super.read(nbt, migration, dimensions);
        code = nbt.getString("Code");
        station = nbt.contains("StationID")? nbt.getUUID("StationID"): null;
        nearestTrain = new WeakReference<>(null);
    }



    @Override
    public void read(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.read(buffer, dimensions);
        code = buffer.readUtf();
        if(buffer.readBoolean()){
            station = buffer.readUUID();
        }
        if (buffer.readBoolean())
            tilePos = buffer.readBlockPos();
    }

    @Override
    public void write(CompoundTag nbt, DimensionPalette dimensions) {
        super.write(nbt, dimensions);;
        nbt.putString("Code", code);
        if(station !=null){
            nbt.putUUID("StationID", station);
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer, DimensionPalette dimensions) {
        super.write(buffer, dimensions);
        buffer.writeUtf(code);
        buffer.writeBoolean(station != null);
        if(station != null)
            buffer.writeUUID(station);
        buffer.writeBoolean(tilePos != null);
        if (tilePos != null)
            buffer.writeBlockPos(tilePos);

    }


    // Heavily TODO
    /*public void reserveFor(Train train) {
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
    }*/

}
