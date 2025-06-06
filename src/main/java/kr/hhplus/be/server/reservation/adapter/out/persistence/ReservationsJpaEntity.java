package kr.hhplus.be.server.reservation.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import kr.hhplus.be.server.reservation.domain.constant.ReservationStatus;
import kr.hhplus.be.server.user.entity.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationsJpaEntity extends BaseTimeEntity {

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
    private final List<ReservationHistoryJpaEntity> reservationHistories = new ArrayList<>();

    @Builder
    public ReservationsJpaEntity(Users users,
                                 String concertSnapshotTitle,
                                 LocalDate concertSnapshotDate, LocalTime concertSnapshotTime,
                                 long concertSnapshotPrice,
                                 ConcertSeats concertSeats) {
        this.users = users;
        this.concertSnapshotTitle = concertSnapshotTitle;
        this.concertSnapshotDate = concertSnapshotDate;
        this.concertSnapshotTime = concertSnapshotTime;
        this.concertSnapshotPrice = concertSnapshotPrice;
        this.concertSeats = concertSeats;
        this.status = ReservationStatus.PENDING;
    }

    public void payment(){
        this.status = ReservationStatus.PAID;
    }
}
