package plus.dragons.createdragontransit.content.logistics.transit;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import plus.dragons.createdragontransit.DragonTransit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransitNetwork {
    public Map<UUID, TransitStation> stations = new HashMap<>();
    public Map<UUID, TransitLine> lines = new HashMap<>();

    public CompoundTag save(CompoundTag compoundTag) {
        TransitNetworkManager routes = DragonTransit.ROUTES;
        compoundTag.put("Stations", NBTHelper.writeCompoundList(routes.network.stations.values(), TransitStation::write));
        compoundTag.put("Lines", NBTHelper.writeCompoundList(routes.network.lines.values(), TransitLine::write));
        return compoundTag;
    }

    public void load(CompoundTag compoundTag) {
        stations.clear();
        lines.clear();
        NBTHelper.iterateCompoundList(compoundTag.getList("Stations", Tag.TAG_COMPOUND), c -> {
            TransitStation station = TransitStation.read(c);
            stations.put(station.getId(), station);
        });
        NBTHelper.iterateCompoundList(compoundTag.getList("Lines", Tag.TAG_COMPOUND), c -> {
            TransitLine line = TransitLine.read(c);
            lines.put(line.getId(), line);
        });
    }
}
