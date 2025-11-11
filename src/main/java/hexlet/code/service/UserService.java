package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + "not found"));
        return userMapper.map(user);
    }

    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        User user = userMapper.map(userCreateDTO);
        String password = passwordEncoder.encode(user.getPasswordDigest());
        user.setPasswordDigest(password);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public UserDTO updateUser(UserUpdateDTO userUpdateDTO, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + "not found"));
        userMapper.update(userUpdateDTO, user);
        String password = passwordEncoder.encode(user.getPasswordDigest());
        user.setPasswordDigest(password);
        userRepository.save(user);
        return userMapper.map(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
