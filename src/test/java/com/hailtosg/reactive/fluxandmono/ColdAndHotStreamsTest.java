package com.hailtosg.reactive.fluxandmono;

import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ColdAndHotStreamsTest {


    @Test
    @SneakyThrows
    public void coldTes(){
        Flux<String> flux = Flux.just("Spring", "Reactive", "Boot")
                .delayElements(Duration.ofSeconds(1));

        flux.subscribe(e -> System.out.println("1"+ e));
        Thread.sleep(2000);



        flux.subscribe(e -> System.out.println("2"+ e));
        Thread.sleep(4000);
    }

    @Test
    @SneakyThrows
    public void hotTest(){
        Flux<String> flux = Flux.just("Spring", "Reactive", "Boot")
                .delayElements(Duration.ofSeconds(1));
        ConnectableFlux<String> connectableFlux = flux.publish();

        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("1 "+ s));
        Thread.sleep(2000);
        connectableFlux.subscribe(s -> System.out.println("2 "+ s));
        Thread.sleep(3000);
    }
}
