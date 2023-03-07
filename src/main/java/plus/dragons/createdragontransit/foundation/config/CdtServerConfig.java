package plus.dragons.createdragontransit.foundation.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.foundation.config.ui.ConfigAnnotations;
import net.minecraftforge.common.ForgeConfigSpec;

public class CdtServerConfig extends ConfigBase {

    public final ConfigInt maxPlatformInAStation = i(10, 1,
            "maxPlatformInAStation",
            "Maximum number of station platforms that can be assigned to a station.",
            ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigInt maxLinesInAStation = i(10, 1,
        "maxLinesInAStation",
        "Maximum number of lines that can be interchanged at a station.",
        "World record are King's Cross St. Pancras in London, an interchange between six lines and Chatelet in Paris, an interchange between five lines.",
        ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigInt maxStationsInALineSegment = i(4000, 0,
        "maxStationsInALine",
        "Maximum number of stations that can be included in a line segment.",
        ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigInt maxLineSegmentsInALine = i(2, 1,
        "maxLineSegmentsInALine",
        "Maximum number of line segments that can be counted into same line.",
        ConfigAnnotations.RequiresRestart.SERVER.asComment());
    
    @Override
    protected void registerAll(ForgeConfigSpec.Builder builder) {
        super.registerAll(builder);
    }

    @Override
    public String getName() {
        return "server";
    }
}
