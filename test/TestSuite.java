import com.hetang.cache.impl.ImageCacheTest;
import com.hetang.cache.policy.LRUCacheTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by hetashah on 6/25/16.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ImageCacheTest.class, LRUCacheTest.class})
public class TestSuite {
}
