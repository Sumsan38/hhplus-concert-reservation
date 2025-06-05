package kr.hhplus.be.server.concert.controller;

import kr.hhplus.be.server.concert.dto.resp.Concert;
import kr.hhplus.be.server.concert.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/concert")
public class ConcertController {

    private final ConcertService concertService;

    @GetMapping
    public ResponseEntity<List<Concert>> concerts(){

        return ResponseEntity.ok(concertService.getConcerts());
    }
}
