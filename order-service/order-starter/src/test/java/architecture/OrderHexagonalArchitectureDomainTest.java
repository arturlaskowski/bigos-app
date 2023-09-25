package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class OrderHexagonalArchitectureDomainTest {

    public static final String ORDER_SERVICE_BASE_PACKAGE = "com.bigos.order";

    @Test
    void domainShouldNotDependOnAdapters() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_BASE_PACKAGE));
    }

    @Test
    void domainShouldNotDependOnApplication() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..application..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_BASE_PACKAGE));
    }

    @Test
    void domainShouldNotDependOnStarter() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..starter..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_BASE_PACKAGE));
    }

    @Test
    void domainShouldNotDependOnEntities() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..entities..")
                .check(new ClassFileImporter().importPackages(ORDER_SERVICE_BASE_PACKAGE));
    }
}