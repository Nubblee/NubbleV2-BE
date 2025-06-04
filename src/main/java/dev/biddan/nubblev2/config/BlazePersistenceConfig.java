package dev.biddan.nubblev2.config;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import jakarta.persistence.EntityManagerFactory;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BlazePersistenceConfig {

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    public EntityViewConfiguration entityViewConfiguration() {
        EntityViewConfiguration config = EntityViews.createDefaultConfiguration();

        registerEntityViewsFromPackage(config);

        return config;
    }

    @Bean
    public EntityViewManager createEntityViewManager(
            CriteriaBuilderFactory cbf,
            EntityViewConfiguration entityViewConfiguration) {
        return entityViewConfiguration.createEntityViewManager(cbf);
    }

    private static void registerEntityViewsFromPackage(EntityViewConfiguration config) {
        Reflections reflections = new Reflections("dev.biddan.nubblev2");
        Set<Class<?>> entityViews = reflections.getTypesAnnotatedWith(
                com.blazebit.persistence.view.EntityView.class);

        for (Class<?> entityView : entityViews) {
            config.addEntityView(entityView);
        }
    }
}
