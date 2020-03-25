package com.hailtosg.reactive.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.print.attribute.standard.Media;
import java.time.Duration;

@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> getFlx(){
        return Flux.just(1,2,3).delayElements(Duration.ofSeconds(2)).log();
    }

    @GetMapping(value = "/fluxFlix", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> getFlxFlix(){
        return Flux.range(1, 20).delayElements(Duration.ofSeconds(2)).log();
    }

}
