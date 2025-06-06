package kr.hhplus.be.server.reservation.domain.model;

import java.time.LocalDateTime;

public class PaymentHistory {

    private long id;
    private Reservation reservation;
    private long paidAmount;
    private LocalDateTime paidAt;

    private PaymentHistory(Reservation reservation, long paidAmount, LocalDateTime paidAt) {
        this.reservation = reservation;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
    }

    public static PaymentHistory create(Reservation reservation, long paidAmount, LocalDateTime paidAt) {
        return new PaymentHistory(reservation, paidAmount, paidAt);
    }

    // 비지니스 로직


    // getter
    public long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public long getPaidAmount() {
        return paidAmount;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    void assignId(Long id) {
        this.id = id;
    }
}
