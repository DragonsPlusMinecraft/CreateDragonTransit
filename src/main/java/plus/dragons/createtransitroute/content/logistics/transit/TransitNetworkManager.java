package plus.dragons.createtransitroute.content.logistics.transit;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import org.apache.commons.lang3.mutable.MutableObject;
import plus.dragons.createtransitroute.TransitRouteClient;

import java.util.*;

public class TransitNetworkManager {

    public Map<UUID, TransitStation> stations = new HashMap<>();
    public Map<UUID, TransitLine> lines = new HashMap<>();
    GlobalRouteSync sync;
    RoutesSavedData savedData;


    public TransitNetworkManager() {
        cleanUp();
    }

    public void playerLogin(Player player) {
        /*if (player instanceof ServerPlayer serverPlayer) {
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
        }*/
    }

    public UUID createLine(){
        
    }

    public UUID createLineSegment(UUID lineID){

    }

    public UUID createStation(){

    }

    public UUID createStationPlatform(UUID stationID){

    }

    public void deleteLine(UUID id){

    }

    public void deleteLineSegment(UUID lineID, UUID id){

    }

    public void deleteStation(UUID id){

    }

    public void deleteStationPlatform(UUID stationID, UUID id){

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

    public void markDirty() {
        if (savedData != null)
            savedData.setDirty();
    }

    public TransitNetworkManager sided(LevelAccessor level) {
        if (level != null && !level.isClientSide())
            return this;
        MutableObject<TransitNetworkManager> m = new MutableObject<>();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientManager(m));
        return m.getValue();
    }

    @OnlyIn(Dist.CLIENT)
    private void clientManager(MutableObject<TransitNetworkManager> m) {
        m.setValue(TransitRouteClient.ROUTES);
    }
}
