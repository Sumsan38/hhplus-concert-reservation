package kr.hhplus.be.server.user.controller;

import kr.hhplus.be.server.user.dto.resp.UserPoint;
import kr.hhplus.be.server.user.service.UsersService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersService usersService;

    @Test
    @DisplayName("포인트 충전 성공")
    void shouldReturnUserPoint() throws Exception {
        // given
        Long userId = 1L;
        Long amount = 1_000L;
        UserPoint userPoint = UserPoint.of(userId, "테스트 유저", amount);
        when(usersService.chargeAndSaveHistory(userId, amount)).thenReturn(userPoint);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/point/" + userId + "/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(amount.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(amount));
    }
    
    @Test
    @DisplayName("유효 하지 않은 포인트 금액 충전")
    void shouldThrowException_invalidPoint() throws Exception {
        // given
        long userId = 1L;
        long amount = -1_000L;
        when(usersService.chargeAndSaveHistory(userId, amount))
                .thenThrow(new IllegalArgumentException("충전 포인트가 유효하지 않습니다: " + amount));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Long.toString(amount)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("유효 하지 않은 유저의 포인트 충천")
    void shouldThrowException_invalidUsers() throws Exception {
        // given
        long userId = Long.MAX_VALUE;
        long amount = -1_000L;
        when(usersService.chargeAndSaveHistory(userId, amount))
                .thenThrow(new IllegalArgumentException("존재하지 않는 유저입니다."));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/point/" + userId + "/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Long.toString(amount)))
                .andExpect(status().isBadRequest());
    }
}