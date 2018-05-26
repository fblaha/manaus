package cz.fb.manaus.core.manager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fb.manaus.core.model.Market;
import cz.fb.manaus.core.test.AbstractLocalTestCase;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

abstract public class AbstractMarketDataAwareTestCase extends AbstractLocalTestCase {
    public static final TypeReference<List<Market>> TYPE_REF = new TypeReference<>() {
    };
    protected List<Market> markets;
    @Autowired
    private ResourceLoader resourceLoader;

    @Before
    public void setUp() throws Exception {
        var resource = resourceLoader.getResource("classpath:cz/fb/manaus/core/service/markets.zip");
        var zipFile = new ZipFile(resource.getFile());
        var zipEntry = zipFile.getEntry("markets.json");
        markets = new ObjectMapper().readValue(zipFile.getInputStream(zipEntry), TYPE_REF);
        markets.forEach(market -> market.getEvent().setOpenDate(DateUtils.addHours(new Date(), 5)));
    }

}
