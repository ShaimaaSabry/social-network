package com.socialnetwork.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.socialnetwork.config.GcpConfig;
import com.socialnetwork.domain.EmailVerificationToken;
import com.socialnetwork.domain.ProfilePicture;
import com.socialnetwork.domain.User;
import com.socialnetwork.dto.CreateUserRequest;
import com.socialnetwork.dto.AuthRequest;
import com.socialnetwork.dto.UpdateUserPasswordRequest;
import com.socialnetwork.dto.UpdateUserRequest;
import com.socialnetwork.dto.UserResponse;
import com.socialnetwork.dto.VerifyUserEmailRequest;
import com.socialnetwork.exception.InvalidUserCredentialsException;
import com.socialnetwork.exception.InvalidUserIdException;
import com.socialnetwork.repository.EmailVerificationTokenRepository;
import com.socialnetwork.repository.ProfilePictureRepository;
import com.socialnetwork.repository.UserRepository;
import com.socialnetwork.util.EmailService;
import com.socialnetwork.util.PhotoVerificationService;
import com.socialnetwork.util.StorageService;

@Service
public class UserServiceImp implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfilePictureRepository profilePictureRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private EmailVerificationTokenRepository emailVerificationTokenRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	GcpConfig gcpConfig;

	@Autowired
	private StorageService profilePictureStorageService;

	@Autowired
	private PhotoVerificationService photoVerificationService;

	@Override
	public List<UserResponse> getAll(String q) {
		List<User> userList = userRepository
				.findAllByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrEmailContainsIgnoreCase(q, q, q);

		List<UserResponse> userResponseList = userList.stream().map(user -> map(user)).collect(Collectors.toList());
		return userResponseList;
	}

	@Override
	public UserResponse getOneById(long userId) throws InvalidUserIdException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		UserResponse userResponse = map(user.get());
		return userResponse;
	}

	@Override
	public UserResponse getOneByEmailAndPassword(AuthRequest authRequest) throws InvalidUserCredentialsException {
		Optional<User> user = userRepository.findFirstByEmail(authRequest.getEmail());
		if (!user.isPresent()) {
			throw new InvalidUserCredentialsException();
		}

		boolean passwordMatches = bCryptPasswordEncoder.matches(authRequest.getPassword(),
				user.get().getPasswordHash());
		if (!passwordMatches) {
			throw new InvalidUserCredentialsException();
		}

		UserResponse userResponse = map(user.get());
		return userResponse;
	}

	@Override
	public UserResponse create(CreateUserRequest createUserRequest) throws AddressException, MessagingException {
		User user = modelMapper.map(createUserRequest, User.class);
		String passwordHash = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		user.setPasswordHash(passwordHash);
		userRepository.save(user);

		EmailVerificationToken emailVerificationToken = new EmailVerificationToken(null, user,
				UUID.randomUUID().toString());
		emailVerificationTokenRepository.save(emailVerificationToken);
		//emailService.sendVerificationEmail(emailVerificationToken);

		UserResponse userResponse = map(user);
		return userResponse;
	}

	@Override
	public void sendVerificationEmail(long userId) throws InvalidUserIdException, AddressException, MessagingException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		EmailVerificationToken emailVerificationToken = new EmailVerificationToken(null, user.get(),
				UUID.randomUUID().toString());
		emailVerificationTokenRepository.save(emailVerificationToken);
		emailService.sendVerificationEmail(emailVerificationToken);
	}

	@Override
	public boolean verifyEmail(VerifyUserEmailRequest verifyUserEmailRequest) {
		Optional<EmailVerificationToken> token = emailVerificationTokenRepository
				.findFirstByToken(verifyUserEmailRequest.getToken());
		if (!token.isPresent()) {
			return false;
		}

		emailVerificationTokenRepository.deleteById(token.get().getId());
		User user = token.get().getUser();
		user.setEmailVerified(true);
		userRepository.save(user);
		return true;
	}

	@Override
	public UserResponse update(long userId, UpdateUserRequest updateUserRequest) throws InvalidUserIdException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		user.get().setFirstName(updateUserRequest.getFirstName());
		user.get().setLastName(updateUserRequest.getLastName());
		userRepository.save(user.get());

		UserResponse userResponse = map(user.get());
		return userResponse;
	}

	@Override
	public UserResponse updateProfilePicture(long userId, MultipartFile profilePictureFile)
			throws InvalidUserIdException, FileNotFoundException, IOException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		String profilePictureBlobName = profilePictureStorageService.upload(gcpConfig.getProfilePictureBucket(),
				profilePictureFile);
		String gcsPath = gcpConfig.getGsBasePath() + gcpConfig.getProfilePictureBucket() + "/" + profilePictureBlobName;
		int faceAnnotationCount = photoVerificationService.countFaceAnnotations(gcsPath);
		boolean profilePictureValidSelfie = false;
		if (faceAnnotationCount == 1) {
			profilePictureValidSelfie = true;
		}

		ProfilePicture profilePicture = new ProfilePicture(null, user.get(), gcpConfig.getProfilePictureBucket(),
				profilePictureBlobName, profilePictureValidSelfie);
		profilePictureRepository.save(profilePicture);
		user.get().setProfilePicture(profilePicture);
		userRepository.save(user.get());

		UserResponse userResponse = map(user.get());
		return userResponse;
	}

	@Override
	public UserResponse deleteProfilePicture(long userId) throws InvalidUserIdException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		ProfilePicture profilePicture = user.get().getProfilePicture();
		if (profilePicture != null) {
			profilePictureStorageService.delete(profilePicture.getBlobName(), profilePicture.getBlobName());
			user.get().setProfilePicture(null);
			userRepository.save(user.get());
			profilePictureRepository.delete(profilePicture);
		}

		UserResponse userResponse = map(user.get());
		return userResponse;
	} 

	@Override
	public boolean updatePassword(long userId, UpdateUserPasswordRequest updateUserPasswordRequest)
			throws InvalidUserIdException {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			throw new InvalidUserIdException();
		}

		boolean passwordMatches = bCryptPasswordEncoder.matches(updateUserPasswordRequest.getCurrentPassword(),
				user.get().getPasswordHash());
		if (!passwordMatches) {
			return false;
		}

		String passwordHash = bCryptPasswordEncoder.encode(updateUserPasswordRequest.getNewPassword());
		user.get().setPasswordHash(passwordHash);
		userRepository.save(user.get());
		return true;
	}

	private UserResponse map(User user) {
		UserResponse userResponse = modelMapper.map(user, UserResponse.class);
		if (user.getProfilePicture() != null) {
			String profilePictureUrl = gcpConfig.getGsBaseUrl() + user.getProfilePicture().getBucketName() + "/"
					+ user.getProfilePicture().getBlobName();
			userResponse.setProfilePictureUrl(profilePictureUrl);
			userResponse.setProfilePictureValidSelfie(user.getProfilePicture().isValidSelfie());
		}
		return userResponse;
	}
}
