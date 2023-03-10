package plus.dragons.createdragontransit.content.logistics.transit.management.edgepoint.station;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import plus.dragons.createdragontransit.content.logistics.transit.ClientRefreshable;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;

public abstract class PlatformAbstractScreen extends AbstractSimiScreen implements ClientRefreshable {

    protected AllGuiTextures background;
    protected final TransitStationPlatformBlockEntity be;
    protected final TransitStationPlatform platform;
    private EditBox nameBox;
    private IconButton editBelongingStation;
    private IconButton resetBelongingStation;
    private EditBox stationNameBox;
    private EditBox stationTranslationNameBox;
    private Indicator privacyIndicator;
    private IconButton confirmStationEditing;

    public PlatformAbstractScreen(TransitStationPlatformBlockEntity be, TransitStationPlatform platform) {
        super(Component.literal(platform.code));
        this.be = be;
        this.platform = platform;
    }

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();
    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(ms, x, y, this);

        ms.pushPose();
        TransformStack msr = TransformStack.cast(ms);
        msr.pushPose()
                .translate(x + background.width + 4, y + background.height + 4, 100)
                .scale(40)
                .rotateX(-22)
                .rotateY(63);
        GuiGameElement.of(be.getBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, false))
                .render(ms);

        ms.popPose();
    }
}
