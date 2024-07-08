package com.example.demo.controllerTests;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CartControllerTests {

    private CartController cartController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        cartController = new CartController(userRepository, cartRepository, itemRepository);

        User user = new User();
        user.setId(0);
        user.setUsername("Ajay");
        user.setPassword("12345678");
        user.setCart(new Cart());
        when(userRepository.findByUsername("Ajay")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Sofa");
        item.setPrice(BigDecimal.valueOf(10.0));
        item.setDescription("Sofa Desc");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    public void addToCartSuccess() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("Ajay");
        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        Assert.assertNotNull(cartResponse);
        Assert.assertNotNull(cartResponse.getBody());
        Assert.assertEquals(BigDecimal.valueOf(10.0), cartResponse.getBody().getTotal());
        Assert.assertEquals(1, cartResponse.getBody().getItems().size());
    }

    @Test
    public void addToCartFailInvalidUserName() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("Vijay");
        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        Assert.assertNull(cartResponse.getBody());
        Assert.assertEquals(404, cartResponse.getStatusCodeValue());
    }

    @Test
    public void addToCartFailInvalidItemId() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(3L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("Ajay");
        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        Assert.assertNull(cartResponse.getBody());
        Assert.assertEquals(404, cartResponse.getStatusCodeValue());
    }

    @Test
    public void removeFromCartSuccess() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(5);
        modifyCartRequest.setUsername("Ajay");
        ResponseEntity<Cart> cartResponse = cartController.addTocart(modifyCartRequest);

        ModifyCartRequest modifyCartRequest1 = new ModifyCartRequest();
        modifyCartRequest1.setItemId(1L);
        modifyCartRequest1.setQuantity(2);
        modifyCartRequest1.setUsername("Ajay");
        ResponseEntity<Cart> cartResponse1 = cartController.removeFromcart(modifyCartRequest1);

        Assert.assertNotNull(cartResponse1);
        Assert.assertEquals(200, cartResponse1.getStatusCodeValue());
        Assert.assertNotNull(cartResponse1.getBody());
        Assert.assertEquals(3, cartResponse1.getBody().getItems().size());
    }
}
