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
import kr.hhplus.be.server.user.entity.Users;
import kr.hhplus.be.server.user.repository.UsersRepository;
import kr.hhplus.be.server.user.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationPersistenceAdapterTest {

    @InjectMocks
    private ReservationPersistenceAdapter adapter;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private ConcertSeatsRepository concertSeatsRepository;

    @Mock
    private SpringDataReservationRepository reservationRepository;

    @Mock
    private SpringDataReservationHistoryRepository reservationHistoryRepository;

    @Mock
    private SpringDataPaymentHistoryRepository springDataPaymentHistoryRepository;

    @Mock
    private UsersService usersService;

    @Test
    @DisplayName("예약 저장 성공 - 좌석이 AVAILABLE 상태인 경우")
    public void saveReservation_success() {
        // given
        long userId = 1L;
        long concertSeatId = 10L;

        Users mockUser = mock(Users.class);
        Concerts concert = Concerts.builder().title("테스트 공연").build();
        ConcertDates concertDate = ConcertDates.builder().concertDate(LocalDate.of(2025, 7, 1)).concertTime(LocalTime.of(10, 10)).concerts(concert).build();
        ConcertSeats seat = ConcertSeats.builder().id(concertSeatId).seatNumber(1).status(SeatStatus.AVAILABLE).price(10000L).concertDates(concertDate).build();
        ReservationsJpaEntity savedEntity = ReservationsJpaEntity.builder().id(100L).build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(concertSeatsRepository.findById(concertSeatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any())).thenReturn(savedEntity);

        // when
        Reservation result = adapter.save(userId, concertSeatId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getConcertSeatId()).isEqualTo(concertSeatId);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    @DisplayName("예약 저장 실패 - 존재 하지 않는 유저")
    public void saveReservation_fail_noUser() {
        // given
        long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adapter.save(userId, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 저장 실패 - 존재 하지 않는 좌석")
    public void saveReservation_fail_noSeat() {
        // given
        long userId = 1L;
        when(usersRepository.findById(userId)).thenReturn(Optional.of(mock(Users.class)));
        when(concertSeatsRepository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adapter.save(userId, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 저장 실패 - 좌석이 AVAILABLE 상태가 아님")
    public void saveReservation_fail_seatUnavailable() {
        // given
        long userId = 1L;
        ConcertSeats seat = ConcertSeats.builder().id(10L).seatNumber(1).status(SeatStatus.RESERVED).build();

        when(usersRepository.findById(userId)).thenReturn(Optional.of(mock(Users.class)));
        when(concertSeatsRepository.findById(10L)).thenReturn(Optional.of(seat));

        // when & then
        assertThatThrownBy(() -> adapter.save(userId, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("결제 성공 - 포인트 차감, 좌석 상태 변경, 예약 상태 변경, 결제 히스토리 저장")
    void payment_success() {
        // given
        long reservationId = 1L;
        long seatId = 10L;
        long price = 10000L;

        Users user = mock(Users.class);

        Concerts concert = Concerts.builder().title("테스트 공연").build();
        ConcertDates concertDates = ConcertDates.builder()
                .concertDate(LocalDate.now())
                .concertTime(LocalTime.NOON)
                .concerts(concert).build();

        ConcertSeats seat = ConcertSeats.builder()
                .id(seatId)
                .status(SeatStatus.HELD)
                .price(price)
                .concertDates(concertDates)
                .build();

        ReservationsJpaEntity reservationEntity = ReservationsJpaEntity.builder()
                .id(reservationId)
                .users(user)
                .concertSeats(seat)
                .status(ReservationStatus.PENDING)
                .build();

        PaymentHistoryJpaEntity savedHistory = PaymentHistoryJpaEntity.builder()
                .paidAmount(price)
                .reservations(reservationEntity)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(springDataPaymentHistoryRepository.save(any())).thenReturn(savedHistory);

        // when
        PaymentHistory result = adapter.payment(reservationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getPaidAmount()).isEqualTo(price);
        verify(usersService).useAndSaveHistory(user.getId(), price);
        verify(springDataPaymentHistoryRepository).save(any(PaymentHistoryJpaEntity.class));
    }

    @Test
    @DisplayName("결제 실패 - 상태가 PENDING이 아닌 경우 예외 발생")
    void payment_fail_notPending() {
        // given
        long reservationId = 2L;
        ReservationsJpaEntity reservationEntity = ReservationsJpaEntity.builder()
                .id(reservationId)
                .status(ReservationStatus.PAID) // already paid
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));

        // when & then
        assertThatThrownBy(() -> adapter.payment(reservationId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 이력 저장 성공")
    void saveHistory_success() {
        // given
        long reservationId = 1L;
        long concertSeatId = 10L;

        ReservationsJpaEntity reservationEntity = ReservationsJpaEntity.builder()
                .id(reservationId)
                .build();

        ConcertSeats seat = ConcertSeats.builder()
                .id(concertSeatId)
                .status(SeatStatus.RESERVED)
                .build();

        Reservation reservation = Reservation.create(
                1L, "콘서트", null, null, 10000L,
                concertSeatId, 1, null
        );
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(concertSeatsRepository.findById(concertSeatId)).thenReturn(Optional.of(seat));

        // when
        adapter.saveHistory(reservation, HistoryStatus.RESERVED);

        // then
        verify(reservationHistoryRepository).save(any(ReservationHistoryJpaEntity.class));
    }

    @Test
    @DisplayName("예약 이력 저장 실패 - 예약 ID가 존재 하지 않음")
    void saveHistory_fail_reservationNotFound() {
        // given
        Reservation reservation = Reservation.create(1L, "콘서트", null, null, 10000L,
                10L, 1, null);
        reservation.assignId(999L);

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adapter.saveHistory(reservation, HistoryStatus.CANCELED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 이력 저장 실패 - 좌석 ID가 존재 하지 않음")
    void saveHistory_fail_seatNotFound() {
        // given
        long reservationId = 1L;
        long seatId = 99L;

        ReservationsJpaEntity reservationEntity = ReservationsJpaEntity.builder()
                .id(reservationId)
                .build();

        Reservation reservation =
                Reservation.create(1L, "콘서트", null, null, 10000L, seatId, 1, null);
        reservation.assignId(reservationId);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservationEntity));
        when(concertSeatsRepository.findById(seatId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> adapter.saveHistory(reservation, HistoryStatus.CANCELED))
                .isInstanceOf(IllegalArgumentException.class);
    }
}