package com.socialnetwork.domain;

import com.socialnetwork.domain.inport.AuthService;
import com.socialnetwork.domain.inport.UserService;
import com.socialnetwork.domain.outport.UserRepository;
import com.socialnetwork.repository.PhotoRepositoryBasic;
import com.socialnetwork.rest.dto.CreateUserRequest;
import com.socialnetwork.rest.dto.UpdateUserPasswordRequest;
import com.socialnetwork.rest.dto.UpdateUserRequest;
import com.socialnetwork.rest.dto.VerifyUserEmailRequest;
import com.socialnetwork.domain.outport.EmailService;
import com.socialnetwork.domain.outport.EmailVerificationService;
import com.socialnetwork.domain.outport.PhotoVerificationService;
import com.socialnetwork.domain.outport.StorageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
class UserServiceImpl implements UserService, AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhotoRepositoryBasic profilePictureRepository;

    private UserMapper mapper;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private StorageService profilePictureStorageService;

    @Autowired
    private PhotoVerificationService photoVerificationService;

//    UserServiceImpl(
//            UserRepository userRepository,
//            UserMapper mapper,
//            EmailVerificationService emailVerificationService,
//            EmailService emailService,
//            StorageService storageService,
//            PhotoVerificationService photoVerificationService) {
//        this.userRepository = userRepository;
//        this.mapper = mapper;
//        this.emailVerificationService = emailVerificationService;
//        this.emailService = emailService;
//        this.profilePictureStorageService = storageService;
//        this.photoVerificationService = photoVerificationService;
//    }

    @Override
    public Set<User> getAll(String q) {
        return userRepository.search(q);
    }

    UserServiceImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public User findOneById(Long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    @Override
    public User authenticate(String email, String password) throws InvalidUserCredentialsException {
        Optional<User> user = userRepository.findFirstByEmailAndPassword(email, password);
        if (!user.isPresent()) {
            throw new InvalidUserCredentialsException();
        }

        return user.get();
    }

    @Override
    public User create(CreateUserRequest createUserRequest) throws MessagingException {
        User user = mapper.map(createUserRequest);
        user = userRepository.save(user);

        String  verificationCode = emailVerificationService.generateVerificationCode(user.getId().toString());
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);

        return user;
    }

    @Override
    public void sendVerificationEmail(long userId) throws UserNotFoundException, MessagingException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        String verificationCode = emailVerificationService.generateVerificationCode(user.get().getId().toString());
        emailService.sendVerificationEmail(user.get().getEmail(), verificationCode);
    }

    @Override
    public boolean verifyEmail(long userId, String verificationCode) throws UserNotFoundException {
        if (!emailVerificationService.verify(Long.toString(userId), verificationCode)) {
            return false;
        }

        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        user.get().verifyEmail();
        userRepository.save(user.get());

        return true;
    }

    @Override
    public User update(long userId, UpdateUserRequest updateUserRequest) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        user.get().updateName(updateUserRequest.getFirstName(), updateUserRequest.getLastName());
        userRepository.save(user.get());

        return user.get();
    }

    @Override
    public boolean updatePassword(long userId, UpdateUserPasswordRequest updateUserPasswordRequest)
            throws UserNotFoundException {
        Optional<User> user = userRepository.findByIdAndPassword(userId, updateUserPasswordRequest.getCurrentPassword());
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        user.get().updatePassword(updateUserPasswordRequest.getNewPassword());
        userRepository.save(user.get());
        return true;
    }

    @Override
    public User updateProfilePicture(long userId, MultipartFile profilePictureFile)
            throws UserNotFoundException, IOException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        String path = profilePictureStorageService.upload(profilePictureFile);
        int faceAnnotationCount = photoVerificationService.countFaces(path);
        boolean profilePictureValidSelfie = false;
        if (faceAnnotationCount == 1) {
            profilePictureValidSelfie = true;
        }

        Photo profilePicture = new Photo(null, user.get(), path, profilePictureValidSelfie);
//        profilePictureRepository.save(profilePicture);

//        user.get().setProfilePicture(profilePicture);
//        userRepository.save(user.get());

        return user.get();
    }

    @Override
    public User deleteProfilePicture(long userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }

        Photo profilePicture = user.get().getProfilePicture();
        if (profilePicture != null) {
            profilePictureStorageService.delete(profilePicture.getPath());
//            profilePictureRepository.delete(profilePicture);

//            user.get().setProfilePicture(null);
            userRepository.save(user.get());
        }

        return user.get();
    }
}
