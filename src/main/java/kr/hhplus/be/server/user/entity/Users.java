package kr.hhplus.be.server.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import kr.hhplus.be.server.user.constant.PointType;
import lombok.*;

@Entity
@Getter
@Table(name = "users")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private long point;

    public void updatePoint(PointType pointType, long amount) {
        if(pointType == PointType.CHARGE) {
            this.point += amount;
        }
        else {
            this.point -= amount;
        }
    }
}
