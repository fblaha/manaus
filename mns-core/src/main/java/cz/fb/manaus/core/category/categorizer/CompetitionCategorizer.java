package cz.fb.manaus.core.category.categorizer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import cz.fb.manaus.core.model.Competition;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
final public class CompetitionCategorizer extends AbstractDelegatingCategorizer {

    public static final String PREFIX = "competition_";

    public CompetitionCategorizer() {
        super(PREFIX);
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        Competition competition = market.getCompetition();
        if (competition == null || Strings.isNullOrEmpty(competition.getName())) {
            return Collections.singleton("none");
        } else {
            return Collections.singleton(normalize(competition.getName()));
        }
    }

    private String normalize(String name) {
        name = CharMatcher.WHITESPACE.or(CharMatcher.JAVA_LETTER_OR_DIGIT).retainFrom(name);
        name = CharMatcher.WHITESPACE.replaceFrom(name, '_');
        name = name.substring(0, Math.min(name.length(), 30));
        return name;
    }
}