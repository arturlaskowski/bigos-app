package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class OrderHexagonalArchitectureApplicationTest {

    public static final String PAYMENT_SERVICE_BASE_PACKAGE = "com.bigos.payment";

    @Test
    void applicationShouldNotDependOnAdapters() {
        noClasses()
                .that()
                .resideInAPackage("..application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages(PAYMENT_SERVICE_BASE_PACKAGE));
    }

    @Test
    void applicationShouldNotDependOnEntities() {
        noClasses()
                .that()
                .resideInAPackage("..application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..entities..")
                .check(new ClassFileImporter().importPackages(PAYMENT_SERVICE_BASE_PACKAGE));
    }

    @Test
    void applicationShouldNotDependOnStarter() {
        noClasses()
                .that()
                .resideInAPackage("..application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..starter..")
                .check(new ClassFileImporter().importPackages(PAYMENT_SERVICE_BASE_PACKAGE));
    }

}