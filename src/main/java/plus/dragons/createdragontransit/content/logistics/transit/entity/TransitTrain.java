package plus.dragons.createdragontransit.content.logistics.transit.entity;

import com.simibubi.create.content.logistics.trains.TrackGraph;
import com.simibubi.create.content.logistics.trains.entity.Carriage;
import com.simibubi.create.content.logistics.trains.entity.Train;

import java.util.List;
import java.util.UUID;

public class TransitTrain extends Train {
    public TransitTrain(UUID id, UUID owner, TrackGraph graph, List<Carriage> carriages, List<Integer> carriageSpacing, boolean doubleEnded) {
        super(id, owner, graph, carriages, carriageSpacing, doubleEnded);
    }
}
