package com.hailtosg.reactive.controller.v1;

import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.document.ItemCapped;
import com.hailtosg.reactive.repository.ItemReactiveCappedRepository;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.hailtosg.reactive.constants.ItemConstants.CAPPED_ITEMS_END_POINT_V1;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient(timeout = "36000")
@ActiveProfiles("test")
@Slf4j
public class ItemCappedControllerTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    private MongoOperations mongoOperations;

    @Before
    public void setUp() {

        mongoOperations.dropCollection(ItemCapped.class);

        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty()
                        .maxDocuments(20)
                        .size(5000)
                        .capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(1))
                .map(i -> new ItemCapped(String.valueOf(i), "CapItem #" + i, Double.valueOf(i)))
                .take(5);

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> log.info("Inserted capped item is: " + itemCapped))
                .blockLast();
    }

    @Test
    public void getStreamOfItems() {
        Flux<ItemCapped> fluxItemCappedStream = client.get()
                .uri(CAPPED_ITEMS_END_POINT_V1)
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody().log()
                .take(5);

        StepVerifier.create(fluxItemCappedStream)
                .expectNextCount(5)
                .verifyComplete();
    }
}
