package plus.dragons.createdragontransit.content.logistics.transit;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import plus.dragons.createdragontransit.DragonTransitClient;

import java.util.function.Supplier;

public class TransitNetworkInitializePacket  extends SimplePacketBase {
    CompoundTag nbt;

    public TransitNetworkInitializePacket(CompoundTag networkNBT) {
        this.nbt = networkNBT;
    }

    public TransitNetworkInitializePacket(FriendlyByteBuf buffer){
        this.nbt = buffer.readAnySizeNbt();
    }
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(nbt);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get()
                .enqueueWork(() -> {
                    var manager = DragonTransitClient.ROUTES;
                    manager.cleanUp();
                    manager.network.load(nbt);
                });
        context.get()
                .setPacketHandled(true);
    }
}
