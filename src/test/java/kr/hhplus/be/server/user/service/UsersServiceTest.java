package kr.hhplus.be.server.user.service;

import kr.hhplus.be.server.user.dto.resp.UserPoint;
import kr.hhplus.be.server.user.entity.Users;
import kr.hhplus.be.server.user.repository.PointHistoryRepository;
import kr.hhplus.be.server.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UsersServiceTest {

    private UsersService usersService;
    private UsersRepository usersRepository;
    private PointHistoryRepository pointHistoryRepository;

    @BeforeEach
    void setUp() {
        usersRepository = mock(UsersRepository.class);
        pointHistoryRepository = mock(PointHistoryRepository.class);
        usersService = new UsersService(usersRepository, pointHistoryRepository);
    }

    @Test
    @DisplayName("유저 포인트 충전을 성공한다")
    void chargeAndSaveHistory_success() {
        // given
        Users users = Users.builder().id(1L).userName("테스트 유저").point(1_000L).build();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(users));

        // when
        UserPoint charge = usersService.chargeAndSaveHistory(1L, 1_000L);

        // then
        assertThat(charge).isNotNull();
        assertThat(charge.id()).isEqualTo(1L);
        assertThat(charge.point()).isEqualTo(2_000L);
        verify(usersRepository, times(1)).findById(anyLong());
        verify(pointHistoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 유저에 대해 포인트를 충천시 에러가 발생한다")
    void chargeAndSaveHistory_fail_invalidUsers() {
        // when & then
        assertThatThrownBy(() -> usersService.chargeAndSaveHistory(1L, 0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("음수 포인트일 경우 에러가 발생한다")
    void chargeAndSaveHistory_fail() {
        // given
        Users users = Users.builder().id(1L).userName("테스트 유저").point(1_000L).build();
        when(usersRepository.findById(1L)).thenReturn(Optional.of(users));

        // when & then
        assertThatThrownBy(() -> usersService.chargeAndSaveHistory(1L, 0L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> usersService.chargeAndSaveHistory(1L, -1L))
                .isInstanceOf(IllegalArgumentException.class);

    }

}