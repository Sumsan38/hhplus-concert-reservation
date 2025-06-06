package kr.hhplus.be.server.reservation.domain.model;

import kr.hhplus.be.server.reservation.domain.constant.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {

    private Long id;
    private long userId;
    private String concertSnapshotTitle;
    private LocalDate concertSnapshotDate;
    private LocalTime concertSnapshotTime;
    private long concertSnapshotPrice;
    private long concertSeatId;
    private long concertSeatNumber;
    private ReservationStatus status;

    private Reservation(long userId,
                        String concertSnapshotTitle,
                        LocalDate concertSnapshotDate,
                        LocalTime concertSnapshotTime,
                        long concertSnapshotPrice,
                        long concertSeatId,
                        long concertSeatNumber,
                        ReservationStatus status) {
        this.userId = userId;
        this.concertSnapshotTitle = concertSnapshotTitle;
        this.concertSnapshotDate = concertSnapshotDate;
        this.concertSnapshotTime = concertSnapshotTime;
        this.concertSnapshotPrice = concertSnapshotPrice;
        this.concertSeatId = concertSeatId;
        this.concertSeatNumber = concertSeatNumber;
        this.status = status;
    }

    public static Reservation create(long userId,
                                     String concertSnapshotTitle,
                                     LocalDate concertSnapshotDate,
                                     LocalTime concertSnapshotTime,
                                     long concertSnapshotPrice,
                                     long concertSeatId,
                                     long concertSeatNumber,
                                     ReservationStatus status) {
        return new Reservation(userId,
                concertSnapshotTitle, concertSnapshotDate, concertSnapshotTime, concertSnapshotPrice,
                concertSeatId, concertSeatNumber, status);
    }

    // 비지니스 로직


    // getter
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getConcertSnapshotTitle() {
        return concertSnapshotTitle;
    }

    public LocalDate getConcertSnapshotDate() {
        return concertSnapshotDate;
    }

    public LocalTime getConcertSnapshotTime() {
        return concertSnapshotTime;
    }

    public long getConcertSnapshotPrice() {
        return concertSnapshotPrice;
    }

    public long getConcertSeatId() {
        return concertSeatId;
    }

    public long getConcertSeatNumber() {
        return concertSeatNumber;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void assignId(Long id) {
        this.id = id;
    }
}
