package plus.dragons.createtransitroute.content.logistics.routes;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GlobalTransitLine {
    public UUID id;
    public String name;
    public String code;
    public String color;
    public Mode mode;
    public List<UUID> stations;
    public UUID owner;
    public Ownership ownership;

    public GlobalTransitLine(String name, String color, UUID owner) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.code = "";
        this.color = color;
        this.mode = Mode.LINEAR_ROUND_TRIP;
        this.stations = new ArrayList<>();
        this.owner = owner;
        this.ownership = Ownership.PRIVATE;
    }

    private GlobalTransitLine(UUID id,String name, String code, String color, Mode mode, List<UUID> stations, UUID owner, Ownership ownership) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.color = color;
        this.mode = mode;
        this.stations = stations;
        this.owner = owner;
        this.ownership = ownership;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putUUID("UUID",id);
        ret.putString("Name",name);
        ret.putString("Code",code);
        ret.putString("Color",color);
        NBTHelper.writeEnum(ret,"Mode",mode);
        ret.put("Stations", NBTHelper.writeCompoundList(stations, uuid -> {
            var tag = new CompoundTag();
            tag.putUUID("$",uuid);
            return tag;

        }));
        ret.putUUID("Owner",owner);
        NBTHelper.writeEnum(ret,"Ownership",ownership);
        return ret;
    }

    public static GlobalTransitLine read(CompoundTag tag){
        var id = tag.getUUID("UUID");
        var name = tag.getString("Name");
        var code = tag.getString("Code");
        var color = tag.getString("Color");
        var mode = NBTHelper.readEnum(tag,"Mode",Mode.class);
        List<UUID> stations = NBTHelper.readCompoundList(tag.getList("Stations", Tag.TAG_COMPOUND), compoundTag -> compoundTag.getUUID("$"));
        var owner = tag.getUUID("Owner");
        var ownership = NBTHelper.readEnum(tag,"Ownership",Ownership.class);
        return new GlobalTransitLine(id,name,code,color,mode,stations,owner,ownership);
    }


    public enum Mode{
        LINEAR_ONE_WAY,
        LINEAR_ROUND_TRIP,
        CIRCULAR,
        CIRCULAR_BIDIRECTIONAL
    }

    public enum Ownership{
        PUBLIC,
        PRIVATE,
        SECRET
    }
}
