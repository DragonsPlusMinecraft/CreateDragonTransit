package plus.dragons.createdragontransit.foundation.gui;

import com.simibubi.create.foundation.gui.widget.AbstractSimiWidget;
import com.simibubi.create.foundation.utility.Pair;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import plus.dragons.createdragontransit.content.logistics.transit.TransitLine;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllPlatformDisplay extends AbstractSimiWidget {

    private List<LerpedFloat> horizontalScrolls = new ArrayList<>();
    private LerpedFloat scroll = LerpedFloat.linear()
            .startWithValue(0);
    private final List<Pair<TransitStationPlatform, TransitLine.Segment>> entries = new ArrayList<>();
    @Nullable
    private UUID station;
    @Nullable
    private UUID excludedPlatform;

    public AllPlatformDisplay(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void refresh(){
        // TODO
    }


    public void setStation(@Nullable UUID station) {
        this.station = station;
    }

    public void setExcludedPlatform(@Nullable UUID excludedPlatform) {
        this.excludedPlatform = excludedPlatform;
    }
}
