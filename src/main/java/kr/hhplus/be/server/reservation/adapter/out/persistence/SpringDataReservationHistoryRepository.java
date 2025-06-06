package kr.hhplus.be.server.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataReservationHistoryRepository extends JpaRepository<ReservationHistoryJpaEntity, Long> {
}
