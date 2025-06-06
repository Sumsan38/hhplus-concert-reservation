package kr.hhplus.be.server.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "concert_dates")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertDates extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    private LocalDate concertDate;

    private LocalTime concertTime;

    private boolean isSoldOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concerts concerts;

    @OneToMany(mappedBy = "concertDates", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConcertSeats> concertSeats = new ArrayList<>();
}
