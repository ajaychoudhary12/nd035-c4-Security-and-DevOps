package com.example.demo.controllerTests;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserControllerTests {
    private UserController userController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController(userRepository, cartRepository, bCryptPasswordEncoder);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("Ajay");
        mockUser.setPassword("12345678");
        mockUser.setCart(new Cart());

        when(userRepository.findByUsername("Ajay")).thenReturn(mockUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(bCryptPasswordEncoder.encode("12345678")).thenReturn("12345678");
    }

    @Test
    public void findByIdSuccess() {
        ResponseEntity<User> userResponse = userController.findById(1L);
        Assert.assertNotNull(userResponse);
        Assert.assertNotNull(userResponse.getBody());
        Assert.assertEquals("Ajay", userResponse.getBody().getUsername());
        Assert.assertEquals("12345678", userResponse.getBody().getPassword());
        Assert.assertEquals(1L, userResponse.getBody().getId());
    }

    @Test
    public void findByIdFailNotFound() {
        ResponseEntity<User> userResponse = userController.findById(2L);
        Assert.assertNull(userResponse.getBody());
        Assert.assertEquals(404, userResponse.getStatusCodeValue());
    }

    @Test
    public void findByUserName() {
        ResponseEntity<User> userResponse = userController.findByUserName("Ajay");
        Assert.assertNotNull(userResponse);
        Assert.assertNotNull(userResponse.getBody());
        Assert.assertEquals("Ajay", userResponse.getBody().getUsername());
        Assert.assertEquals("12345678", userResponse.getBody().getPassword());
        Assert.assertEquals(1L, userResponse.getBody().getId());
    }

    @Test
    public void findByUserNameFailNotFound() {
        ResponseEntity<User> userResponse = userController.findByUserName("Vijay");
        Assert.assertNull(userResponse.getBody());
        Assert.assertEquals(404, userResponse.getStatusCodeValue());
    }

    @Test
    public void createUserSuccess() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Ajay");
        createUserRequest.setPassword("12345678");
        createUserRequest.setConfirmPassword("12345678");
        ResponseEntity<User> userResponse = userController.createUser(createUserRequest);

        Assert.assertNotNull(userResponse);
        Assert.assertNotNull(userResponse.getBody());
        Assert.assertEquals("Ajay", userResponse.getBody().getUsername());
        Assert.assertEquals("12345678", userResponse.getBody().getPassword());
    }

    @Test
    public void createUserFailDueToShortPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Ajay");
        createUserRequest.setPassword("12345");
        createUserRequest.setConfirmPassword("12345");
        ResponseEntity<User> userResponse = userController.createUser(createUserRequest);

        Assert.assertNull(userResponse.getBody());
        Assert.assertEquals(400, userResponse.getStatusCodeValue());
    }

    @Test
    public void createUserFailDueToMismatchPass() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Ajay");
        createUserRequest.setPassword("12345678");
        createUserRequest.setConfirmPassword("123458765");
        ResponseEntity<User> userResponse = userController.createUser(createUserRequest);

        Assert.assertNull(userResponse.getBody());
        Assert.assertEquals(400, userResponse.getStatusCodeValue());
    }
}
