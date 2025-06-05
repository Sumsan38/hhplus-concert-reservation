package kr.hhplus.be.server.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservations reservations;

    private long paidAmount;

    private LocalDateTime paidAt;
}
