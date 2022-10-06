package ru.practicum.explorewithme.specificlocation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocation;
import ru.practicum.explorewithme.specificlocation.model.SpecificLocationUpdate;
import ru.practicum.explorewithme.specificlocation.model.Status;
import ru.practicum.explorewithme.specificlocation.storage.SpecificLocationRepository;
import ru.practicum.explorewithme.users.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecificLocationServiceImpl implements SpecificLocationService {
    private final SpecificLocationRepository specificLocationRepository;
    private final UserService userService;

    @Override
    @Transactional
    public SpecificLocation addLocation(SpecificLocation specificLocation) {
        if (specificLocationRepository.existsByName(specificLocation.getName())) {
            throw new ValidationException("Такая локация уже существует");
        }

        if (specificLocation.getId() != null) {
            throw new ValidationException("Передан непустой идентификатор");
        }

        specificLocation.setStatus(Status.APPROVED);

        return specificLocationRepository.save(specificLocation);
    }

    @Override
    public List<SpecificLocation> getLocations(Integer from, Integer size) {
        return specificLocationRepository.findAll(PageRequest.of(from, size)).toList();
    }

    @Override
    @Transactional
    public SpecificLocation userAddLocation(Long userId, SpecificLocation specificLocation) {
        if (userService.findUsers(List.of(userId), 0, 1).isEmpty()) {
            throw new NotFoundException("Пользователь с заданным id не найден");
        }

        if (specificLocationRepository.existsByName(specificLocation.getName())) {
            throw new ValidationException("Такая локация уже существует");
        }

        specificLocation.setStatus(Status.WAITING);

        return specificLocationRepository.save(specificLocation);
    }

    @Override
    @Transactional
    public void approveLocation(Long locId) {
        Optional<SpecificLocation> optionalSpecificLocation = specificLocationRepository.findById(locId);

        if (!optionalSpecificLocation.isPresent()) {
            throw new NotFoundException("Локация не найдена");
        }

        SpecificLocation specificLocation = optionalSpecificLocation.get();

        if (!specificLocation.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Локация не может быть одобрена");
        }

        specificLocation.setStatus(Status.APPROVED);
    }

    @Override
    @Transactional
    public void rejectLocation(Long locId) {
        Optional<SpecificLocation> optionalSpecificLocation = specificLocationRepository.findById(locId);

        if (!optionalSpecificLocation.isPresent()) {
            throw new NotFoundException("Локация не найдена");
        }

        SpecificLocation specificLocation = optionalSpecificLocation.get();

        if (!specificLocation.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Локация не может быть одобрена");
        }

        specificLocation.setStatus(Status.REJECTED);
    }

    @Override
    @Transactional
    public SpecificLocation updateLocation(Long locId, SpecificLocationUpdate specificLocationUpdate) {
        Optional<SpecificLocation> optionalSpecificLocation = specificLocationRepository.findById(locId);

        if (!optionalSpecificLocation.isPresent()) {
            throw new NotFoundException("Локация не найдена");
        }

        SpecificLocation specificLocation = optionalSpecificLocation.get();

        if (specificLocation.getStatus().equals(Status.REJECTED)) {
            throw new ValidationException("Событие не может быть изменено");
        }

        if (specificLocationUpdate.getName() != null) {
            if (specificLocationRepository.existsByName(specificLocationUpdate.getName())) {
                throw new NotFoundException("Такое имя уже существует");
            }

            specificLocation.setName(specificLocationUpdate.getName());
        }

        if (specificLocationUpdate.getLat() != null) {
            specificLocation.setLat(specificLocationUpdate.getLat());
        }

        if (specificLocationUpdate.getLon() != null) {
            specificLocation.setLon(specificLocationUpdate.getLon());
        }

        if (specificLocationUpdate.getRadius() != null) {
            specificLocation.setRadius(specificLocationUpdate.getRadius());
        }

        return specificLocationRepository.save(specificLocation);
    }
}
