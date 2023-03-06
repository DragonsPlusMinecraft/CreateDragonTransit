package plus.dragons.createdragontransit.entry;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station.TransitStationPlatformBlockEntity;
import plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station.TransitStationPlatformRenderer;

import static plus.dragons.createdragontransit.DragonTransit.REGISTRATE;

public class CtrBlockEntities {

    public static final BlockEntityEntry<TransitStationPlatformBlockEntity> TRANSIT_STATION_PLATFORM = REGISTRATE
            .tileEntity("transit_station_platform", TransitStationPlatformBlockEntity::new)
            .renderer(() -> TransitStationPlatformRenderer::new)
            .validBlocks(AllBlocks.TRACK_STATION)
            .register();

    public static void register() {};
}
