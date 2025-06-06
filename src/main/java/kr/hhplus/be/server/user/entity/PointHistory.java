package kr.hhplus.be.server.user.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.user.constant.PointType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "point_history")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    @Enumerated(EnumType.STRING)
    private PointType type;

    private long amount;

    private long currentAmount;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdAt;

    public PointHistory(Users users, PointType type, long amount, long currentAmount) {
        this.users = users;
        this.type = type;
        this.amount = amount;
        this.currentAmount = currentAmount;
    }
}
