package plus.dragons.createtransitroute.content.logistics.routes;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LineSavedData extends SavedData {

    private Map<UUID,GlobalLine> lines = new HashMap<>();
    @Override
    public CompoundTag save(CompoundTag pCompoundTag) {
        return null;
    }

    private static LineSavedData load(CompoundTag nbt) {
        return null
    }
}
