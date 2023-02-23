package plus.dragons.createtransitroute;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createtransitroute.content.logistics.transit.TransitRouteManager;

public class TransitRouteClient {

    public static TransitRouteManager ROUTES = new TransitRouteManager();

    public TransitRouteClient() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    }
}
