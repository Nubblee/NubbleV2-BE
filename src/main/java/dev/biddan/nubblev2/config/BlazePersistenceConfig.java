package dev.biddan.nubblev2.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEntityViews(basePackages = "dev.biddan.nubblev2")
public class BlazePersistenceConfig {

    @Bean
    public CriteriaBuilderFactory createCriteriaBuilderFactory(EntityManagerFactory entityManagerFactory) {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    public EntityViewManager createEntityViewManager(CriteriaBuilderFactory cbf,
            EntityViewConfiguration entityViewConfiguration) {
        return entityViewConfiguration.createEntityViewManager(cbf);
    }
}
