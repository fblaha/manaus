package cz.fb.manaus.core.category.categorizer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        var result = new HashSet<String>();
        for (var entry : patterns.entrySet()) {
            var pattern = entry.getValue();
            var matcher = pattern.matcher(name);
            if (matcher.matches()) {
                var key = entry.getKey();
                for (var i = 1; i <= matcher.groupCount(); i++) {
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
