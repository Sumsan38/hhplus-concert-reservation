package kr.hhplus.be.server.reservation.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistoryJpaEntity {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationsJpaEntity reservations;

    private long paidAmount;

    private LocalDateTime paidAt;

    @Builder
    public PaymentHistoryJpaEntity(ReservationsJpaEntity reservations, long paidAmount) {
        this.reservations = reservations;
        this.paidAmount = paidAmount;
        this.paidAt = LocalDateTime.now();
    }
}
