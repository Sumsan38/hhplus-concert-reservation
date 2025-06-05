package kr.hhplus.be.server.user.controller;

import kr.hhplus.be.server.user.dto.resp.UserPoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/point")
public class PointController {
    // TODO 구현

    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(@PathVariable long id, @RequestBody long amount) {

        return null;
    }

    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(@PathVariable long id, @RequestBody long amount) {

        return null;
    }
}
