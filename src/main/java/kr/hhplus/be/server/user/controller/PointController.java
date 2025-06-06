package kr.hhplus.be.server.user.controller;

import kr.hhplus.be.server.user.dto.resp.UserPoint;
import kr.hhplus.be.server.user.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/point")
public class PointController {

    private final UsersService usersService;

    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(@PathVariable long id, @RequestBody long amount) {

        return ResponseEntity.ok(usersService.chargeAndSaveHistory(id, amount));
    }

}
