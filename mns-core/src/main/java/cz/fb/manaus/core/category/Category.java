package cz.fb.manaus.core.category;

import cz.fb.manaus.core.MarketCategories;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class Category {
    public static final Pattern PATTERN = Pattern.compile("^((?:market_)?[a-zA-Z0-9]+_)(.*)");
    public static final String MARKET_PREFIX = "market_";


    private final String category;

    private Category(String category) {
        this.category = category;
    }

    public static Category parse(String category) {
        return new Category(category);
    }

    public boolean isAll() {
        return MarketCategories.ALL.equals(category);
    }

    public String getBase() {
        return getMatcher().group(1);
    }


    public String getTail() {
        return getMatcher().group(2);
    }

    private Matcher getMatcher() {
        Matcher matcher = PATTERN.matcher(category);
        checkState(matcher.matches());
        checkState(!MARKET_PREFIX.equals(matcher.group(1)));
        return matcher;
    }


}
