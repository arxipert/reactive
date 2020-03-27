package com.hailtosg.reactive.router;

import com.hailtosg.reactive.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SampleRouterConfig {

  @Bean
  public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction) {
    return RouterFunctions.route(RequestPredicates.GET("/functional/flux")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::flx)
            .andRoute(RequestPredicates.GET("/functional/mono")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
  }
}
