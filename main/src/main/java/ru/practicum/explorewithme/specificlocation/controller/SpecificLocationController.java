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
    public List<SpecificLocation> getLocations(@RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        return specificLocationService.getLocations(from, size);
    }

    @PostMapping("/users/{userId}/locations")
    public SpecificLocation userAddLocation(@PathVariable Long userId,
                                            @RequestBody @Valid SpecificLocation specificLocation) {
        return specificLocationService.userAddLocation(userId, specificLocation);
    }

    @GetMapping("/admin/locations/{locId}/approve")
    public void approveLocation(@PathVariable Long locId) {
        specificLocationService.approveLocation(locId);
    }

    @GetMapping("/admin/locations/{locId}/reject")
    public void rejectLocation(@PathVariable Long locId) {
        specificLocationService.rejectLocation(locId);
    }

    @PatchMapping("/admin/locations/{locId}")
    public SpecificLocation updateLocation(@PathVariable Long locId,
                                           @RequestBody SpecificLocationUpdate specificLocationUpdate) {
        return specificLocationService.updateLocation(locId, specificLocationUpdate);
    }
}
