package plus.dragons.createtransitroute;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import plus.dragons.createtransitroute.content.logistics.routes.GlobalRouteManager;

@Mod(TransitRoute.ID)
public class TransitRoute
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "create_transit_route";
    public static final String NAME = "Create Transit Route";
    public static final CreateRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);
    public static GlobalRouteManager ROUTES = new GlobalRouteManager();


    public TransitRoute() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        registerEntries(modEventBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> TransitRouteClient::new);
    }

    private void registerEntries(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);
    }
}
