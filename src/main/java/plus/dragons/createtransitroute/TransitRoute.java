package plus.dragons.createtransitroute;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TransitRoute.ID)
public class TransitRoute
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "create_transit_route";
    public static final String NAME = "Create Transit Route";

    public TransitRoute()
    {
    }
}
