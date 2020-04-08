package com.hailtosg.reactive.initialize;

import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.document.ItemCapped;
import com.hailtosg.reactive.repository.ItemReactiveCappedRepository;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataIInitializer implements CommandLineRunner {

    private ItemReactiveRepository itemReactiveRepository;
    private ItemReactiveCappedRepository itemReactiveCappedRepository;
    private MongoOperations mongoOperations;

    public ItemDataIInitializer(ItemReactiveRepository itemRepository,
                                ItemReactiveCappedRepository itemReactiveCappedRepository,
                                MongoOperations mongoOperations) {
        this.itemReactiveRepository = itemRepository;
        this.itemReactiveCappedRepository = itemReactiveCappedRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void run(String... args) {
        initialDataSetUp();
        createCappedCollection();
        cappedCollectionSetUp();
    }

    private void initialDataSetUp() {
        List<Item> items = Arrays.asList(
                new Item(null, "Vax", 100.00),
                new Item(null, "Fax", 200.00),
                new Item("ABC", "Zax", 250.00),
                new Item(null, "Rax", 300.00));

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items).log())
                .flatMap(itemReactiveRepository::save)
                .subscribe();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty()
                        .maxDocuments(400)
                        .size(50000)
                        .capped());
    }

    private void cappedCollectionSetUp() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "CapItem #" + i, Double.valueOf(i)));

        itemReactiveCappedRepository.insert(itemCappedFlux)
                .subscribe(itemCapped -> log.info("Inserted capped item is: " + itemCapped));
    }
}
