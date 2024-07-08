package com.example.demo.controllerTests;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemControllerTests {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController(itemRepository);

        Item mockItem = new Item();
        mockItem.setId(1L);
        mockItem.setName("Sofa");
        mockItem.setDescription("Sofa Desc");

        BigDecimal price = BigDecimal.valueOf(10.0);
        mockItem.setPrice(price);

        List<Item> items = new ArrayList<>();
        items.add(mockItem);

        when(itemRepository.findAll()).thenReturn(items);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(mockItem));
        when(itemRepository.findByName("Sofa")).thenReturn(items);
    }

    @Test
    public void getAllItemsSuccess() {
        ResponseEntity<List<Item>> itemsResponse = itemController.getItems();
        Assert.assertNotNull(itemsResponse);
        Assert.assertEquals(1, itemsResponse.getBody().size() );
    }

    @Test
    public void getItemByIdSuccess() {
        ResponseEntity<Item> item = itemController.getItemById(1L);
        Assert.assertNotNull(item);
        Assert.assertEquals("Sofa", item.getBody().getName() );
        Assert.assertEquals("Sofa Desc", item.getBody().getDescription());
    }

    @Test
    public void getItemByIdFail() {
        ResponseEntity<Item> item = itemController.getItemById(2L);
        Assert.assertNull(item.getBody());
        Assert.assertEquals(404, item.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameSuccess() {
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Sofa");
        Assert.assertNotNull(items);
        Assert.assertEquals(1, items.getBody().size());
    }

    @Test
    public void getItemsByName404() {
        ResponseEntity<List<Item>> items = itemController.getItemsByName("Table");
        Assert.assertNull(items.getBody());
        Assert.assertEquals(404, items.getStatusCodeValue());
    }
}
