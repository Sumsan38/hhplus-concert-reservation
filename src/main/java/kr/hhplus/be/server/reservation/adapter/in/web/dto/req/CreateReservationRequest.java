package kr.hhplus.be.server.reservation.adapter.in.web.dto.req;

public record CreateReservationRequest(
    long userId,
    long concertSeatId
) {
}
