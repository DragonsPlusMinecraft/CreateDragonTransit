package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.gui.components.EditBox;
import plus.dragons.createdragontransit.content.logistics.transit.TransitLine;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import java.util.ArrayList;
import java.util.List;

public abstract class PlatformEditingScreen extends PlatformAbstractScreen {

    private List<LerpedFloat> horizontalScrolls = new ArrayList<>();
    private LerpedFloat scroll = LerpedFloat.linear()
            .startWithValue(0);

    private EditBox nameBox;
    private final List<Pair<TransitStationPlatform, TransitLine.Segment>> entries = new ArrayList<>();

    public PlatformEditingScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform) {
        super(be,platform);
    }



    @Override
    public void clientRefresh() {
        // TODO
    }
}
