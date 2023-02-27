package plus.dragons.createdragontransit.content.logistics.transit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class RoutesSavedData extends SavedData {

    public TransitNetwork network = new TransitNetwork();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        network.save(compoundTag);
        return compoundTag;
    }

    private static RoutesSavedData load(CompoundTag compoundTag) {
        RoutesSavedData ret = new RoutesSavedData();
        ret.network.load(compoundTag);
        return ret;
    }

    private RoutesSavedData() {}

    public static RoutesSavedData load(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(RoutesSavedData::load, RoutesSavedData::new, "dragon_transit_railway");
    }
}
