package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.ConcertSeats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertSeatsRepository extends JpaRepository<ConcertSeats, Long> {
}
