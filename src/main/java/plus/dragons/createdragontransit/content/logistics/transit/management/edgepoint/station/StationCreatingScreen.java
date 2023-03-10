package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.client.gui.components.EditBox;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStation;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

public abstract class StationCreatingScreen extends PlatformAbstractScreen {
    private EditBox stationNameBox;
    private EditBox stationTranslationNameBox;
    private Indicator privacyIndicator;
    private IconButton confirmStationCreating;

    public StationCreatingScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform) {
        super(be,platform);
    }

    @Override
    public void clientRefresh() {
        // TODO
    }
}
