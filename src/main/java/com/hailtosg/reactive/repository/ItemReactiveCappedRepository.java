package com.hailtosg.reactive.repository;

import com.hailtosg.reactive.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

  @Tailable
  Flux<ItemCapped> findItemCappedBy();

}
