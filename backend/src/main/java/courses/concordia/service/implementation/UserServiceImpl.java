package courses.concordia.service.implementation;

import courses.concordia.model.User;
import courses.concordia.repository.UserRepository;
import courses.concordia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public User getCurrentUser() {
        return null;
    }
}
