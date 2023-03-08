package plus.dragons.createdragontransit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import plus.dragons.createdragontransit.content.logistics.transit.TransitNetworkManager;
import plus.dragons.createdragontransit.content.logistics.transit.TransitStationPlatform;
import plus.dragons.createdragontransit.entry.CdtBlockEntities;
import plus.dragons.createdragontransit.entry.CdtBlocks;
import plus.dragons.createdragontransit.entry.CdtPackets;
import plus.dragons.createdragontransit.foundation.config.CdtConfigs;

@Mod(DragonTransit.ID)
public class DragonTransit
{
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ID = "create_dragon_transit";
    public static final String NAME = "Create Dragon Transit";
    public static final CreateRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);
    public static TransitNetworkManager ROUTES = new TransitNetworkManager();
    public static final EdgePointType<TransitStationPlatform> PLATFORM =
            EdgePointType.register(genRL("platform"), TransitStationPlatform::new);


    public DragonTransit() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        CdtConfigs.register(ModLoadingContext.get());

        registerEntries(modEventBus);
        modEventBus.addListener(DragonTransit::setup);
        registerTransitNetworkManagerEvent(forgeEventBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> DragonTransitClient::new);
    }

    private void registerEntries(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);
        CdtBlocks.register();
        CdtBlockEntities.register();
    }

    private void registerTransitNetworkManagerEvent(IEventBus forgeEventBus) {
        forgeEventBus.addListener(TransitNetworkManager::playerLoggedIn);
        forgeEventBus.addListener(TransitNetworkManager::onLoadWorld);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CdtPackets.registerPackets();
        });
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(ID, name);
    }
}
