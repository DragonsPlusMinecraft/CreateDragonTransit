package plus.dragons.createtransitroute.content.logistics.transit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.LevelAccessor;
import org.slf4j.Logger;
import plus.dragons.createtransitroute.TransitRoute;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;

public class TransitStation {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UUID id;
    private final Pair<String,String> names;
    private final Map<UUID,Platform> platforms;
    private final UUID owner;
    private boolean isPrivate;
    private Function<LevelAccessor,List<TransitLine.Segment>> linesCache;

    public TransitStation(String name, UUID owner) {
        this.id = UUID.randomUUID();
        this.names = Pair.of(name,"");
        this.platforms = new HashMap<>();
        this.owner = owner;
        this.isPrivate = true;
        flushLinesCache();
    }

    private TransitStation(UUID id, String name, String translatedName, UUID owner, boolean isPrivate) {
        this.id = id;
        this.names = Pair.of(name,translatedName);
        this.platforms = new HashMap<>();
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putUUID("UUID",id);
        ret.putString("Name", names.getFirst());
        ret.putString("TranslatedName", names.getSecond());
        ret.put("Platforms", NBTHelper.writeCompoundList(platforms.values(), Platform::write));
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
        ret.addAllPlatform(NBTHelper.readCompoundList(tag.getList("Platforms", Tag.TAG_COMPOUND), ret::createPlatformFromTag));
        ret.flushLinesCache();
        return ret;
    }

    private void flushLinesCache(){
        this.linesCache = new Function<>() {
            final List<TransitLine.Segment> cache = new ArrayList<>();
            boolean initialized = false;

            @Override
            public List<TransitLine.Segment> apply(LevelAccessor level) {
                if (!initialized) {
                    for(var platform: platforms.values()){
                        var lineSeg = platform.getLineSegment(level);
                        if(lineSeg!=null)
                            cache.add(platform.getLineSegment(level));
                    }
                    initialized = true;
                }
                return cache;
            }
        };

    }

    private void createPlatform(){
        var in = new Platform();
        platforms.put(in.id,in);
    }

    @Nullable
    public Platform getPlatform(UUID platformID){
        return platforms.get(platformID);
    }

    public void removePlatform(UUID platformID){
        platforms.remove(platformID);
        flushLinesCache();
    }


    private void addAllPlatform(List<Platform> platforms){
        platforms.forEach(platform -> this.platforms.put(platform.id,platform));
    }

    private Platform createPlatformFromTag(CompoundTag tag){
        return new Platform(tag);
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

    public List<Platform> getPlatforms() {
        return platforms.values().stream().toList();
    }

    public List<TransitLine.Segment> getAllLineSegments(LevelAccessor level){
        return linesCache.apply(level);
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

    public class Platform {
        private final UUID id;
        private boolean maintaining;
        @Nullable
        private Pair<UUID,UUID> line = null;
        private Function<LevelAccessor, TransitLine.Segment> lineCache;

        private Platform() {
            this.id = UUID.randomUUID();
            this.maintaining = false;
            flushLineCache();
        }

        private Platform(CompoundTag tag) {
            this.id = tag.getUUID("ID");
            this.maintaining = tag.getBoolean("Maintaining");
            if(tag.contains("Line")){
                this.line = Pair.of(tag.getUUID("Line"),tag.getUUID("Segment"));
            } else this.line =  null;
            flushLineCache();
        }

        public CompoundTag write(){
            CompoundTag ret = new CompoundTag();
            ret.putUUID("ID",id);
            ret.putBoolean("Maintaining",maintaining);
            if(line!=null){
                ret.putUUID("Line",line.getFirst());
                ret.putUUID("Segment",line.getSecond());
            }
            return ret;
        }

        private void flushLineCache(){
            this.lineCache = new Function<>() {
                WeakReference<TransitLine.Segment> cache = null;

                @Nullable
                @Override
                public TransitLine.Segment apply(LevelAccessor level) {
                    if (line == null)
                        return null;
                    if (cache == null) {
                        var retrieved = TransitRoute.ROUTES.sided(level).lines.values().stream().flatMap(line -> line.getSegments().stream())
                                .filter(segment -> segment.getId().equals(line.getSecond())).findFirst().orElse(null);
                        if (retrieved == null) {
                            LOGGER.error("Cannot find LineSegment instance by UUID " + line.getSecond() + " in all Lines, something must goes wrong!");
                            return null;
                        }
                        cache = new WeakReference<>(retrieved);
                    }
                    return cache.get();
                }
            };

        }

        public boolean isMaintaining() {
            return maintaining;
        }

        public void setMaintaining(boolean maintaining) {
            this.maintaining = maintaining;
        }

        public UUID getId() {
            return id;
        }

        @Nullable
        public TransitLine.Segment getLineSegment(LevelAccessor level) {
            return lineCache.apply(level);
        }

        public TransitStation getStation(){
            return TransitStation.this;
        }

        public boolean bindLineSegment(UUID lineID, UUID segmentID){
            if(this.line!=null) return false;
            this.line = Pair.of(lineID,segmentID);
            flushLineCache();
            return true;
        }

        public void unbindLineSegment(){
            this.line = null;
            flushLineCache();
        }
    }


}
