package plus.dragons.createdragontransit.content.logistics.transit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


import java.util.*;

public class TransitLine {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID id;
    private final Couple<String> names;
    private String code;
    private String color;
    private final Map<UUID,Segment> segments;
    private final UUID owner;
    private Ownership ownership;

    public TransitLine(UUID owner) {
        this.id = UUID.randomUUID();
        this.names = Couple.create("New Line","");
        this.code = "";
        var random = new Random();
        char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] s = new char[7];
        int n = random.nextInt(0x1000000);
        s[0] = '#';
        for (int i=1;i<7;i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        this.color = new String(s);
        this.segments = new HashMap<>();
        this.owner = owner;
        this.ownership = Ownership.PRIVATE;
    }

    private TransitLine(UUID id, String name, String translatedName, String code, String color, UUID owner, Ownership ownership) {
        this.id = id;
        this.names = Couple.create(name,translatedName);
        this.code = code;
        this.color = color;
        this.segments = new HashMap<>();
        this.owner = owner;
        this.ownership = ownership;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putUUID("UUID",id);
        ret.putString("Name", names.getFirst());
        ret.putString("TranslatedName", names.getSecond());
        ret.putString("Code",code);
        ret.putString("Color",color);
        ret.putUUID("Owner",owner);
        NBTHelper.writeEnum(ret,"Ownership",ownership);
        ret.put("Segments", NBTHelper.writeCompoundList(segments.values(), Segment::write));
        return ret;
    }

    public static TransitLine read(CompoundTag tag){
        var id = tag.getUUID("UUID");
        var name = tag.getString("Name");
        var translatedName = tag.getString("TranslatedName");
        var code = tag.getString("Code");
        var color = tag.getString("Color");
        var owner = tag.getUUID("Owner");
        var ownership = NBTHelper.readEnum(tag,"Ownership",Ownership.class);
        var ret = new TransitLine(id,name,translatedName,code,color,owner,ownership);
        ret.addAllSegments(NBTHelper.readCompoundList(tag.getList("Segments", Tag.TAG_COMPOUND), ret::createSegmentFromTag));
        return ret;
    }

    Segment createLineSegment(){
        var ret = new Segment("New Segment");
        segments.put(ret.id,ret);
        return ret;
    }

    Segment createSegmentFromTag(CompoundTag tag){
        var id = tag.getUUID("Id");
        var names = Couple.create(tag.getString("Name"),tag.getString("TranslatedName"));
        var stations = NBTHelper.readCompoundList(tag.getList("Stations", Tag.TAG_COMPOUND),
                compoundTag -> Couple.create(compoundTag.getUUID("ID"),compoundTag.getUUID("Platform")));
        var maintaining = tag.getBoolean("Maintaining");
        var emergency = tag.getBoolean("Emergency");
        return new Segment(id,names,stations,maintaining,emergency);
    }

    void updateSegmentFromTag(CompoundTag tag){
        var id = tag.getUUID("Id");
        segments.put(id,createSegmentFromTag(tag));
    }

    @Nullable
    Segment getSegment(UUID segmentID){
        return segments.get(segmentID);
    }

    @Nullable
    Segment removeSegment(UUID segmentID){
        return segments.remove(segmentID);
    }

    private void addAllSegments(List<Segment> segments){
        for(var segment:segments){
            this.segments.put(segment.id,segment);
        }
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

    public String getCode() {
        return code;
    }

    public String getColor() {
        return color;
    }

    public List<Segment> getSegments() {
        return segments.values().stream().toList();
    }

    public UUID getOwner() {
        return owner;
    }

    public Ownership getOwnership() {
        return ownership;
    }

    public void setName(String name) {
        this.names.setFirst(name);
    }

    public void setTranslatedName(String name) {
        this.names.setSecond(name);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOwnership(Ownership ownership) {
        this.ownership = ownership;
    }

    public enum Ownership{
        PUBLIC,
        PRIVATE,
        SECRET
    }

    public class Segment{
        private final UUID id;
        private final Couple<String> names;
        private final List<Couple<UUID>> stations;
        private boolean maintaining;
        private boolean emergency;

        private Segment(String name) {
            this.id = UUID.randomUUID();
            this.names = Couple.create(name,"");
            this.stations = new ArrayList<>();
            this.maintaining = true;
            this.emergency = false;
        }

        public Segment(UUID id, Couple<String> names, List<Couple<UUID>> stations, boolean maintaining, boolean emergency) {
            this.id = id;
            this.names = names;
            this.stations = stations;
            this.maintaining = maintaining;
            this.emergency = emergency;
        }

        public boolean attachPlatform(UUID stationID, UUID platformID){
            if(stations.contains(Couple.create(stationID,platformID)))
                return false;
            this.stations.add(Couple.create(stationID,platformID));
            return true;
        }

        public boolean detachPlatform(UUID stationID, UUID platformID){
            if(stations.contains(Couple.create(stationID,platformID))){
                stations.removeIf(couple -> platformID.equals(couple.getSecond()));
                return true;
            }
            return false;
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

        public void setMaintaining() {
            this.maintaining = true;
        }

        public boolean isMaintaining() {
            return maintaining;
        }

        public boolean stopMaintaining() {
            if(emergency) return false;
            maintaining = false;
            return true;
        }

        public boolean isEmergency() {
            return emergency;
        }

        public void setName(String name) {
            this.names.setFirst(name);
        }

        public void setTranslatedName(String name) {
            this.names.setSecond(name);
        }

        public void emergency() {
            this.maintaining = true;
            this.emergency = true;
        }

        public void stopEmergency(){
            this.emergency = false;
        }

        public TransitLine getLine(){
            return TransitLine.this;
        }

        public CompoundTag write(){
            CompoundTag ret = new CompoundTag();
            ret.putUUID("Id",id);
            ret.putString("Name",names.getFirst());
            ret.putString("TranslatedName",names.getSecond());
            ret.putBoolean("Maintaining",maintaining);
            ret.putBoolean("Emergency",emergency);
            ret.put("Stations", NBTHelper.writeCompoundList(stations, pair -> {
                var tag = new CompoundTag();
                tag.putUUID("ID",pair.getFirst());
                tag.putUUID("Platform",pair.getSecond());
                return tag;
            }));
            return ret;
        }
    }
}
