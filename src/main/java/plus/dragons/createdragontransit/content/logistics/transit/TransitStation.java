package plus.dragons.createdragontransit.content.logistics.transit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


public class TransitStation {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID id;
    private final Couple<String> names;
    private final List<Pair<UUID,Boolean>> platforms;
    private final UUID owner;
    private boolean isPrivate;

    public TransitStation(String name, String translatedName, UUID owner) {
        this.id = UUID.randomUUID();
        this.names = Couple.create(name,translatedName);
        this.platforms = new ArrayList<>();
        this.owner = owner;
        this.isPrivate = true;
    }

    private TransitStation(UUID id, String name, String translatedName, UUID owner, boolean isPrivate) {
        this.id = id;
        this.names = Couple.create(name,translatedName);
        this.platforms = new ArrayList<>();
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putUUID("UUID",id);
        ret.putString("Name", names.getFirst());
        ret.putString("TranslatedName", names.getSecond());
        ret.put("Platforms", NBTHelper.writeCompoundList(platforms, p->{
            CompoundTag tag = new CompoundTag();
            tag.putUUID("PlatformID",p.getFirst());
            tag.putBoolean("Occupied",p.getSecond());
            return tag;
        }));
        ret.putUUID("Owner",owner);
        ret.putBoolean("IsPrivate",isPrivate);
        return ret;
    }

    public static TransitStation read(CompoundTag tag){
        var id = tag.getUUID("UUID");
        var name = tag.getString("Name");
        var translatedName = tag.getString("TranslatedName");
        var owner = tag.getUUID("Owner");
        var isPrivate = tag.getBoolean("IsPrivate");
        var ret = new TransitStation(id,name,translatedName,owner,isPrivate);
        ret.addAllPlatform(NBTHelper.readCompoundList(tag.getList("Platforms", Tag.TAG_COMPOUND), compoundTag ->
                Pair.of(compoundTag.getUUID("PlatformID"),compoundTag.getBoolean("Occupied"))));
        return ret;
    }

    boolean removePlatform(UUID platformID){
        return platforms.removeIf(p->p.getFirst().equals(platformID));
    }

    void addPlatform(UUID platformID){
        platforms.add(Pair.of(platformID,false));
    }

    boolean containsPlatform(UUID platformID){
        return platforms.stream().map(Pair::getFirst).collect(Collectors.toList()).contains(platformID);
    }

    private void addAllPlatform(List<Pair<UUID,Boolean>> platforms){
        this.platforms.addAll(platforms);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return names.getFirst();
    }

    public String getTranslatedName() {
        return names.getSecond();
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setName(String name) {
        this.names.setFirst(name);
    }

    public void setTranslatedName(String name) {
        this.names.setSecond(name);
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
