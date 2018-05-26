package cz.fb.manaus.reactor.filter;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import cz.fb.manaus.spring.DatabaseComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;

public class _AbstractUnprofitableCategoriesRegistryTest extends AbstractDatabaseTestCase {

    @Autowired
    private TestUnprofitableCategoriesRegistry registry;
    @Autowired
    private Test2UnprofitableCategoriesRegistry registry2;

    private ProfitRecord pr(String category, double profitAndLoss, int betCount) {
        return new ProfitRecord(category, profitAndLoss, betCount, 0, 2d, 0.06);
    }

    @Before
    public void setUp() {
        registry.setWhitelist("white.tes");
    }

    @After
    public void tearDown() {
        registry.setWhitelist("white.tes");
    }

    @Test
    public void testBlacklistThreshold() {
        assertThat(registry.getBlacklist(0.1d, 1, 110,
                List.of(pr("horror", -10d, 10)).stream(),
                Set.of()), hasItem("horror"));
        assertThat(registry.getBlacklist(0.1d, 1, 90,
                List.of(pr("horror", -10d, 10)).stream(),
                Set.of()), not(hasItem("horror")));
        assertThat(registry.getBlacklist(0.1d, 0, 110,
                List.of(pr("horror", -10d, 10)).stream(),
                Set.of()), not(hasItem("horror")));
    }

    @Test
    public void testBlacklistSort() {
        assertThat(registry.getBlacklist(0.1d, 1, 110,
                List.of(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Set.of()),
                allOf(hasItem("horror"), not(hasItem("weak")), not(hasItem("bad")))
        );
        assertThat(registry.getBlacklist(0.1d, 2, 110,
                List.of(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Set.of()),
                allOf(hasItem("horror"), not(hasItem("weak")), hasItem("bad"))
        );
        assertThat(registry.getBlacklist(0.1d, 3, 110,
                List.of(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Set.of()),
                allOf(hasItem("horror"), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testBlacklistDuplicate() {
        assertThat(registry.getBlacklist(0.1d, 2, 110,
                List.of(pr("horror", -10d, 10),
                        pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(),
                Set.of("horror")),
                allOf(not(hasItem("horror")), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testBlacklistWhiteList() {
        assertThat(registry.getBlacklist(0.1d, 2, 110,
                List.of(pr("white.test", -10d, 10),
                        pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(),
                Set.of()),
                allOf(not(hasItem("white.test")), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testUpdateFilterPrefix() {
        var configUpdate = ConfigUpdate.empty(Duration.ZERO);
        var properties = configUpdate.getSetProperties();
        registry.updateBlacklists(List.of(pr(MarketCategories.ALL, 10d, 100),
                pr("weak1", -1d, 5),
                pr("not_match", -1d, 2),
                pr("weak2", -1d, 5)), configUpdate);

        assertThat(properties.get("unprofitable.black.list.test.5"), is("weak1,weak2"));
    }

    @Test
    public void testThreshold() {
        assertThat(registry.getThreshold(10), is(0.1d));
    }

    @Test
    public void testSave() {
        var configUpdate = ConfigUpdate.empty(Duration.ZERO);
        registry.saveBlacklist(10, Set.of("weak1", "weak2", "weak3"), configUpdate);
        var properties = configUpdate.getSetProperties();

        assertThat(properties.get("unprofitable.black.list.test.10"), is("weak1,weak2,weak3"));

    }

    @Test
    public void testSavedBlacklist() {
        var configUpdate = ConfigUpdate.empty(Duration.ZERO);
        var properties = configUpdate.getSetProperties();
        registry.saveBlacklist(10, Set.of("weak10_1", "weak10_2", "weak10_3"), configUpdate);
        assertThat(properties.get("unprofitable.black.list.test.10"), is("weak10_1,weak10_2,weak10_3"));
        registry.saveBlacklist(5, Set.of("weak5_1", "weak5_2", "weak5_3"), configUpdate);
        assertThat(properties.get("unprofitable.black.list.test.5"), is("weak5_1,weak5_2,weak5_3"));
    }

    @DatabaseComponent
    private static class TestUnprofitableCategoriesRegistry extends AbstractUnprofitableCategoriesRegistry {
        public TestUnprofitableCategoriesRegistry() {
            super("test", Duration.ofDays(30), Optional.of(Side.LAY), 0,
                    "weak", Map.of(5, 2, 2, 7));
        }
    }

    @DatabaseComponent
    private static class Test2UnprofitableCategoriesRegistry extends AbstractUnprofitableCategoriesRegistry {
        public Test2UnprofitableCategoriesRegistry() {
            super("test2", Duration.ofDays(30), Optional.of(Side.LAY), 0,
                    "weak", Map.of(10, 1));
        }
    }

}
