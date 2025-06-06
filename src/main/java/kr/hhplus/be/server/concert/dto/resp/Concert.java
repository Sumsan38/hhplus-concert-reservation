package kr.hhplus.be.server.concert.dto.resp;

import kr.hhplus.be.server.concert.entity.Concerts;

import java.util.List;

public record Concert(
        Long id,
        String title,
        String description,
        List<ConcertDate> concertDates
) {
    public static Concert of(Concerts concerts) {
        return new Concert(concerts.getId(),
                concerts.getTitle(),
                concerts.getDescription(),
                concerts.getConcertDates().stream().map(ConcertDate::of).toList()
        );
    }


}
