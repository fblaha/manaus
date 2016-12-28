package cz.fb.manaus.core.test;

import cz.fb.manaus.core.provider.ProviderConfigurationValidator;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assume.assumeTrue;

abstract public class AbstractRemoteTestCase extends AbstractLocalTestCase {

    @Autowired
    private Optional<ProviderConfigurationValidator> validator;


    @Before
    public void checkEnvironment() {
        assumeTrue(validator.isPresent() && validator.get().isConfigured());
    }

}
