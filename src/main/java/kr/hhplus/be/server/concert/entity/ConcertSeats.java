package kr.hhplus.be.server.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.constant.SeatStatus;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import lombok.*;

@Entity
@Getter
@Table(name = "concert_seats")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSeats extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    private int seatNumber;
    private Long price;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_date_id", nullable = false)
    private ConcertDates concertDates;
}