package plus.dragons.createtransitroute;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createtransitroute.content.logistics.transit.GlobalTransitManager;

public class TransitRouteClient {

    public static GlobalTransitManager ROUTES = new GlobalTransitManager();

    public TransitRouteClient() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    }
}
