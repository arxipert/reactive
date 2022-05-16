package com.hailtosg.reactive.fluxandmono;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@AutoConfigureWebTestClient(timeout = "36000")
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient client;

    @Test
    public void flxApproachOne(){
        Flux<Integer> streamFlux = client.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .returnResult(Integer.class)
                .getResponseBody().log();

        StepVerifier.create(streamFlux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();

    }

    @Test
    public void flxApproachTwo() {
        client.get().uri("/fluxFlix")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectHeader()
                    .contentType(MediaType.APPLICATION_STREAM_JSON)
                .expectBodyList(Integer.class)
                    .hasSize(20);
    }

    @Test
    public void flxApproachThree() {
        List<Integer> expected = Arrays.asList(1,2,3);

        EntityExchangeResult<List<Integer>> result = client.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                    .isOk()
                .expectBodyList(Integer.class)
                .returnResult();
        Assert.assertEquals(expected, result.getResponseBody());
    }

    @Test
    public void flxApproachThreePointOne() {
        List<Integer> expected = Arrays.asList(1,2,3);

        client.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> Assert.assertEquals(expected, response.getResponseBody()));
    }

    @Test
    public void flxInfiniteApproach() {

        Flux<Long> streamFlux = client.get().uri("/fluxInfinite")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .returnResult(Long.class)
                .getResponseBody().log();

        StepVerifier.create(streamFlux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .thenCancel()
                .verify();
    }
    @Test
    public void monoOne() {
        Integer i = 1;
        client.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> Assert.assertEquals(i, response.getResponseBody()));
    }

    @Test
    public void flElementsTestWithoutError(){
        Flux<String> flux = Flux.just("Sprig", "Reactive", "Boot");

        StepVerifier.create(flux)
                .expectNext("Sprig")
                .expectNext("Reactive")
                .expectNext("Boot")
                .verifyComplete();
    }
    @Test
    public void mnTest(){
        Mono<String> mono =  Mono.just("Mono");
        mono.log().subscribe(System.out::println);

        StepVerifier.create(mono).expectNext("Mono").verifyComplete();
    }
}
