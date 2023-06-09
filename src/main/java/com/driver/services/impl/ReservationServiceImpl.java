package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        User user;
        ParkingLot parkingLot;
        try{
            user=userRepository3.findById(userId).get();
            parkingLot=parkingLotRepository3.findById(parkingLotId).get();
        }catch (Exception e){
            throw new Exception("Cannot make reservation");
        }
        SpotType currentSpot;
        if(numberOfWheels<=2) currentSpot=SpotType.TWO_WHEELER;
        else if(numberOfWheels<=4) currentSpot=SpotType.FOUR_WHEELER;
        else currentSpot=SpotType.OTHERS;

        boolean spotBooked=false;
        int price=Integer.MAX_VALUE;
        Spot bookedSpot=new Spot();

        List<Spot> spotList=parkingLot.getSpotList();
        for(Spot spot:spotList){
            if(currentSpot.equals(SpotType.TWO_WHEELER)){
                if(spot.getOccupied()==false && (spot.getPricePerHour()*timeInHours)<price) {
                    price=spot.getPricePerHour()*timeInHours;
                    spotBooked=true;
                    bookedSpot=spot;
                }
            }else if(currentSpot.equals(SpotType.FOUR_WHEELER)){
                if(spot.getOccupied()==false && (spot.getPricePerHour()*timeInHours)<price &&(!spot.getSpotType().equals(SpotType.TWO_WHEELER))){
                    price=spot.getPricePerHour()*timeInHours;
                    spotBooked=true;
                    bookedSpot=spot;
                }
            }else {
                if(spot.getOccupied()==false && (spot.getPricePerHour()*timeInHours)<price &&(spot.getSpotType().equals(SpotType.OTHERS))){
                    price=spot.getPricePerHour()*timeInHours;
                    spotBooked=true;
                    bookedSpot=spot;
                }
            }
        }

        if(!spotBooked) throw new Exception("Cannot make reservation");

        bookedSpot.setOccupied(true);

        Reservation reservation=new Reservation();
        reservation.setSpot(bookedSpot);
        reservation.setUser(user);
        reservation.setNumberOfHours(timeInHours);
        user.getReservationList().add(reservation);

        userRepository3.save(user);
        spotRepository3.save(bookedSpot);

        return reservation;


    }
}