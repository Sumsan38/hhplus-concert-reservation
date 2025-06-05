package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.dto.resp.Concert;
import kr.hhplus.be.server.concert.entity.ConcertDates;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.concert.entity.Concerts;
import kr.hhplus.be.server.concert.repository.ConcertsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ConcertServiceTest {

    private ConcertsRepository concertsRepository;
    private ConcertService concertService;

    @BeforeEach
    void setUp() {
        concertsRepository = mock(ConcertsRepository.class);
        concertService = new ConcertService(concertsRepository);
    }

    @Test
    @DisplayName("콘서트 리스트를 조회합니다.")
    void getConcerts_ReturnList() {
        // given
        Concerts concerts = getConcertTestDate();
        when(concertsRepository.findAll()).thenReturn(List.of(concerts));

        // when
        List<Concert> results = concertService.getConcerts();

        // then
        assertThat(results).isNotEmpty();
        verify(concertsRepository, times(1)).findAll();
    }



    private Concerts getConcertTestDate() {
        ConcertSeats seats1 = ConcertSeats.builder().id(1L).seatNumber(1).price(10000).build();
        ConcertSeats seats2 = ConcertSeats.builder().id(2L).seatNumber(2).price(10000).build();

        ConcertDates dates = ConcertDates.builder().id(1L)
                .concertDate(LocalDate.of(2025, 7, 1))
                .concertTime(LocalTime.of(10, 10))
                .concertSeats(List.of(seats1, seats2)).build();
        return Concerts.builder().id(1L).title("콘서트").description("콘서트 설명").concertDates(List.of(dates)).build();
    }
}