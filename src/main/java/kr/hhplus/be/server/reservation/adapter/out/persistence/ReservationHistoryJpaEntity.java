package kr.hhplus.be.server.reservation.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.reservation.domain.constant.HistoryStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservation_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationHistoryJpaEntity {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationsJpaEntity reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private ConcertSeats concertSeats;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private HistoryStatus status;

    @Builder
    public ReservationHistoryJpaEntity(ConcertSeats concertSeats,
                                       ReservationsJpaEntity reservations,
                                       HistoryStatus status) {
        this.concertSeats = concertSeats;
        this.reservations = reservations;
        this.status = status;
    }
}
