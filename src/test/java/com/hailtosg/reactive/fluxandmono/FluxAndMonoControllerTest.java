package com.hailtosg.reactive.fluxandmono;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoControllerTest {

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
