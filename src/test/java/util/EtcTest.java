package util;

import com.github.f4b6a3.ulid.UlidCreator;
import org.junit.jupiter.api.Test;

public class EtcTest {

    @Test
    void uuidTest(){
        System.out.println(UlidCreator.getMonotonicUlid().toUuid());
    }
}
