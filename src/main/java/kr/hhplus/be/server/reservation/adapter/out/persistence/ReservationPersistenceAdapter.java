package kr.hhplus.be.server.reservation.adapter.out.persistence;

import kr.hhplus.be.server.concert.constant.SeatStatus;
import kr.hhplus.be.server.concert.entity.ConcertDates;
import kr.hhplus.be.server.concert.entity.ConcertSeats;
import kr.hhplus.be.server.concert.entity.Concerts;
import kr.hhplus.be.server.concert.repository.ConcertSeatsRepository;
import kr.hhplus.be.server.reservation.domain.constant.HistoryStatus;
import kr.hhplus.be.server.reservation.domain.constant.ReservationStatus;
import kr.hhplus.be.server.reservation.domain.model.PaymentHistory;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.port.out.SaveReservationPort;
import kr.hhplus.be.server.user.entity.Users;
import kr.hhplus.be.server.user.repository.UsersRepository;
import kr.hhplus.be.server.user.service.UsersService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReservationPersistenceAdapter implements SaveReservationPort {

    private final UsersRepository usersRepository;
    private final ConcertSeatsRepository concertSeatsRepository;
    private final SpringDataReservationRepository reservationRepository;
    private final SpringDataReservationHistoryRepository reservationHistoryRepository;
    private final SpringDataPaymentHistoryRepository springDataPaymentHistoryRepository;

    private final UsersService usersService;

    public ReservationPersistenceAdapter(UsersRepository usersRepository,
                                         ConcertSeatsRepository concertSeatsRepository,
                                         SpringDataReservationRepository reservationRepository,
                                         SpringDataReservationHistoryRepository reservationHistoryRepository,
                                         SpringDataPaymentHistoryRepository springDataPaymentHistoryRepository,
                                         UsersService usersService) {
        this.usersRepository = usersRepository;
        this.concertSeatsRepository = concertSeatsRepository;
        this.reservationRepository = reservationRepository;
        this.reservationHistoryRepository = reservationHistoryRepository;
        this.springDataPaymentHistoryRepository = springDataPaymentHistoryRepository;
        this.usersService = usersService;
    }

    @Override
    @Transactional
    public Reservation save(long userId, long concertSeatId) {
        Users users = getUserById(userId);
        ConcertSeats seat = getSeatById(concertSeatId);
        validateSeatAvailable(seat);

        ReservationsJpaEntity entity = buildReservationEntity(users, seat);
        ReservationsJpaEntity saved = reservationRepository.save(entity);

        seat.held();

        return buildReservationDomain(users, seat, ReservationStatus.PENDING, saved.getId());
    }

    @Override
    @Transactional
    public PaymentHistory payment(long reservationId) {
        ReservationsJpaEntity reservationEntity = getReservationById(reservationId);

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("대기 상태가 아닙니다. 관리자에게 문의하세요.");
        }

        Users users = reservationEntity.getUsers();
        ConcertSeats seat = reservationEntity.getConcertSeats();
        Long price = seat.getPrice();

        // 포인트 차감 및 히스토리 저장
        usersService.useAndSaveHistory(users.getId(), price);

        // 좌석 및 예약 상태 변경
        seat.reservation();
        reservationEntity.payment();

        // 도메인 객체 생성
        Reservation reservation = buildReservationDomain(users, seat, ReservationStatus.PAID, reservationEntity.getId());

        // 결제 히스토리 저장
        PaymentHistoryJpaEntity savedPaymentHistory = springDataPaymentHistoryRepository.save(
                PaymentHistoryJpaEntity.builder()
                        .reservations(reservationEntity)
                        .paidAmount(price)
                        .build());

        return PaymentHistory.create(reservation, price, savedPaymentHistory.getPaidAt());
    }

    @Override
    @Transactional
    public void saveHistory(Reservation reservation, HistoryStatus status) {
        ReservationsJpaEntity reservationsJpaEntity = getReservationById(reservation.getId());
        ConcertSeats seat = getSeatById(reservation.getConcertSeatId());

        ReservationHistoryJpaEntity historyJpaEntity =
                buildReservationHistoryEntity(reservationsJpaEntity, seat, status);
        reservationHistoryRepository.save(historyJpaEntity);
    }


    private ReservationsJpaEntity getReservationById(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보가 없습니다. 관리자에게 문의하세요."));
    }

    private Users getUserById(long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    private ConcertSeats getSeatById(long id) {
        return concertSeatsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 좌석입니다. 관리자에게 문의하세요."));
    }

    private void validateSeatAvailable(ConcertSeats seat) {
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalArgumentException("예약 가능한 좌석이 아닙니다. 좌석 번호: " + seat.getSeatNumber());
        }
    }

    private ReservationHistoryJpaEntity buildReservationHistoryEntity(ReservationsJpaEntity reservationsJpa,
                                                                      ConcertSeats concertSeats,
                                                                      HistoryStatus status) {
        return ReservationHistoryJpaEntity.builder()
                .concertSeats(concertSeats)
                .reservations(reservationsJpa)
                .status(status)
                .build();
    }

    private ReservationsJpaEntity buildReservationEntity(Users user, ConcertSeats seat) {
        ConcertDates date = seat.getConcertDates();
        Concerts concert = date.getConcerts();

        return ReservationsJpaEntity.builder()
                .users(user)
                .concertSnapshotTitle(concert.getTitle())
                .concertSnapshotDate(date.getConcertDate())
                .concertSnapshotTime(date.getConcertTime())
                .concertSeats(seat)
                .build();
    }

    private Reservation buildReservationDomain(Users user, ConcertSeats seat, ReservationStatus status, Long id) {
        ConcertDates date = seat.getConcertDates();
        Concerts concert = date.getConcerts();

        Reservation reservation = Reservation.create(
                user.getId(),
                concert.getTitle(),
                date.getConcertDate(),
                date.getConcertTime(),
                seat.getPrice(),
                seat.getId(),
                seat.getSeatNumber(),
                status
        );

        reservation.assignId(id);
        return reservation;
    }
}
