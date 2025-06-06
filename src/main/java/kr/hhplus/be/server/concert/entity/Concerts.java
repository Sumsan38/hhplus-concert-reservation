package kr.hhplus.be.server.concert.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.global.entity.BaseTimeEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "concerts")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concerts extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String title;

    private String description;

    @OneToMany(mappedBy = "concerts", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConcertDates> concertDates = new ArrayList<>();
}
