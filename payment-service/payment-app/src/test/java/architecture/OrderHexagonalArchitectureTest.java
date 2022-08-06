package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class OrderHexagonalArchitectureTest {

    @Test
    void domainShouldNotDependOnAdapters() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..adapters..")
                .check(new ClassFileImporter().importPackages("com.bigos.payment"));
    }

    @Test
    void domainShouldNotDependOnApp() {
        noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..app..")
                .check(new ClassFileImporter().importPackages("com.bigos.payment"));
    }

}