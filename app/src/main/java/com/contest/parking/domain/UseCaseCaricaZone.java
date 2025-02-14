package com.contest.parking.domain;

import android.content.res.AssetManager;
import com.contest.parking.data.model.Zone;
import com.contest.parking.data.repository.ZoneRepository;

import java.util.List;

public class UseCaseCaricaZone {

    private ZoneRepository zoneRepository;

    public UseCaseCaricaZone(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public List<Zone> execute(String luogoId, AssetManager assetManager) {
        return zoneRepository.getZonesForLuogo(luogoId, assetManager);
    }
}
