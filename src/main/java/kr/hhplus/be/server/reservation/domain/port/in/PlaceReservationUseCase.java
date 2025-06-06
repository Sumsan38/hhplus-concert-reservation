package kr.hhplus.be.server.reservation.domain.port.in;

import kr.hhplus.be.server.reservation.domain.model.PaymentHistory;
import kr.hhplus.be.server.reservation.domain.model.Reservation;

// 도메인이 외부로부터 호출받는 작업
public interface PlaceReservationUseCase {

    // 주문 생성
    Reservation placeReservation(long userId, long concertSeatId);

    // 결제
    PaymentHistory placePayment(long reservationId);
}
