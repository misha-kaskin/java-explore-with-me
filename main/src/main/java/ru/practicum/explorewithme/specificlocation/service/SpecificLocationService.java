package ru.practicum.explorewithme.specificlocation.service;

import ru.practicum.explorewithme.specificlocation.model.SpecificLocation;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocationUpdate;

import java.util.List;

public interface SpecificLocationService {
    SpecificLocation addLocation(SpecificLocation specificLocation);

    List<SpecificLocation> getLocations(Integer from, Integer size);

    SpecificLocation userAddLocation(Long userId, SpecificLocation specificLocation);

    void approveLocation(Long locId);

    void rejectLocation(Long locId);

    SpecificLocation updateLocation(Long locId, SpecificLocationUpdate specificLocationUpdate);
}
