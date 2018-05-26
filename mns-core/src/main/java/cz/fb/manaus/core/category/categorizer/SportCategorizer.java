package cz.fb.manaus.core.category.categorizer;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.model.Market;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;


@Component
final public class SportCategorizer extends AbstractDelegatingCategorizer {

    public static final Pattern AMERICAN_FOOTBALL = Pattern.compile("american\\s+football");
    public static final Pattern MOTOR_SPORT = Pattern.compile("motor\\s+sport");
    public static final Pattern ICE_HOCKEY = Pattern.compile("ice\\s+hockey");
    public static final String PREFIX = "sport_";

    public SportCategorizer() {
        super(PREFIX);
    }

    private Optional<String> getCategory(Market market) {
        var typeName = market.getEventType().getName().toLowerCase();
        if ("basketball".equals(typeName)) {
            return Optional.of(MarketCategories.BASKETBALL);
        } else if (AMERICAN_FOOTBALL.matcher(typeName).matches()) {
            return Optional.of(MarketCategories.AMERICAN_FOOTBALL);
        } else if (ICE_HOCKEY.matcher(typeName).matches()) {
            return Optional.of(MarketCategories.ICE_HOCKEY);
        } else if (MOTOR_SPORT.matcher(typeName).matches()) {
            return Optional.of(MarketCategories.MOTOR_SPORT);
        } else if ("volleyball".equals(typeName)) {
            return Optional.of(MarketCategories.VOLLEYBALL);
        } else if ("soccer".equals(typeName)) {
            return Optional.of(MarketCategories.SOCCER);
        } else if ("snooker".equals(typeName)) {
            return Optional.of(MarketCategories.SNOOKER);
        } else if ("cricket".equals(typeName)) {
            return Optional.of(MarketCategories.CRICKET);
        } else if ("handball".equals(typeName)) {
            return Optional.of(MarketCategories.HANDBALL);
        } else if (typeName.startsWith("greyhound")) {
            return Optional.of(MarketCategories.GREY_HOUNDS);
        } else if ("cycling".equals(typeName)) {
            return Optional.of(MarketCategories.CYCLING);
        } else if ("baseball".equals(typeName)) {
            return Optional.of(MarketCategories.BASEBALL);
        } else if ("golf".equals(typeName)) {
            return Optional.of(MarketCategories.GOLF);
        } else if ("tennis".equals(typeName)) {
            return Optional.of(MarketCategories.TENNIS);
        } else if (typeName.startsWith("horse")) {
            return Optional.of(MarketCategories.HORSES);
        } else if (typeName.startsWith("financial")) {
            return Optional.of(MarketCategories.FINANCIAL);
        } else if (typeName.startsWith("rugby")) {
            return Optional.of(MarketCategories.RUGBY);
        } else if (typeName.startsWith("politics")) {
            return Optional.of(MarketCategories.POLITICS);
        }
        return Optional.empty();
    }

    @Override
    public Set<String> getCategoryRaw(Market market) {
        var category = getCategory(market);
        return category.map(Set::of).orElse(Set.of());
    }
}
