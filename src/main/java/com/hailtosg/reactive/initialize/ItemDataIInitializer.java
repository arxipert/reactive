package com.hailtosg.reactive.initialize;

import com.hailtosg.reactive.document.Item;
import com.hailtosg.reactive.document.ItemCapped;
import com.hailtosg.reactive.repository.ItemReactiveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataIInitializer implements CommandLineRunner {

    private ItemReactiveRepository itemReactiveRepository;
    private MongoOperations mongoOperations;

    public ItemDataIInitializer (ItemReactiveRepository itemRepository, MongoOperations mongoOperations){
        this.itemReactiveRepository = itemRepository;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void run(String... args) {
        initialDataSetUp();
        createCapCollection();
    }

    private void initialDataSetUp() {
        List<Item> items = Arrays.asList(new Item(null, "Vax", 100.00)
                ,new Item(null, "Fax", 200.00)
                ,new Item("ABC", "Zax", 250.00)
                ,new Item(null, "Rax", 300.00));
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items).log())
                .flatMap(itemReactiveRepository::save)
                .subscribe();
    }

    private void createCapCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(
                ItemCapped.class,
                CollectionOptions.empty()
                        .maxDocuments(20)
                        .size(Integer.MAX_VALUE)
                        .capped());
    }
}
