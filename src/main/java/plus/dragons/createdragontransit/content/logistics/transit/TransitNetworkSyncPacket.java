package plus.dragons.createdragontransit.content.logistics.transit;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragontransit.DragonTransitClient;

import java.util.UUID;
import java.util.function.Supplier;

public class TransitNetworkSyncPacket extends SimplePacketBase {
    @Nullable
    TransitStation modifiedStation;
    @Nullable
    TransitLine modifiedLine;
    @Nullable
    UUID id;
    int op; // 0 = update & new, 1 = delete station, 2 = delete line

    public TransitNetworkSyncPacket(FriendlyByteBuf buffer){
        op = buffer.readInt();
        if(op==0){
            if(buffer.readInt()==0){
                modifiedStation = TransitStation.read(buffer.readAnySizeNbt());
            } else {
                modifiedLine = TransitLine.read(buffer.readAnySizeNbt());
            }
        } else {
            id = buffer.readUUID();
        }
    }

    public TransitNetworkSyncPacket(@Nullable TransitStation modifiedStation, @Nullable TransitLine modifiedLine, @Nullable UUID id, int op) {
        this.modifiedStation = modifiedStation;
        this.modifiedLine = modifiedLine;
        this.id = id;
        this.op = op;
    }

    public static TransitNetworkSyncPacket updateStation(TransitStation station){
        return new TransitNetworkSyncPacket(station,null,null,0);
    }

    public static TransitNetworkSyncPacket updateLine(TransitLine line){
        return new TransitNetworkSyncPacket(null,line,null,0);
    }

    public static TransitNetworkSyncPacket deleteStation(UUID id){
        return new TransitNetworkSyncPacket(null,null,id,1);
    }

    public static TransitNetworkSyncPacket deleteLine(UUID id){
        return new TransitNetworkSyncPacket(null,null,id,2);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(op);
        if(op==0){
            if(modifiedStation!=null){
                buffer.writeInt(0);
                buffer.writeNbt(modifiedStation.write());
            } else {
                buffer.writeInt(1);
                buffer.writeNbt(modifiedLine.write());
            }
        } else {
            buffer.writeUUID(id);
        }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get()
                .enqueueWork(() -> {
                    var manager = DragonTransitClient.ROUTES;
                    if(op==0){
                        if(modifiedStation!=null){
                            manager.network.stations.put(modifiedStation.getId(),modifiedStation);
                        } else{
                            manager.network.lines.put(modifiedLine.getId(),modifiedLine);
                        }
                    } else if(op==1) {
                        manager.network.stations.remove(id);
                    } else {
                        manager.network.lines.remove(id);
                    }
                });
        context.get()
                .setPacketHandled(true);
    }
}
