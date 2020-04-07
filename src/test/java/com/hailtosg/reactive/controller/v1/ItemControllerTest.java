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

import static com.hailtosg.reactive.constants.ItemConstants.ITEMS_END_POINT_V1;
import static com.hailtosg.reactive.constants.ItemConstants.ID_SUFFIX;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
public class ItemControllerTest {

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
                .uri(ITEMS_END_POINT_V1)
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
                .uri(ITEMS_END_POINT_V1.concat(ID_SUFFIX), "ABC")
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
                .uri(ITEMS_END_POINT_V1.concat(ID_SUFFIX), "DEF")
                .exchange()
                .expectStatus()
                    .isNotFound();
    }

    @Test
    public void createNewItem() {
        Item item = new Item(null, "Lax", 400.00);
        client.post()
                .uri(ITEMS_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                    .isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.desc").isEqualTo("Lax")
                .jsonPath("$.price").isEqualTo(400.00);
    }

    @Test
    public void delete() {
        client.delete()
                .uri(ITEMS_END_POINT_V1.concat(ID_SUFFIX), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateOne() {
        Item item = new Item(null, "Lax", 400.00);
        client.put()
                .uri(ITEMS_END_POINT_V1.concat(ID_SUFFIX), "ABC")
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
                .uri(ItemConstants.ITEMS_END_POINT_V1.concat(ID_SUFFIX), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus()
                    .isNotFound();
    }

    @Test
    public void getRTException() {
        client.get()
                .uri(ITEMS_END_POINT_V1.concat("/rte"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("RTE occurred");
    }
}
