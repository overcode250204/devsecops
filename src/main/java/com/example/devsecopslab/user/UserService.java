package com.example.devsecopslab.user;

import com.example.devsecopslab.common.CurrentUser;
import com.example.devsecopslab.config.LabProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("email", "fullName", "createdAt");

    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;
    private final LabProperties labProperties;

    public List<?> searchByEmail(String email) {
        if (labProperties.isVulnerableMode()) {
            return userSearchRepository.searchByEmailVulnerable(email)
                    .stream()
                    .map(InsecureUserResponse::from)
                    .toList();
        }

        return userSearchRepository.searchByEmailFixed(email)
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public List<UserResponse> getUsers(String sortBy) {
        if (labProperties.isVulnerableMode()) {
            return userSearchRepository.findAllWithSortVulnerable(sortBy)
                    .stream()
                    .map(UserResponse::from)
                    .toList();
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sort field");
        }

        return userRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy))
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public Object getUser(UUID targetUserId, CurrentUser currentUser) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (labProperties.isVulnerableMode()) {
            return InsecureUserResponse.from(user);
        }

        boolean isOwner = targetUserId.equals(currentUser.id());
        boolean isAdmin = currentUser.isAdmin();

        if (!isOwner && !isAdmin) {
            log.warn("Unauthorized user access attempt requesterId={} targetUserId={}",
                    currentUser.id(), targetUserId);
            throw new AccessDeniedException("Forbidden");
        }

        return UserResponse.from(user);
    }

    public Object updateUserVulnerable(UUID targetUserId, InsecureUpdateUserRequest request) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.verified() != null) {
            user.setVerified(request.verified());
        }
        if (request.balance() != null) {
            user.setBalance(request.balance());
        }

        return InsecureUserResponse.from(userRepository.save(user));
    }

    public UserResponse updateProfile(UUID targetUserId, UpdateProfileRequest request, CurrentUser currentUser) {
        boolean isOwner = targetUserId.equals(currentUser.id());

        if (!isOwner) {
            log.warn("Unauthorized profile update attempt requesterId={} targetUserId={}",
                    currentUser.id(), targetUserId);
            throw new AccessDeniedException("Forbidden");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        user.setFullName(request.fullName());
        user.setEmail(request.email());

        return UserResponse.from(userRepository.save(user));
    }
}
