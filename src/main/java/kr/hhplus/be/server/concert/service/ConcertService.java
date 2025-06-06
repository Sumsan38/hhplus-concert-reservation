package kr.hhplus.be.server.concert.service;

import kr.hhplus.be.server.concert.dto.resp.Concert;
import kr.hhplus.be.server.concert.entity.Concerts;
import kr.hhplus.be.server.concert.repository.ConcertsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertService {

    private final ConcertsRepository concertsRepository;

    public List<Concert> getConcerts(){
        List<Concerts> concerts = concertsRepository.findAll();

        return concerts.stream().map(Concert::of).toList();
    }


}
