package plus.dragons.createdragontransit.entry;

import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;
import plus.dragons.createdragontransit.DragonTransit;
import plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station.TransitStationPlatformBlock;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static plus.dragons.createdragontransit.DragonTransit.REGISTRATE;

public class CdtBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> Create.BASE_CREATIVE_TAB).startSection(AllSections.LOGISTICS);
    }

    public static final BlockEntry<TransitStationPlatformBlock> TRANSIT_STATION_PLATFORM = REGISTRATE
            .block("transit_station_platform", TransitStationPlatformBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.PODZOL))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(TagGen.pickaxeOnly())
            .blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
            .item(TrackTargetingBlockItem.ofType(DragonTransit.PLATFORM))
            .transform(customItemModel())
            .register();

    public static void register() {}

}
