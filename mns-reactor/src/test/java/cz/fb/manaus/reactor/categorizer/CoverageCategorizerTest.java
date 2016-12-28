package cz.fb.manaus.reactor.categorizer;

import cz.fb.manaus.core.category.BetCoverage;
import cz.fb.manaus.core.model.SettledBet;
import cz.fb.manaus.core.model.Side;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import cz.fb.manaus.core.test.CoreTestFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.junit.Assert.assertThat;

public class CoverageCategorizerTest extends AbstractLocalTestCase {

    @Autowired
    private CoverageCategorizer categorizer;

    @Test
    public void testCategoryFromSides() throws Exception {
        assertThat(categorizer.getCategories(Side.BACK, singletonMap(Side.BACK, 2d)), hasItems("coverage_soloBack", "coverage_solo"));
        assertThat(categorizer.getCategories(Side.LAY, singletonMap(Side.LAY, 2d)), hasItems("coverage_soloLay", "coverage_solo"));
        assertThat(categorizer.getCategories(Side.BACK, of(Side.LAY, 2d, Side.BACK, 2d)), hasItem("coverage_both"));
    }

    @Test
    public void testCategoryBothEquality() throws Exception {
        assertThat(categorizer.getCategories(Side.BACK, of(Side.LAY, 2d, Side.BACK, 2d)), hasItem("coverage_bothEqual"));
        assertThat(categorizer.getCategories(Side.BACK, of(Side.LAY, 3d, Side.BACK, 2d)), hasItem("coverage_bothLayGt"));
        assertThat(categorizer.getCategories(Side.BACK, of(Side.LAY, 3d, Side.BACK, 4d)), hasItem("coverage_bothBackGt"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCategoryNoSide() throws Exception {
        categorizer.getCategories(Side.BACK, Collections.emptyMap());
    }

    @Test(expected = IllegalStateException.class)
    public void testCategoryNoMySide() throws Exception {
        categorizer.getCategories(Side.BACK, Collections.singletonMap(Side.LAY, 2d));
    }

    @Test
    public void testCategory() throws Exception {
        SettledBet bet = CoreTestFactory.newSettledBet(2d, Side.LAY);
        BetCoverage coverage = BetCoverage.from(Collections.singletonList(bet));
        assertThat(categorizer.getCategories(bet, coverage), hasItems("coverage_soloLay", "coverage_solo"));
    }

}