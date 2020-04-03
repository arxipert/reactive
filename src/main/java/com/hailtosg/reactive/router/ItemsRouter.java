package com.hailtosg.reactive.router;

import com.hailtosg.reactive.constants.ItemConstants;
import com.hailtosg.reactive.handler.ItemsHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandlerFunction handlerFunction) {
        return RouterFunctions.route(RequestPredicates.GET(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::items)
                .andRoute(RequestPredicates.GET(ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1+"/{id}")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handlerFunction::itemById);
    }
}
