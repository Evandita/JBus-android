package com.evanditaWiratamaPutraJBusER.jbus_android.model;

import java.sql.Timestamp;

public class Invoice extends Serializable
{
    public Timestamp time;
    public int buyerId;
    public int renterId;
    public BusRating rating;
    public PaymentStatus status;

    public enum BusRating
    {
        NONE,
        NEUTRAL,
        GOOD,
        BAD;
    }

    public enum PaymentStatus
    {
        FAILED,
        WAITING,
        SUCCESS;
    }
}
