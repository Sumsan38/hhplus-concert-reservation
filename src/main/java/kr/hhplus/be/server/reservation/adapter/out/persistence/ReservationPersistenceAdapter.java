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

import java.util.Optional;

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
        Users users = getUsers(userId);

        ConcertSeats concertSeats = getConcertSeat(concertSeatId);
        isReservationSeatable(concertSeats);

        ConcertDates concertDates = concertSeats.getConcertDates();
        Concerts concerts = concertDates.getConcerts();

        ReservationsJpaEntity entity = toEntity(users, concerts, concertDates, concertSeats);
        ReservationsJpaEntity saved = reservationRepository.save(entity);

        concertSeats.held();

        Reservation reservation = toDomain(users, concerts, concertDates, concertSeats, ReservationStatus.PENDING);
        reservation.assignId(saved.getId());

        return reservation;
    }

    @Override
    @Transactional
    public PaymentHistory payment(long reservationId) {
        ReservationsJpaEntity reservationsJpaEntity = getReservationsJpaEntity(reservationId);

        ReservationStatus status = reservationsJpaEntity.getStatus();
        if(status != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("대기 상태가 아닙니다. 관리자에게 문의하세요.");
        }

        Users users = reservationsJpaEntity.getUsers();
        ConcertSeats concertSeats = reservationsJpaEntity.getConcertSeats();

        ConcertDates concertDates = concertSeats.getConcertDates();
        Concerts concerts = concertDates.getConcerts();
        Long price = concertSeats.getPrice();

        usersService.useAndSaveHistory(users.getId(), price);

        concertSeats.reservation();
        reservationsJpaEntity.payment();

        Reservation reservation = toDomain(users, concerts, concertDates, concertSeats, ReservationStatus.PAID);
        reservation.assignId(reservationsJpaEntity.getId());

        PaymentHistoryJpaEntity paymentHistoryJpa = PaymentHistoryJpaEntity.builder()
                .reservations(reservationsJpaEntity)
                .paidAmount(price)
                .build();
        PaymentHistoryJpaEntity savedPaymentHistory = springDataPaymentHistoryRepository.save(paymentHistoryJpa);

        return PaymentHistory.create(reservation, price, savedPaymentHistory.getPaidAt());
    }

    @Override
    @Transactional
    public void saveHistory(Reservation reservation, HistoryStatus status) {
        Long id = reservation.getId();
        ReservationsJpaEntity reservationsJpaEntity = getReservationsJpaEntity(id);

        ConcertSeats concertSeats = getConcertSeat(reservation.getConcertSeatId());

        ReservationHistoryJpaEntity historyJpaEntity = toEntity(reservationsJpaEntity, concertSeats, status);
        reservationHistoryRepository.save(historyJpaEntity);
    }

    private ReservationsJpaEntity getReservationsJpaEntity(long reservationId) {
        Optional<ReservationsJpaEntity> jpaEntityOptional = reservationRepository.findById(reservationId);
        if (jpaEntityOptional.isEmpty()) {
            throw new IllegalArgumentException("예약 정보가 없습니다. 관리자에게 문의하세요.");
        }
        return jpaEntityOptional.get();
    }

    private Users getUsers(long userId) {
        Optional<Users> optionalUsers = usersRepository.findById(userId);
        if (optionalUsers.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        return optionalUsers.get();
    }

    private ConcertSeats getConcertSeat(long concertSeatId) {
        Optional<ConcertSeats> seatsOptional = concertSeatsRepository.findById(concertSeatId);
        if (seatsOptional.isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 좌석입니다. 관리자에게 문의하세요.");
        }
        return seatsOptional.get();
    }

    private void isReservationSeatable(ConcertSeats concertSeats) {
        if (concertSeats.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalArgumentException("예약 가능한 좌석이 아닙니다. 좌석 번호: " + concertSeats.getSeatNumber());
        }
    }

    private ReservationsJpaEntity toEntity(Users users,
                                           Concerts concerts,
                                           ConcertDates concertDates,
                                           ConcertSeats concertSeats) {
        return ReservationsJpaEntity.builder()
                .users(users)
                .concertSnapshotTitle(concerts.getTitle())
                .concertSnapshotDate(concertDates.getConcertDate())
                .concertSnapshotTime(concertDates.getConcertTime())
                .concertSeats(concertSeats)
                .build();
    }

    private Reservation toDomain(Users users,
                                 Concerts concerts,
                                 ConcertDates concertDates,
                                 ConcertSeats concertSeats,
                                 ReservationStatus status) {
        return Reservation.create(users.getId(),
                concerts.getTitle(),
                concertDates.getConcertDate(), concertDates.getConcertTime(),
                concertSeats.getPrice(), concertSeats.getId(), concertSeats.getSeatNumber(),
                status);
    }

    private ReservationHistoryJpaEntity toEntity(ReservationsJpaEntity reservationsJpaEntity,
                                                 ConcertSeats concertSeats,
                                                 HistoryStatus status) {
        return ReservationHistoryJpaEntity.builder()
                .concertSeats(concertSeats)
                .reservations(reservationsJpaEntity)
                .status(status)
                .build();
    }
}
