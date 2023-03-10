package plus.dragons.createdragontransit.content.logistics.transit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createdragontransit.DragonTransit;
import plus.dragons.createdragontransit.DragonTransitClient;
import plus.dragons.createdragontransit.entry.CdtPackets;

import java.util.*;

/**
 * A Transit Manager holding and handling all transit lines & transit stations. <br><br>
 *
 * Please use {@link #syncLine(TransitLine)} and {@link #syncStation(TransitStation)} after direct editing.
 * For other creation/deletion/binding/unbinding actions, please use methods in {@link TransitNetworkManager} and manual syncing is not required.
 */

public class TransitNetworkManager {
    public TransitNetwork network = new TransitNetwork();
    RoutesSavedData savedData;

    public TransitNetworkManager() {
        cleanUp();
    }

    public UUID createLine(UUID creator){
        var line = new TransitLine(creator);
        network.lines.put(line.getId(),line);
        markDirty();
        return line.getId();
    }

    @Nullable
    public UUID createLineSegment(UUID lineID){
        var line = network.lines.get(lineID);
        if(line==null) return null;
        var segment = line.createLineSegment();
        markDirty();
        return segment.getId();
    }

    public UUID createStation(String name, String translatedName, UUID creator){
        var station = new TransitStation(name,translatedName,creator);
        network.stations.put(station.getId(),station);
        markDirty();
        return station.getId();
    }

    public boolean deleteLine(UUID id){
        if(network.lines.remove(id)!=null) {
            markDirty();
            return true;
        }
        return false;
    }

    public boolean deleteLineSegment(UUID lineID, UUID id){
        var line = network.lines.get(lineID);
        if(line==null) return false;
        if(line.removeSegment(id)!=null){
            markDirty();
            return true;
        }
        return false;
    }

    public boolean deleteStation(UUID id){
        if(network.stations.remove(id)!=null){
            markDirty();
            return true;
        }
        return false;
    }

    public boolean bindPlatformToLineSegment(UUID stationID, UUID platformID, UUID lineID, UUID segmentID){
        var line = network.lines.get(lineID);
        if(line==null) return false;
        var segment = line.getSegment(segmentID);
        if(segment==null) return false;
        var station = network.stations.get(stationID);
        if(station==null) return false;
        if(!station.containsPlatform(platformID)) return false;
        if(segment.attachPlatform(stationID,platformID)){
            markDirty();
            syncLine(line);
            return true;
        }
        return false;
    }

    public boolean unbindPlatformFromLineSegment(UUID stationID, UUID platformID, UUID lineID, UUID segmentID){
        var line = network.lines.get(lineID);
        if(line==null) return false;
        var segment = line.getSegment(segmentID);
        if(segment==null) return false;
        var station = network.stations.get(stationID);
        if(station==null) return false;
        if(!station.containsPlatform(platformID)) return false;
        if(segment.detachPlatform(stationID,platformID)){
            markDirty();
            syncLine(line);
            return true;
        }
        return false;
    }

    public boolean addPlatformToStation(UUID stationID, UUID platformID){
        var station = network.stations.get(stationID);
        if(station==null) return false;
        station.addPlatform(platformID);
        syncStation(station);
        markDirty();
        return true;
    }

    public boolean removePlatformFromStation(UUID stationID, UUID platformID){
        var station = network.stations.get(stationID);
        if(station==null) return false;
        if(station.removePlatform(platformID)){
            syncStation(station);
            markDirty();
            return true;
        }
        return false;
    }

    public void syncStation(TransitStation station){
        var packet = TransitNetworkSyncPacket.updateStation(station);
        CdtPackets.channel.send(PacketDistributor.ALL.noArg(), packet);
        markDirty();
    }

    public void syncLine(TransitLine line){
        var packet = TransitNetworkSyncPacket.updateLine(line);
        CdtPackets.channel.send(PacketDistributor.ALL.noArg(), packet);
        markDirty();
    }

    public static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        var manager = DragonTransit.ROUTES;
        if (player instanceof ServerPlayer serverPlayer) {
            manager.loadRouteData(serverPlayer.getServer());
            CompoundTag tag = new CompoundTag();
            manager.network.save(tag);
            var packet = new TransitNetworkInitializePacket(tag);
            CdtPackets.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
        }
    }

    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor level = event.getLevel();
        var manager = DragonTransit.ROUTES;
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        manager.cleanUp();
        manager.savedData = null;
        manager.loadRouteData(server);
    }

    private void loadRouteData(MinecraftServer server) {
        if (savedData != null)
            return;
        savedData = RoutesSavedData.load(server);
        network = savedData.network;
    }

    public void cleanUp() {
        network = new TransitNetwork();
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
        m.setValue(DragonTransitClient.ROUTES);
    }
}
