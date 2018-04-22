package cz.fb.manaus.core.category.categorizer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class AbstractRegexpResolver {
    private final String prefix;

    protected AbstractRegexpResolver(String prefix) {
        this.prefix = prefix;
    }

    protected static Pattern compile(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }


    protected Set<String> getCategories(String name, Map<String, Pattern> patterns) {
        if (name == null) {
            return Set.of();
        }
        Set<String> result = new HashSet<>();
        for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            Pattern pattern = entry.getValue();
            Matcher matcher = pattern.matcher(name);
            if (matcher.matches()) {
                String key = entry.getKey();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    key = key.replace("{" + i + "}", matcher.group(i));
                }
                result.add(key);
            }
        }
        return result;
    }

    protected String addPrefix(String key) {
        return prefix + key;
    }

}
