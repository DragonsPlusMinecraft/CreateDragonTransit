package plus.dragons.createdragontransit;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createdragontransit.content.logistics.transit.TransitNetworkManager;

public class DragonTransitClient {

    public static TransitNetworkManager ROUTES = new TransitNetworkManager();

    public DragonTransitClient() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
    }
}
