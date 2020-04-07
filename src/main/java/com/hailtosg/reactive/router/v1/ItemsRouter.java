package com.hailtosg.reactive.router.v1;

import com.hailtosg.reactive.handler.v1.ItemsHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.hailtosg.reactive.constants.ItemConstants.ID_SUFFIX;
import static com.hailtosg.reactive.constants.ItemConstants.ITEMS_FUNCTIONAL_END_POINT_V1;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandlerFunction handlerFunction) {
        return route(GET(ITEMS_FUNCTIONAL_END_POINT_V1)
                        .and(accept(APPLICATION_JSON)), handlerFunction::items)
                .andRoute(GET(ITEMS_FUNCTIONAL_END_POINT_V1 + ID_SUFFIX)
                        .and(accept(APPLICATION_JSON)), handlerFunction::itemById)
                .andRoute(RequestPredicates.POST(ITEMS_FUNCTIONAL_END_POINT_V1)
                        .and(accept(APPLICATION_JSON)), handlerFunction::createItem)
                .andRoute(RequestPredicates.DELETE(ITEMS_FUNCTIONAL_END_POINT_V1 + ID_SUFFIX),
                        handlerFunction::deleteItem)
                .andRoute(RequestPredicates.PUT(ITEMS_FUNCTIONAL_END_POINT_V1 + ID_SUFFIX)
                        .and(accept(APPLICATION_JSON)), handlerFunction::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandlerFunction handlerFunction) {
        return route(GET("/fun/rte")
                .and(accept(APPLICATION_JSON)), handlerFunction::itemsRte);

    }
}
