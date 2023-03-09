package plus.dragons.createdragontransit.foundation.gui;

import com.simibubi.create.foundation.gui.widget.AbstractSimiWidget;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import plus.dragons.createdragontransit.content.logistics.transit.TransitLine;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStation;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AllStationDisplay extends AbstractSimiWidget {

    private List<LerpedFloat> horizontalScrolls = new ArrayList<>();
    private LerpedFloat scroll = LerpedFloat.linear()
            .startWithValue(0);
    private final List<Pair<TransitStation, Set<TransitLine>>> entries = new ArrayList<>();
    @Nullable
    private UUID station;
    @Nullable
    private UUID excludedStation;

    public AllStationDisplay(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void refresh(){
        // TODO
    }


    public void setStation(@Nullable UUID station) {
        this.station = station;
    }

    public void setExcludedStation(@Nullable UUID excludedStation) {
        this.excludedStation = excludedStation;
    }
}
