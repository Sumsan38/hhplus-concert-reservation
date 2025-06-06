package kr.hhplus.be.server.user.service;

import kr.hhplus.be.server.user.constant.PointType;
import kr.hhplus.be.server.user.dto.resp.UserPoint;
import kr.hhplus.be.server.user.entity.PointHistory;
import kr.hhplus.be.server.user.entity.Users;
import kr.hhplus.be.server.user.repository.PointHistoryRepository;
import kr.hhplus.be.server.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersService {

    private final UsersRepository usersRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public UserPoint chargeAndSaveHistory(long id, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 포인트가 유효하지 않습니다: " + amount);
        }

        Users users = getUsers(id);

        savePointHistory(users, PointType.CHARGE, amount);

        users.updatePoint(PointType.CHARGE, amount);

        return UserPoint.of(users.getId(), users.getUserName(), users.getPoint());
    }

    @Transactional
    public void useAndSaveHistory(long id, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 포인트가 유효하지 않습니다: " + amount);
        }

        Users users = getUsers(id);
        if(users.getPoint() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다. 유저의 현재 포인트: " + users.getPoint());
        }

        savePointHistory(users, PointType.USE, amount);

        users.updatePoint(PointType.USE, amount);
    }

    private Users getUsers(long id) {
        Optional<Users> optionalUsers = usersRepository.findById(id);
        if (optionalUsers.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        return optionalUsers.get();
    }

    private void savePointHistory(Users users, PointType type, long amount) {
        long currentPoint = users.getPoint();
        PointHistory pointHistory = new PointHistory(users, type, amount, currentPoint);
        pointHistoryRepository.save(pointHistory);
    }
}
