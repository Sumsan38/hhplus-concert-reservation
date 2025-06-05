package kr.hhplus.be.server.user.dto.resp;

public record UserPoint(Long id, String userName, Long point) {

    public static UserPoint of(Long id, String userName, Long point) {
        return new UserPoint(id, userName, point);
    }

}
