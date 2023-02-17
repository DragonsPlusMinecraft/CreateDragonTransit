package plus.dragons.createtransitroute.content.logistics.routes;

import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.UUID;

public class GlobalTransitStation {
    public String name;
    public List<UUID> lines;
    public UUID owner;
    public boolean isPrivate;

    public GlobalTransitStation(String name, List<UUID> lines, UUID owner, boolean isPrivate) {
        this.name = name;
        this.lines = lines;
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putString("Name",name);
        ret.put("Lines", NBTHelper.writeCompoundList(lines, uuid -> {
            var tag = new CompoundTag();
            tag.putUUID("LineUUID",uuid);
            return tag;

        }));
        ret.putUUID("Owner",owner);
        ret.putBoolean("IsPrivate",isPrivate);
        return ret;
    }

    public static GlobalTransitStation read(CompoundTag tag){
       var name = tag.getString("Name");
       List<UUID> lines = NBTHelper.readCompoundList(tag.getList("Lines", Tag.TAG_COMPOUND), compoundTag -> compoundTag.getUUID("LineUUID"));
       var owner = tag.getUUID("Owner");
       var isPrivate = tag.getBoolean("IsPrivate");
       return new GlobalTransitStation(name,lines,owner,isPrivate);
    }

}
