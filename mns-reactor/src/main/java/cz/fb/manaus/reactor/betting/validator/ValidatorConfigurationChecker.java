package cz.fb.manaus.reactor.betting.validator;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

@Component
public class ValidatorConfigurationChecker {

    @Autowired(required = false)
    private List<Validator> validators = new LinkedList<>();

    @PostConstruct
    public void checkValidators() {
        validators.forEach(this::checkConfiguration);
    }

    void checkConfiguration(Validator validator) {
        if (validator.isDowngradeAccepting()) {
            Preconditions.checkState(validator.isPriceRequired(), "downgrade accepting while price not required");
        }
    }

}
