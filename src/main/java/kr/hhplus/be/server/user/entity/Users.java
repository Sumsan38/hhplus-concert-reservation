package kr.hhplus.be.server.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class Users extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String userName;

    private long point;
}
