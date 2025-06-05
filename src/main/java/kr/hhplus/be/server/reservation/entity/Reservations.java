package kr.hhplus.be.server.reservation.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import kr.hhplus.be.server.reservation.constant.ReservationStatus;
import kr.hhplus.be.server.user.entity.Users;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reservations")
public class Reservations extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    private String concertSnapshotTitle;
    private LocalDate concertSnapshotDate;
    private LocalTime concertSnapshotTime;
    private long concertSnapshotPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_seat_id", nullable = false)
    private ConcertSeats concertSeats;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservations", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationHistory> reservationHistories = new ArrayList<>();

}
