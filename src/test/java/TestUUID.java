import com.github.softbasic.micro.utils.UUIDUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUUID {
    private static Logger log = LoggerFactory.getLogger(TestUUID.class);

    @Test
    public void test(){
        log.info(UUIDUtils.create());
    }

    @Test
    public void test1(){

        String fileName = "xx.xlsx";
        String fileExtName=fileName.substring(fileName.lastIndexOf("."));
        System.out.println(fileExtName);
    }
}
