package com.salankiv.cicmiscanner.service;

import com.salankiv.cicmiscanner.repository.IataAirportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AirportFinder {

    @Autowired
    IataAirportRepo iataAirportRepo;

    public String findAirport(String code) {
        try {
            return iataAirportRepo.findByCode(code).getAirport();
        } catch (NullPointerException e) {
            return code;
        }

    }
}
