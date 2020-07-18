package nonInject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Test1 {
    @Autowired
            @Qualifier("test22")
    Test2 test2;
    @Autowired
    Test3 test3;
}
