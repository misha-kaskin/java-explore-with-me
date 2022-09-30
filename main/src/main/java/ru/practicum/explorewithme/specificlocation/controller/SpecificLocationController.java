package ru.practicum.explorewithme.specificlocation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocation;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocationUpdate;
import ru.practicum.explorewithme.specificlocation.service.SpecificLocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SpecificLocationController {
    private final SpecificLocationService specificLocationService;

    @PostMapping("/admin/locations")
    public SpecificLocation addLocation(@RequestBody @Valid SpecificLocation specificLocation) {
        return specificLocationService.addLocation(specificLocation);
    }

    @GetMapping("/admin/locations")
    public List<SpecificLocation> getLocations() {
        return specificLocationService.getLocations();
    }

    @PostMapping("/users/{userId}/locations")
    public SpecificLocation userAddLocation(@PathVariable Long userId,
                                            @RequestBody @Valid SpecificLocation specificLocation) {
        return specificLocationService.userAddLocation(userId, specificLocation);
    }

    @PatchMapping("/admin/locations/{locId}/approve")
    public SpecificLocation approveLocation(@PathVariable Long locId) {
        return specificLocationService.approveLocation(locId);
    }

    @PatchMapping("/admin/locations/{locId}/reject")
    public SpecificLocation rejectLocation(@PathVariable Long locId) {
        return specificLocationService.rejectLocation(locId);
    }

    @PatchMapping("/admin/locations/{locId}")
    public SpecificLocation updateLocation(@PathVariable Long locId,
                                           @RequestBody SpecificLocationUpdate specificLocationUpdate) {
        return specificLocationService.updateLocation(locId, specificLocationUpdate);
    }
}
