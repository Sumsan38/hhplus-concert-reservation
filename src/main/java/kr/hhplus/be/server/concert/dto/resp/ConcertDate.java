package kr.hhplus.be.server.concert.dto.resp;

import kr.hhplus.be.server.concert.entity.ConcertDates;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ConcertDate(Long id,
                          LocalDate concertDate,
                          LocalTime concertTime,
                          boolean isSoldOut,
                          List<ConcertSeat> seats) {

    public static ConcertDate of(ConcertDates concertDates) {
        return new ConcertDate(concertDates.getId(),
                concertDates.getConcertDate(),
                concertDates.getConcertTime(),
                concertDates.isSoldOut(),
                concertDates.getConcertSeats().stream().map(ConcertSeat::of).toList()
        );
    }
}