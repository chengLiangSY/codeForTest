package com.cl.data.chain;

import com.google.common.collect.ComparisonChain;
import org.junit.Test;

/**
 * Created by 亮 on 2017/12/7.
 */
public class OrderComparisonChainTest {

    @Test
    public void orderCompTest(){
        boolean[] ret = OrderComparisonChain.start()
                            .compare(123, 2)
                            .compare(123, 123)
                            .compare(12.4, 12.4)
                            .compare(12L, 23L)
                            .compare("hello", "hello1")
                            .result();
        for (int i = 0; i < ret.length ; i++) {
            System.out.println("i diff --> " + ret[i]);
        }

        for (int i = 0; i < 3; i++) {
            ComparisonChain occ = ComparisonChain.start();
            System.out.println(occ);
        }


    }
}
