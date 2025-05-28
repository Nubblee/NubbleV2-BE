package dev.biddan.nubblev2;

import com.google.common.base.CaseFormat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner implements InitializingBean {

    @Autowired
    private EntityManager entityManager;

    private Set<String> tableNames;


    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .map(this::getTableName)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional
    public void cleanDatabase() {
        entityManager.flush();

        // postgre truncate
        String tableList = String.join(", ", tableNames);
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableList + " RESTART IDENTITY CASCADE").executeUpdate();
    }

    private String getTableName(EntityType<?> entityType) {
        if (entityType.getJavaType().isAnnotationPresent(Table.class)) {
            String tableName = entityType.getJavaType().getAnnotation(Table.class).name();
            if (!tableName.isBlank()) {
                return tableName;
            }
        }

        // @Table이 없거나 name이 비어있는 경우 클래스 이름을 기반으로 테이블 이름 생성
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName());
    }
}
