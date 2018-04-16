package cz.fb.manaus.reactor.filter;

import cz.fb.manaus.core.MarketCategories;
import cz.fb.manaus.core.maintanance.ConfigUpdate;
import cz.fb.manaus.core.model.ProfitRecord;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.service.PropertiesService;
import cz.fb.manaus.core.test.AbstractDatabaseTestCase;
import cz.fb.manaus.spring.DatabaseComponent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.of;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class _AbstractUnprofitableCategoriesRegistryTest extends AbstractDatabaseTestCase {

    @Autowired
    private TestUnprofitableCategoriesRegistry registry;
    @Autowired
    private Test2UnprofitableCategoriesRegistry registry2;
    @Autowired
    private PropertiesService propertiesService;

    private ProfitRecord pr(String category, double profitAndLoss, int betCount) {
        return new ProfitRecord(category, profitAndLoss, betCount, 0, 2d, 0.06);
    }

    @Before
    public void setUp() throws Exception {
        Mockito.reset(propertiesService);
        registry.setWhiteList("white.tes");
    }

    @After
    public void tearDown() throws Exception {
        registry.setWhiteList("white.tes");
    }

    @Test
    public void testBlackListThreshold() throws Exception {
        assertThat(registry.getBlackList(0.1d, 1, 110,
                asList(pr("horror", -10d, 10)).stream(),
                Collections.emptySet()), hasItem("horror"));
        assertThat(registry.getBlackList(0.1d, 1, 90,
                asList(pr("horror", -10d, 10)).stream(),
                Collections.emptySet()), not(hasItem("horror")));
        assertThat(registry.getBlackList(0.1d, 0, 110,
                asList(pr("horror", -10d, 10)).stream(),
                Collections.emptySet()), not(hasItem("horror")));
    }

    @Test
    public void testBlackListSort() throws Exception {
        assertThat(registry.getBlackList(0.1d, 1, 110,
                asList(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Collections.emptySet()),
                allOf(hasItem("horror"), not(hasItem("weak")), not(hasItem("bad")))
        );
        assertThat(registry.getBlackList(0.1d, 2, 110,
                asList(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Collections.emptySet()),
                allOf(hasItem("horror"), not(hasItem("weak")), hasItem("bad"))
        );
        assertThat(registry.getBlackList(0.1d, 3, 110,
                asList(pr("horror", -10d, 10), pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(), Collections.emptySet()),
                allOf(hasItem("horror"), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testBlackListDuplicate() throws Exception {
        assertThat(registry.getBlackList(0.1d, 2, 110,
                asList(pr("horror", -10d, 10),
                        pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(),
                Collections.singleton("horror")),
                allOf(not(hasItem("horror")), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testBlackListWhiteList() throws Exception {
        assertThat(registry.getBlackList(0.1d, 2, 110,
                asList(pr("white.test", -10d, 10),
                        pr("weak", -1d, 10),
                        pr("bad", -5d, 10)).stream(),
                Collections.emptySet()),
                allOf(not(hasItem("white.test")), hasItem("weak"), hasItem("bad"))
        );
    }

    @Test
    public void testUpdateFilterPrefix() throws Exception {
        ConfigUpdate configUpdate = ConfigUpdate.empty(Duration.ZERO);
        Map<String, String> properties = configUpdate.getSetProperties();
        registry.updateBlackLists(asList(pr(MarketCategories.ALL, 10d, 100),
                pr("weak1", -1d, 5),
                pr("not_match", -1d, 2),
                pr("weak2", -1d, 5)), configUpdate);

        assertThat(properties.get("unprofitable.black.list.test.5"), is("weak1,weak2"));
    }

    @Test
    public void testThreshold() throws Exception {
        assertThat(registry.getThreshold(10), is(0.1d));
    }

    @Test
    public void testSave() throws Exception {
        ConfigUpdate configUpdate = ConfigUpdate.empty(Duration.ZERO);
        registry.saveBlackList(10, of("weak1", "weak2", "weak3"), configUpdate);
        Map<String, String> properties = configUpdate.getSetProperties();

        assertThat(properties.get("unprofitable.black.list.test.10"), is("weak1,weak2,weak3"));

    }

    @Test
    public void testSavedBlackList() throws Exception {
        ConfigUpdate configUpdate = ConfigUpdate.empty(Duration.ZERO);
        Map<String, String> properties = configUpdate.getSetProperties();
        registry.saveBlackList(10, of("weak10_1", "weak10_2", "weak10_3"), configUpdate);
        assertThat(properties.get("unprofitable.black.list.test.10"), is("weak10_1,weak10_2,weak10_3"));
        registry.saveBlackList(5, of("weak5_1", "weak5_2", "weak5_3"), configUpdate);
        assertThat(properties.get("unprofitable.black.list.test.5"), is("weak5_1,weak5_2,weak5_3"));
    }

    @Test
    public void testGetBlackList() throws Exception {
        when(propertiesService.list(any()))
                .thenReturn(Map.of(
                        "unprofitable.black.list.test.10", "weak10_1,weak10_2,weak10_3",
                        "unprofitable.black.list.test.5", "weak5_1,weak5_2,weak5_3"));
        Set<String> blackList = registry.getSavedBlackList();
        assertThat(blackList.size(), is(6));
        assertThat(blackList, hasItems("weak10_1", "weak10_2", "weak10_3", "weak5_1", "weak5_2", "weak5_3"));
    }

    @Test
    public void testUnprofitableCategories() throws Exception {
        when(propertiesService.list(any()))
                .thenReturn(Map.of(
                        "unprofitable.black.list.test.10", "weak10_1,weak10_2,weak10_3",
                        "unprofitable.black.list.test.5", "weak5_1,weak5_2,weak5_3"));
        assertThat(registry.getUnprofitableCategories(of("weak5_1", "weak5_2", "weak5_3")),
                is(of("weak5_1", "weak5_2", "weak5_3")));
        assertThat(registry.getUnprofitableCategories(of("weak5_1", "weak5_2-XXX", "weak10_1")),
                is(of("weak5_1", "weak10_1")));
    }

    @DatabaseComponent
    private static class TestUnprofitableCategoriesRegistry extends AbstractUnprofitableCategoriesRegistry {
        public TestUnprofitableCategoriesRegistry() {
            super("test", Duration.ofDays(30), Optional.of(Side.LAY), 0, Duration.ofHours(2),
                    "weak", Map.of(5, 2, 2, 7));
        }
    }

    @DatabaseComponent
    private static class Test2UnprofitableCategoriesRegistry extends AbstractUnprofitableCategoriesRegistry {
        public Test2UnprofitableCategoriesRegistry() {
            super("test2", Duration.ofDays(30), Optional.of(Side.LAY), 0, Duration.ofHours(2),
                    "weak", Map.of(10, 1));
        }
    }

}
