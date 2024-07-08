package com.example.demo.controllerTests;

import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTests {

    private OrderController orderController;
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController(orderRepository, userRepository);

        List<Item> items = getItems();
        User user = getMockUser(items);

        UserOrder mockOrder = new UserOrder();
        mockOrder.setId(1L);
        mockOrder.setItems(items);
        mockOrder.setUser(user);
        List<UserOrder> orders = new ArrayList<>();
        orders.add(mockOrder);

        when(userRepository.findByUsername("Ajay")).thenReturn(user);
        when(orderRepository.findByUser(Mockito.any())).thenReturn(orders);
    }

    @Test
    public void submitOrderSuccess() {
        ResponseEntity<UserOrder> userOrderResponse = orderController.submit("Ajay");
        Assert.assertNotNull(userOrderResponse);
        Assert.assertEquals(1, userOrderResponse.getBody().getItems().size());
        Assert.assertNotNull(userOrderResponse.getBody());
    }

    @Test
    public void submitOrderWithWrongUsername() {
        ResponseEntity<UserOrder> userOrderResponse = orderController.submit("Vijay");
        Assert.assertNull(userOrderResponse.getBody());
        Assert.assertEquals(404, userOrderResponse.getStatusCodeValue());
    }

    @Test
    public void getOrderForUserSuccess() {
        ResponseEntity<List<UserOrder>> orderListResponse = orderController.getOrdersForUser("Ajay");
        Assert.assertNotNull(orderListResponse);
        Assert.assertEquals(1, orderListResponse.getBody().size());
        Assert.assertNotNull(orderListResponse.getBody());
    }

    @Test
    public void getOrderForUserNotFound() {
        ResponseEntity<List<UserOrder>> orderListResponse = orderController.getOrdersForUser("Vijay");
        Assert.assertNull(orderListResponse.getBody());
        Assert.assertEquals(404, orderListResponse.getStatusCodeValue());
    }

    private User getMockUser(List<Item> items) {
        User user = new User();
        user.setId(0);
        user.setUsername("Ajay");
        user.setPassword("12345678");

        Cart cart = new Cart();
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(2.99);
        cart.setTotal(total);
        user.setCart(cart);

        return user;
    }

    private List<Item> getItems() {
        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Sofa");
        mockItem.setDescription("Sofa Desc");

        BigDecimal price = BigDecimal.valueOf(10.0);
        mockItem.setPrice(price);

        List<Item> items = new ArrayList<>();
        items.add(mockItem);

        return items;
    }
}
