package com.library.impl;

import com.library.exception.CityHasLibrariesException;
import com.library.exception.DuplicateEntryException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.City;
import com.library.payload.request.CityRequest;
import com.library.payload.response.CityResponse;
import com.library.repository.CityRepository;
import com.library.repository.LibraryRepository;
import com.library.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LibraryRepository libraryRepository;

    @Override
    @Transactional
    public CityResponse createCity(CityRequest cityRequest) {
        City city = new City();
        city.setName(cityRequest.getCityName());
        city.setCreatedAt(LocalDate.now());

        if (cityRepository.existsByName(city.getName())) {
            throw new DuplicateEntryException("City with the same name already exists");
        }

        city = cityRepository.save(city);
        return mapToCityResponse(city);
    }

    @Override
    @Transactional
    public CityResponse getCityById(Long id) {
        City user = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found"));
        return mapToCityResponse(user);
    }

    @Override
    @Transactional
    public List<CityResponse> getAllCities() {
        List<City> users = cityRepository.findAllByIsActiveOrderByNameAsc(true);
        return users.stream().map(this::mapToCityResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CityResponse updateCity(Long id, CityRequest cityRequest) {
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found"));
        city.setName(cityRequest.getCityName());
        city.setUpdatedAt(LocalDate.now());
        city = cityRepository.save(city);
        return mapToCityResponse(city);
    }

    @Override
    @Transactional
//    public void deleteCity(Long id) {
//        City user = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found"));
//        cityRepository.delete(user);
//    }
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found"));

//        boolean hasLibraries = libraryRepository.existsByCity(city);
//        if (hasLibraries) {
//            throw new CityHasLibrariesException("Cannot delete city as it has associated libraries");
//        }

//        cityRepository.delete(city);
        city.setActive(false);
        cityRepository.save(city);
    }

    @Transactional
    private CityResponse mapToCityResponse(City city) {
        CityResponse cityResponse = new CityResponse();
        cityResponse.setId(city.getId());
        cityResponse.setName(city.getName());
        cityResponse.setCreatedAt(city.getCreatedAt());
        return cityResponse;
    }
}
