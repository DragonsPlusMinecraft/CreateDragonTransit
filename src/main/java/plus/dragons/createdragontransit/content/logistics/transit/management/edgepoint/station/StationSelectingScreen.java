package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.gui.components.EditBox;
import plus.dragons.createdragontransit.content.logistics.transit.TransitLine;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStation;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class StationSelectingScreen extends PlatformAbstractScreen {

    private List<LerpedFloat> horizontalScrolls = new ArrayList<>();
    private LerpedFloat scroll = LerpedFloat.linear()
            .startWithValue(0);
    private final List<Pair<TransitStation, Set<TransitLine>>> entries = new ArrayList<>();
    private final List<Pair<TransitStation, Set<TransitLine>>> shownEntry = new ArrayList<>();
    private Indicator onlyShowOwned;
    private Indicator onlyShowAvailable;
    private EditBox searchBox;

    public StationSelectingScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform) {
        super(be,platform);
    }

    @Override
    public void clientRefresh() {
        // TODO
    }
}
