package plus.dragons.createtransitroute.content.logistics.transit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelAccessor;
import org.slf4j.Logger;
import plus.dragons.createtransitroute.TransitRoute;


import java.util.*;
import java.util.function.Function;

public class TransitLine {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID id;
    private final Pair<String,String> names;
    private String code;
    private String color;
    private final Map<UUID,Segment> segments;
    private final UUID owner;
    private Ownership ownership;

    public TransitLine(String name, String color, UUID owner) {
        this.id = UUID.randomUUID();
        this.names = Pair.of(name,"");
        this.code = "";
        this.color = color;
        this.segments = new HashMap<>();
        this.owner = owner;
        this.ownership = Ownership.PRIVATE;
    }

    private TransitLine(UUID id, String name, String translatedName, String code, String color, UUID owner, Ownership ownership) {
        this.id = id;
        this.names = Pair.of(name,translatedName);
        this.code = code;
        this.color = color;
        this.segments = new HashMap<>();
        this.owner = owner;
        this.ownership = ownership;
    }

    private void addAllSegments(List<Segment> segments){
        for(var segment:segments){
            this.segments.put(segment.id,segment);
        }
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

    private Segment createSegmentFromTag(CompoundTag tag){
        return new Segment(tag);
    }

    private Segment createLineSegment(String name){
        var ret = new Segment(name);
        segments.put(ret.id,ret);
        return ret;
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

    public class Segment {
        private final UUID id;
        private final Pair<String,String> names;
        private final List<Pair<UUID,UUID>> stations;
        private boolean maintaining;
        private boolean emergency;
        private Function<LevelAccessor, List<TransitStation.Platform>> platformCache;

        private Segment(String name) {
            this.id = UUID.randomUUID();
            this.names = Pair.of(name,"");
            this.stations = new ArrayList<>();
            this.maintaining = true;
            this.emergency = false;
            flushPlatformsCache();
        }

        public Segment(CompoundTag tag) {
            this.id = tag.getUUID("Id");
            this.names = Pair.of(tag.getString("Name"),tag.getString("TranslatedName"));
            this.stations = NBTHelper.readCompoundList(tag.getList("Stations", Tag.TAG_COMPOUND),
                    compoundTag -> Pair.of(compoundTag.getUUID("ID"),compoundTag.getUUID("Platform")));
            this.maintaining = tag.getBoolean("Maintaining");
            this.emergency = tag.getBoolean("Emergency");
            flushPlatformsCache();
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

        private void flushPlatformsCache(){
            this.platformCache = new Function<>() {
                final List<TransitStation.Platform> cache = new ArrayList<>();
                boolean initialized = false;

                @Override
                public List<TransitStation.Platform> apply(LevelAccessor level) {
                    if (!initialized) {
                        for(var pair:stations){
                            var station = TransitRoute.ROUTES.sided(level).stations.values().stream()
                                    .filter(station1 -> station1.getId().equals(pair.getFirst())).findFirst().orElse(null);
                            if (station == null) {
                                LOGGER.error("Cannot find Station by UUID " + pair.getFirst() + " in all stations, something must goes wrong! This station will be simply skipped!");
                                continue;
                            }
                            var platform = station.getPlatforms().stream()
                                    .filter(platform1 -> platform1.getId().equals(pair.getSecond())).findFirst().orElse(null);
                            if (platform == null) {
                                LOGGER.error("Cannot find Platform by UUID " + pair.getSecond() + " in all platforms, something must goes wrong! This platform will be simply skipped!");
                                continue;
                            }
                            cache.add(platform);
                        }
                        initialized = true;
                    }
                    return cache;
                }
            };

        }

        public List<TransitStation.Platform> getPlatforms(LevelAccessor levelAccessor){
            return platformCache.apply(levelAccessor);
        }

        public boolean attachPlatform(UUID stationID,UUID platformID){
            this.stations.add(Pair.of(stationID,platformID));
            flushPlatformsCache();
            return true;
        }

        public void detachPlatform(UUID platformID){
            if(stations.stream().map(Pair::getSecond).toList().contains(platformID)){
                stations.removeIf(pair -> platformID.equals(pair.getSecond()));
                flushPlatformsCache();
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
    }
}
