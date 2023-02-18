package plus.dragons.createtransitroute.content.logistics.routes;

import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TrainPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalEdgeGroup;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableObject;
import plus.dragons.createtransitroute.TransitRouteClient;

import java.util.*;

public class GlobalRouteManager {

    public Map<UUID,GlobalTransitStation> stations = new HashMap<>();
    public Map<UUID, GlobalTransitLine> lines = new HashMap<>();
    GlobalRouteSync sync;

    RoutesSavedData savedData;


    public GlobalRouteManager() {
        cleanUp();
    }

    public void playerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            loadRouteData(serverPlayer.getServer());
            trackNetworks.values()
                    .forEach(g -> sync.sendFullGraphTo(g, serverPlayer));
            ArrayList<SignalEdgeGroup> asList = new ArrayList<>(signalEdgeGroups.values());
            sync.sendEdgeGroups(asList.stream()
                            .map(g -> g.id)
                            .toList(),
                    asList.stream()
                            .map(g -> g.color)
                            .toList(),
                    serverPlayer);
            for (Train train : trains.values())
                AllPackets.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new TrainPacket(train, true));
        }
    }

    public void playerLogout(Player player) {}

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        cleanUp();
        savedData = null;
        loadRouteData(server);
    }

    private void loadRouteData(MinecraftServer server) {
        if (savedData != null)
            return;
        savedData = RoutesSavedData.load(server);
        stations = savedData.stations;
        lines = savedData.lines;
    }

    public void cleanUp() {
        stations = new HashMap<>();
        lines = new HashMap<>();
        sync = new GlobalRouteSync();
    }

    public void markTracksDirty() {
        if (savedData != null)
            savedData.setDirty();
    }

    public GlobalRouteManager sided(LevelAccessor level) {
        if (level != null && !level.isClientSide())
            return this;
        MutableObject<GlobalRouteManager> m = new MutableObject<>();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientManager(m));
        return m.getValue();
    }

    @OnlyIn(Dist.CLIENT)
    private void clientManager(MutableObject<GlobalRouteManager> m) {
        m.setValue(TransitRouteClient.ROUTES);
    }
}
