package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
public class ItemsHandlerTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    private List<Item> items = Arrays.asList(new Item(null, "Vax", 100.00)
            ,new Item(null, "Fax", 200.00)
            ,new Item("ABC", "Zax", 250.00)
            ,new Item(null, "Rax", 300.00));

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items).log())
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println("is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        client.get()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Item.class)
                .hasSize(4);
    }

    @Test
    public void getOneById() {
        client.get()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.price", 250.00);
    }

    @Test
    public void getOneByIdNotFound() {
        client.get()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void createNewItem() {
        Item item = new Item(null, "Lax", 400.00);
        client.post()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                    .isCreated()
                .expectHeader().value("Location", location ->
                    assertTrue(location.contains(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1)))
                .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.desc").isEqualTo("Lax")
                    .jsonPath("$.price").isEqualTo(400.00);
    }

    @Test
    public void delete() {
        client.delete()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody(Void.class);
    }

    @Test
    public void deleteNotFound() {
        client.delete()
                .uri(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "FEF")
                .exchange()
                .expectStatus()
                    .isNotFound();
    }

    @Test
    public void updateOne() {
        Item item = new Item(null, "Lax", 400.00);
        client.put()
                .uri(ItemConstants.ITEMS_END_POINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("ABC")
                .jsonPath("$.desc").isEqualTo("Lax")
                .jsonPath("$.price").isEqualTo(400.00);
    }

    @Test
    public void updateOneNotFound() {
        Item item = new Item(null, "Lax", 400.00);
        client.put()
                .uri(ItemConstants.ITEMS_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}
