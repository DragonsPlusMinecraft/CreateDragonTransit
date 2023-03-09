package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

import java.util.ArrayList;
import java.util.List;

public class TransitStationPlatformScreen extends AbstractSimiScreen{
    private final TransitStationPlatformBlockEntity be;
    private final TransitStationPlatform platform;
    private Mode mode;
    private EditBox nameBox;
    private IconButton editBelongingStation;
    private IconButton resetBelongingStation;
    private EditBox stationNameBox;
    private EditBox stationTranslationNameBox;
    private Indicator privacyIndicator;
    private IconButton confirmStationEditing;

    public TransitStationPlatformScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform) {
        super(Component.literal(platform.code));
        this.be = be;
        this.platform = platform;
    }

    public void refreshOnSync(){
        // TODO
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {

    }

    private enum Mode{
        VIEW_STATION,
        SELECT_STATION,
        EDIT_STATION
    }
}
