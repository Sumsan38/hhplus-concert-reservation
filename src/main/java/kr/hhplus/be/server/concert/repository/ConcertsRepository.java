package kr.hhplus.be.server.concert.repository;

import kr.hhplus.be.server.concert.entity.Concerts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertsRepository extends JpaRepository<Concerts, Long> {
}
