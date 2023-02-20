package plus.dragons.createtransitroute.content.logistics.transit;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.utility.NBTHelper;
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
    private String name;
    private final List<StationPlatform> platforms;
    private final UUID owner;
    private boolean isPrivate;
    private Function<LevelAccessor,List<TransitLine.LineSegment>> linesCache;

    public TransitStation(String name, UUID owner) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.platforms = new ArrayList<>();
        this.owner = owner;
        this.isPrivate = true;
        flushLinesCache();
    }

    private TransitStation(UUID id, String name, UUID owner, boolean isPrivate) {
        this.id = id;
        this.name = name;
        this.platforms = new ArrayList<>();
        this.owner = owner;
        this.isPrivate = isPrivate;
    }

    public CompoundTag write(){
        CompoundTag ret = new CompoundTag();
        ret.putUUID("UUID",id);
        ret.putString("Name",name);
        ret.put("Platforms", NBTHelper.writeCompoundList(platforms, StationPlatform::write));
        ret.putUUID("Owner",owner);
        ret.putBoolean("IsPrivate",isPrivate);
        return ret;
    }

    public static TransitStation read(CompoundTag tag){
        var id = tag.getUUID("UUID");
        var name = tag.getString("Name");
        var owner = tag.getUUID("Owner");
        var isPrivate = tag.getBoolean("IsPrivate");
        var ret = new TransitStation(id,name,owner,isPrivate);
        ret.addAllPlatform(NBTHelper.readCompoundList(tag.getList("Platforms", Tag.TAG_COMPOUND), ret::createPlatformFromTag));
        ret.flushLinesCache();
        return ret;
    }

    private void flushLinesCache(){
        this.linesCache = new Function<>() {
            final List<TransitLine.LineSegment> cache = new ArrayList<>();
            boolean initialized = false;

            @Override
            public List<TransitLine.LineSegment> apply(LevelAccessor level) {
                if (!initialized) {
                    for(var platform: platforms){
                        cache.add(platform.getLineSegment(level));
                    }
                    initialized = true;
                }
                return cache;
            }
        };

    }

    private void addAllPlatform(List<StationPlatform> platforms){
        this.platforms.addAll(platforms);
    }

    private void createPlatform(String name){
        platforms.add(new StationPlatform(name));
    }

    private StationPlatform createPlatformFromTag(CompoundTag tag){
        return new StationPlatform(tag);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StationPlatform> getPlatforms() {
        return platforms;
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public class StationPlatform{
        private final UUID id;
        private String name;
        private boolean maintaining;
        @Nullable
        private UUID line = null;
        private Function<LevelAccessor,TransitLine.LineSegment> lineCache;

        private StationPlatform(String name) {
            this.id = UUID.randomUUID();
            this.name = name;
            this.maintaining = false;
            flushLineCache();
        }
        private StationPlatform(CompoundTag tag) {
            this.id = tag.getUUID("ID");
            this.name = tag.getString("Name");
            this.maintaining = tag.getBoolean("Maintaining");
            this.line = tag.contains("Line")? tag.getUUID("Line"): null;
            flushLineCache();
        }

        public CompoundTag write(){
            CompoundTag ret = new CompoundTag();
            ret.putUUID("ID",id);
            ret.putString("Name",name);
            ret.putBoolean("Maintaining",maintaining);
            if(line!=null) ret.putUUID("Line",line);
            return ret;
        }

        private void flushLineCache(){
            this.lineCache = new Function<>() {
                WeakReference<TransitLine.LineSegment> cache = null;

                @Nullable
                @Override
                public TransitLine.LineSegment apply(LevelAccessor level) {
                    if (line == null)
                        return null;
                    if (cache == null) {
                        var retrieved = TransitRoute.ROUTES.sided(level).lines.values().stream().flatMap(line -> line.getSegments().stream())
                                .filter(segment -> segment.getId().equals(line)).findFirst().orElse(null);
                        if (retrieved == null) {
                            LOGGER.error("Cannot find LineSegment by UUID " + line + " in all Lines, something must goes wrong! LineSegment attached to this platform " + id + " has been detached!");
                            unbindLineSegment();
                            return null;
                        }
                        cache = new WeakReference<>(retrieved);
                    }
                    return cache.get();
                }
            };

        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
        public TransitLine.LineSegment getLineSegment(LevelAccessor level) {
            return lineCache.apply(level);
        }

        public TransitStation getStation(){
            return TransitStation.this;
        }

        public boolean bindLineSegment(UUID line){
            if(this.line!=null) return false;
            this.line = line;
            flushLineCache();
            return true;
        }

        public void unbindLineSegment(){
            this.line = null;
            flushLineCache();
        }

        public void remove(){
            unbindLineSegment();
            TransitStation.this.platforms.remove(this);
            TransitStation.this.flushLinesCache();
        }
    }


}
