package com.hailtosg.reactive.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getFlx(){
        return Flux.just(1,2,3).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/fluxFlix", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> getFlxFlix(){
        return Flux.range(1, 20).delayElements(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/fluxInfinite", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> getFlxInfinite(){
        return Flux.interval(Duration.ofSeconds(1)).log();
    }

    @GetMapping("/mono")
    public Mono<Integer> getMono(){
        return Mono.just(1).log();
    }
}
