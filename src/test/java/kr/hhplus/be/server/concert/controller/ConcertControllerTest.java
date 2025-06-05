package kr.hhplus.be.server.concert.controller;

import kr.hhplus.be.server.concert.dto.resp.Concert;
import kr.hhplus.be.server.concert.entity.ConcertDates;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.concert.entity.Concerts;
import kr.hhplus.be.server.concert.service.ConcertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConcertController.class)
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConcertService concertService;

    @Test
    @DisplayName("콘서트 조회 성공")
    void shouldReturnConcertList() throws Exception {
        // given
        Concerts concerts = getConcertTestDate();
        Concert concert = Concert.of(concerts);
        when(concertService.getConcerts()).thenReturn(List.of(concert));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/concert"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("콘서트"))
                .andExpect(jsonPath("$[0].description").value("콘서트 설명"))
                .andExpect(jsonPath("$[0].concertDates[0].seats[0].seatNumber").value(1))
        ;
    }

    private Concerts getConcertTestDate() {
        ConcertSeats seats1 = ConcertSeats.builder().id(1L).seatNumber(1).price(10_000L).build();
        ConcertSeats seats2 = ConcertSeats.builder().id(2L).seatNumber(2).price(10_000L).build();

        ConcertDates dates = ConcertDates.builder().id(1L)
                .concertDate(LocalDate.of(2025, 7, 1))
                .concertTime(LocalTime.of(10, 10))
                .concertSeats(List.of(seats1, seats2)).build();
        return Concerts.builder().id(1L).title("콘서트").description("콘서트 설명").concertDates(List.of(dates)).build();
    }
}