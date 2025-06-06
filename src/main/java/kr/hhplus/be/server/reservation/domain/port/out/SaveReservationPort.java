package kr.hhplus.be.server.reservation.domain.port.out;

import kr.hhplus.be.server.reservation.domain.constant.HistoryStatus;
import kr.hhplus.be.server.reservation.domain.model.PaymentHistory;
import kr.hhplus.be.server.reservation.domain.model.Reservation;

// 도메인이 외부 시스템을 호출해야 할 때 의존하는 추상 계약
// 실제 구현은 RDB(JPA), NoSQL, 메시지 큐 등 어댑터 층이 담당
public interface SaveReservationPort {

    Reservation save(long userId, long concertSeatId);

    PaymentHistory payment(long reservationId);

    void saveHistory(Reservation reservation, HistoryStatus status);

}
