package ro.unibuc.springlab8example1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.unibuc.springlab8example1.domain.User;
import ro.unibuc.springlab8example1.domain.UserType;
import ro.unibuc.springlab8example1.dto.UserDto;
import ro.unibuc.springlab8example1.exception.AlreadyUsedException;
import ro.unibuc.springlab8example1.exception.UserNotFoundException;
import ro.unibuc.springlab8example1.mapper.UserMapper;
import ro.unibuc.springlab8example1.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDto create(UserDto userDto, UserType type) {
        checkUsernameNotUsed(userDto.getUsername());
        checkCnpNotUsed(userDto.getCnp());

        User user = userMapper.mapToEntity(userDto);
        user.setUserType(type);
        User savedUser = userRepository.save(user);

        return userMapper.mapToDto(savedUser);
    }

    public UserDto getOne(String username) {
        return userMapper.mapToDto(userRepository.get(username));
    }

    public UserDto updateById(Long id, UserDto userDto) {
        checkExistenceById(id);

        User existingUser = userRepository.getById(id);
        if (!existingUser.getUsername().equals(userDto.getUsername())) {
            checkUsernameNotUsed(userDto.getUsername());
        }
        if (!existingUser.getUserDetails().getCnp().equals(userDto.getCnp())) {
            checkCnpNotUsed(userDto.getCnp());
        }

        User user = userMapper.mapToEntity(userDto);
        return userMapper.mapToDto(userRepository.updateById(id, user));
    }

    public void deleteById(Long id) {
        checkExistenceById(id);
        userRepository.deleteById(id);
    }

    public List<UserDto> getByType(UserType type) {
        return userRepository
                .getByType(type).stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void checkExistenceById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found!");
        }
    }

    private void checkUsernameNotUsed(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AlreadyUsedException("This username is already used!");
        }
    }

    private void checkCnpNotUsed(String cnp) {
        if (userRepository.existsByCnp(cnp)) {
            throw new AlreadyUsedException("This cnp is already used!");
        }
    }
}
