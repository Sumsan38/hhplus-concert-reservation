package kr.hhplus.be.server.concert.dto.resp;

import kr.hhplus.be.server.concert.constant.SeatStatus;
import kr.hhplus.be.server.concert.entity.ConcertSeats;


public record ConcertSeat(Long id,
                          int seatNumber,
                          Long price,
                          SeatStatus status) {
    public static ConcertSeat of(ConcertSeats concertSeats) {
        return new ConcertSeat(
                concertSeats.getId(),
                concertSeats.getSeatNumber(),
                concertSeats.getPrice(),
                concertSeats.getStatus());
    }
}
