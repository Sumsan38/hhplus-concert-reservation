package kr.hhplus.be.server.reservation.adapter.in.web;

import kr.hhplus.be.server.reservation.adapter.in.web.dto.req.CreateReservationRequest;
import kr.hhplus.be.server.reservation.adapter.in.web.dto.req.PaymentRequest;
import kr.hhplus.be.server.reservation.domain.model.PaymentHistory;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.port.in.PlaceReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {

    private final PlaceReservationUseCase placeReservationUseCase;

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody CreateReservationRequest request){

        Reservation reservation =
                placeReservationUseCase.placeReservation(request.userId(), request.concertSeatId());

        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentHistory> payment(@RequestBody PaymentRequest request) {

        PaymentHistory paymentHistory = placeReservationUseCase.placePayment(request.reservationId());

        return ResponseEntity.ok(paymentHistory);
    }
}
