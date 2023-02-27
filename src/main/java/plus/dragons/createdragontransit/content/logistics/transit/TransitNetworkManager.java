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
import plus.dragons.createdragontransit.entry.CtrPackets;

import java.util.*;

/**
 * A Transit Manager holding and handling all transit lines & transit stations. <br><br>
 *
 * {@link TransitStation} and {@link TransitLine} are merely data structures for holding information,
 * thus any direct editing on them will not be synced automatically
 * and any bind & unbind action between {@link TransitStation.Platform} & {@link TransitLine.Segment}
 * does not affect corresponding target (bind & unbind action should be done in both side).<br><br>
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

    public UUID createLine(){

        markDirty();
    }

    @Nullable
    public UUID createLineSegment(UUID lineID){

        markDirty();
    }

    public UUID createStation(){

        markDirty();
    }

    @Nullable
    public UUID createStationPlatform(UUID stationID){

        markDirty();
    }

    public void deleteLine(UUID id){

        markDirty();
    }

    public void deleteLineSegment(UUID lineID, UUID id){

        markDirty();
    }

    public void deleteStation(UUID id){

        markDirty();
    }

    public void deleteStationPlatform(UUID stationID, UUID id){

        markDirty();
    }

    public boolean bindPlatformTo(UUID stationID, UUID PlatformID, UUID lineID, UUID segmentID){

        markDirty();
    }

    public void unbindPlatformTo(UUID stationID, UUID PlatformID, UUID lineID, UUID segmentID){

        markDirty();
    }

    public void syncStation(TransitStation station){
        var packet = TransitNetworkSyncPacket.updateStation(station);
        CtrPackets.channel.send(PacketDistributor.ALL.noArg(), packet);
        markDirty();
    }

    public void syncLine(TransitLine line){
        var packet = TransitNetworkSyncPacket.updateLine(line);
        CtrPackets.channel.send(PacketDistributor.ALL.noArg(), packet);
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
            CtrPackets.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
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
