package com.evanditaWiratamaPutraJBusER.jbus_android.request;

import com.evanditaWiratamaPutraJBusER.jbus_android.model.Account;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Renter;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BaseResponse;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Facility;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Bus;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.BusType;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.Station;
import com.evanditaWiratamaPutraJBusER.jbus_android.model.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService {
    @GET("account/{id}")
    Call<Account> getAccountbyId (@Path("id") int id);
    @POST("account/register")
    Call<BaseResponse<Account>> register(
                @Query("name") String name,
                @Query("email") String email,
                @Query("password") String password);
    @POST("account/login")
    Call<BaseResponse<Account>> login(
            @Query("email") String email,
            @Query("password") String password);

    @POST("account/{id}/topUp")
    Call<BaseResponse<Account>> topUp (
        @Path("id") int id,
        @Query("amount") double amount
    );

    @POST ("account/{id}/registerRenter")
    Call<BaseResponse<Renter>> registerRenter (
            @Path("id") int id,
            @Query("companyName") String companyName,
            @Query("address") String address,
            @Query("phoneNumber") String phoneNumber
    );

    @GET("bus/getMyBus")
    Call<BaseResponse<List<Bus>>> getMyBus(@Query("accountId") int accountId);

    @POST("bus/create")
    Call<BaseResponse<Bus>> create(
            @Query("accountId") int accountId,
            @Query("name") String name,
            @Query("facilities") List<Facility> facilities,
            @Query("price") int price,
            @Query("capacity") int capacity,
            @Query("busType") BusType busType,
            @Query("stationDepartureId") int stationDepartureId,
            @Query("stationArrivalId") int stationArrivalId);

    @GET("station/getAll")
    Call<BaseResponse<List<Station>>> getAllStation();

    @POST("bus/addSchedule")
    Call<BaseResponse<Bus>> addSchedule(
            @Query("busId") int busId,
            @Query("time") String time
    );

    @GET("bus/getAll")
    Call<BaseResponse<List<Bus>>> getAllBus();

    @POST("payment/makeBooking")
    Call<BaseResponse<Payment>> makeBooking (
            @Query("buyerId") int buyerId,
            @Query("renterId") int renterId,
            @Query("busId") int busId,
            @Query("busSeats") List<String> busSeats,
            @Query("departureDate") String departureDate
    );

    @GET("payment/getBuyerPayment")
    Call<BaseResponse<List<Payment>>> getBuyerPayment(@Query("buyerId") int buyerId);

    @GET("payment/getRenterPayment")
    Call<BaseResponse<List<Payment>>> getRenterPayment(@Query("renterId") int renterId, @Query("departureDate") Timestamp departureDate);

    @POST("payment/{id}/cancel")
    Call<BaseResponse<Payment>> cancel (
            @Path("id") int id
    );

    @POST("payment/{id}/accept")
    Call<BaseResponse<Payment>> accept (
            @Path("id") int id
    );

}
