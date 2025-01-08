package ec.com.sofka.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "ec.com.sofka.database.bank",
       reactiveMongoTemplateRef = "accountMongoTemplate")
public class AccountMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Primary
    @Bean(name = "accountsDatabaseFactory")
    public ReactiveMongoDatabaseFactory accountsDatabaseFactory() {
        MongoClient mongoClient = MongoClients.create(uri);
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, "bank_management");
    }


    @Primary
    @Bean(name = "accountMongoTemplate")
    public ReactiveMongoTemplate accountsMongoTemplate(@Qualifier("accountsDatabaseFactory") ReactiveMongoDatabaseFactory accountsDatabaseFactory) {
        return new ReactiveMongoTemplate(accountsDatabaseFactory);
    }
}
