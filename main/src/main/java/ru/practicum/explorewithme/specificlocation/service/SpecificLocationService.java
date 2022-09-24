package ru.practicum.explorewithme.specificlocation.service;

import ru.practicum.explorewithme.specificlocation.model.SpecificLocation;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocationUpdate;

import java.util.List;

public interface SpecificLocationService {
    SpecificLocation addLocation(SpecificLocation specificLocation);

    List<SpecificLocation> getLocations();

    SpecificLocation userAddLocation(Long userId, SpecificLocation specificLocation);

    SpecificLocation approveLocation(Long locId);

    SpecificLocation rejectLocation(Long locId);

    SpecificLocation updateLocation(Long locId, SpecificLocationUpdate specificLocationUpdate);
}
