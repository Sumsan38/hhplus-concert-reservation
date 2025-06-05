package kr.hhplus.be.server.reservation.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reservation_history")
public class ReservationHistory {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservations reservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private ConcertSeats concertSeats;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;
}
