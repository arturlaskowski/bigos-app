package architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

 class NoSpringInDomainLogicTest {

    @Test
     void domainShouldNotDependOnSpring() {
        noClasses()
                .that()
                .resideInAPackage(
                        "..domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("org.springframework..")
                .check(new ClassFileImporter().importPackages("com.bigos.restaurant"));

    }
}