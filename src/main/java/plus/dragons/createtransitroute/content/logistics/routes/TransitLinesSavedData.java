package plus.dragons.createtransitroute.content.logistics.routes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransitLinesSavedData extends SavedData {

    private Map<UUID,GlobalTransitStation> stations = new HashMap<>();
    private Map<UUID, GlobalTransitLine> lines = new HashMap<>();

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return null;
    }

    private static TransitLinesSavedData load(CompoundTag compoundTag) {
        return null;
    }
}
