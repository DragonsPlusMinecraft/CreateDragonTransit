package plus.dragons.createtransitroute.content.logistics.transit;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createtransitroute.TransitRouteClient;
import plus.dragons.createtransitroute.entry.CtrPackets;

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
 * For other creation/deletion/binding/unbinding actions, please use methods in {@link TransitRouteManager} and manual syncing is not required.
 */
public class TransitRouteManager {
    public TransitNetwork network = new TransitNetwork();
    RoutesSavedData savedData;

    public TransitRouteManager() {
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

    public void playerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            loadRouteData(serverPlayer.getServer());
            CompoundTag tag = new CompoundTag();
            network.save(tag);
            var packet = new TransitNetworkInitializePacket(tag);
            CtrPackets.channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
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
        network = savedData.network;
    }

    public void cleanUp() {
        network = new TransitNetwork();
        sync = new GlobalRouteSync();
    }

    public void markDirty() {
        if (savedData != null)
            savedData.setDirty();
    }

    public TransitRouteManager sided(LevelAccessor level) {
        if (level != null && !level.isClientSide())
            return this;
        MutableObject<TransitRouteManager> m = new MutableObject<>();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> clientManager(m));
        return m.getValue();
    }

    @OnlyIn(Dist.CLIENT)
    private void clientManager(MutableObject<TransitRouteManager> m) {
        m.setValue(TransitRouteClient.ROUTES);
    }
}
