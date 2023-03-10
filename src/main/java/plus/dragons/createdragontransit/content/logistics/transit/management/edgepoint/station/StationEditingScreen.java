package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.client.gui.components.EditBox;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStation;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

public abstract class StationEditingScreen extends PlatformAbstractScreen {

    private TransitStation station;
    private IconButton editBelongingStation;
    private IconButton resetBelongingStation;
    private EditBox stationNameBox;
    private EditBox stationTranslationNameBox;
    private Indicator privacyIndicator;
    private IconButton confirmStationEditing;

    public StationEditingScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform, TransitStation station) {
        super(be,platform);
        this.station = station;
    }

    @Override
    public void clientRefresh() {
        // TODO
    }
}
