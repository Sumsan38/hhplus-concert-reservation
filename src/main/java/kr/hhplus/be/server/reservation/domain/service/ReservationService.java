package kr.hhplus.be.server.reservation.domain.service;

import kr.hhplus.be.server.reservation.domain.constant.HistoryStatus;
import kr.hhplus.be.server.reservation.domain.model.PaymentHistory;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.port.in.PlaceReservationUseCase;
import kr.hhplus.be.server.reservation.domain.port.out.SaveReservationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService implements PlaceReservationUseCase {

    private final SaveReservationPort saveReservationPort;

    public ReservationService(SaveReservationPort saveReservationPort) {
        this.saveReservationPort = saveReservationPort;
    }

    @Override
    @Transactional
    public Reservation placeReservation(long userId,
                                        long concertSeatId) {

        Reservation reservation = saveReservationPort.save(userId, concertSeatId);
        saveReservationPort.saveHistory(reservation, HistoryStatus.HELD);

        return reservation;
    }

    @Override
    public PaymentHistory placePayment(long reservationId) {
        PaymentHistory payment = saveReservationPort.payment(reservationId);
        saveReservationPort.saveHistory(payment.getReservation(), HistoryStatus.RESERVED);

        return payment;
    }
}
