package com.cl.data.stream;

import com.cl.data.entry.DailyOrder;
import com.cl.data.entry.Diff;
import com.cl.data.util.DateUtils;
import com.google.common.primitives.SignedBytes;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.cl.data.stream.StreamForTest.Type.LONG;

/**
 * Created by 亮 on 2017/12/14.
 */
public class StreamForTest {

    @Test
    public void readerForTest(){
        URL fileUrl = getClass().getClassLoader().getResource("readerForTest.txt");
        String srcPath = fileUrl.getFile();

        LocalFileReader lfr = new LocalFileReader(srcPath, 10);
        lfr.initReaderStream();
        List<String> lines = lfr.readFile();
        System.out.println("lines num -> " + lines.size());
        for (String line : lines) {
            System.out.println("line content -> " + line);
        }
    }

    @Test
    public void readCfgForTest(){
        URL fileUrl = getClass().getClassLoader().getResource("diffFields.cfg");
        String srcPath = fileUrl.getFile();
        LocalFileReader lfr = new LocalFileReader(srcPath);
        System.out.println(lfr.readCfgFile());
    }

    enum Type{

        LONG(Long.class.getTypeName(), 1),
        STRING(String.class.getTypeName(), 2);

        private String type;
        private Integer index;

        Type(String type, int index){
            this.type = type;
            this.index = index;
        }
    }

    @Test
    public void fieldForTest() throws Exception{



        DailyOrder diff = new DailyOrder();
        Field f = diff.getClass().getDeclaredField("arrDate");
//        f.setAccessible(true);
        String type = f.getType().getTypeName();
        System.out.println(type);

        System.out.println(Long.class.getTypeName().equals(type));
        final String LONGTYPE = Long.class.getTypeName();
        switch (type){
            case "java.lang.Long":
                System.out.println(true);
        }

        System.out.println(long.class.getTypeName());
        System.out.println(Long.class.getTypeName());

        String test = "hello".getBytes("UTF-8") + "-12";
        String[] r = test.split("-");
        System.out.println(test);
        System.out.println(org.apache.commons.codec.binary.StringUtils.newStringUtf8(r[0].getBytes()));

        System.out.println("1233432323434".equals("1233432323434"));

        System.out.println(String.join("#", new String[]{"12","34","2"}));

    }

    @Test
    public void initFieldsForTest() throws Exception {
        long day1 = DateUtils.DateToLong("2017-10-01 00:00:00");
        long day1_1 = DateUtils.DateToLong("2017-10-03 00:00:00");
        DailyOrder do1 = new DailyOrder(1234, day1, "AOC").setArrDate(day1);
        DailyOrder do3 = new DailyOrder(1234, day1_1, "SDF").setArrDate(day1);

        URL cfgUrl = getClass().getClassLoader().getResource("diffFields.cfg");
        LocalFileReader lfr = new LocalFileReader(cfgUrl.getFile());
        List<String> fieldCfgs = lfr.readCfgFile();
        Object preField = null;
        Object curField = null;
        List result = null;
        String temp = null;
        try{
            result = new ArrayList<>();
            for(String field : fieldCfgs){
                temp = field.split(":")[0];
                Field f1 = do1.getClass().getDeclaredField(temp);
                f1.setAccessible(true);
                preField = f1.get(do1);
                Field f2 = do3.getClass().getDeclaredField(temp);
                f2.setAccessible(true);
                curField = f2.get(do3);

                result.add(field + ":" + preField + ":" + curField);
            }
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

        System.out.println(result);
    }
}
