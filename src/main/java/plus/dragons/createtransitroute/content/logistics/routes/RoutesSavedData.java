package plus.dragons.createtransitroute.content.logistics.routes;

import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalEdgeGroup;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import plus.dragons.createtransitroute.TransitRoute;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RoutesSavedData extends SavedData {

    public Map<UUID,GlobalTransitStation> stations = new HashMap<>();
    public Map<UUID,GlobalTransitLine> lines = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        GlobalRouteManager routes = TransitRoute.ROUTES;
        compoundTag.put("Stations", NBTHelper.writeCompoundList(routes.stations.values(), GlobalTransitStation::write));
        compoundTag.put("Lines", NBTHelper.writeCompoundList(routes.lines.values(),GlobalTransitLine::write));
        return compoundTag;
    }

    private static RoutesSavedData load(CompoundTag compoundTag) {
        RoutesSavedData ret = new RoutesSavedData();
        NBTHelper.iterateCompoundList(compoundTag.getList("Stations", Tag.TAG_COMPOUND), c -> {
            GlobalTransitStation station = GlobalTransitStation.read(c);
            ret.stations.put(station.id, station);
        });
        NBTHelper.iterateCompoundList(compoundTag.getList("Lines", Tag.TAG_COMPOUND), c -> {
            GlobalTransitLine line = GlobalTransitLine.read(c);
            ret.lines.put(line.id, line);
        });
        return ret;
    }

    private RoutesSavedData() {}

    public static RoutesSavedData load(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(RoutesSavedData::load, RoutesSavedData::new, "create_transit_route");
    }
}
