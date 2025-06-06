package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.ConcertDates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertDatesRepository extends JpaRepository<ConcertDates, Long> {
}
