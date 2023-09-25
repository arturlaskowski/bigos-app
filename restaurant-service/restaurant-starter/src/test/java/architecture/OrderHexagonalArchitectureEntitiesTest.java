package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class OrderHexagonalArchitectureEntitiesTest {

    public static final String RESTAURANT_SERVICE_BASE_PACKAGE = "com.bigos.restaurant";

    @Test
    void entitiesShouldNotDependOnAdapters() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages(RESTAURANT_SERVICE_BASE_PACKAGE));
    }

    @Test
    void entitiesShouldNotDependOnStarter() {
        noClasses()
                .that()
                .resideInAPackage("..entities..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..starter..")
                .check(new ClassFileImporter().importPackages(RESTAURANT_SERVICE_BASE_PACKAGE));
    }
}