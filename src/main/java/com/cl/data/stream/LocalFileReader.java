package com.cl.data.stream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by 亮 on 2017/12/14.
 * 本地文件读取器，构建本地文件读取，实现ReaderStream接口
 */
public class LocalFileReader implements ReaderStream<List<String>> {

    private BufferedReader br = null;

    private String srcPath = null;

    private int orderRange = 0;

    public LocalFileReader(String srcPath, int orderRange){
        this.srcPath = srcPath;
        this.orderRange = orderRange;
    }

    public LocalFileReader(String srcPath) {
        this.srcPath = srcPath;
    }

    /**
     * 初始化本地读取器，按照指定文件路径
     */
    @Override
    public void initReaderStream() {
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcPath)));
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    public List<String> readCfgFile(){
        List<String> lines = new ArrayList<String>();;
        String line = null;
        try {
            initReaderStream();
            while ((line = br.readLine()) != null){
                lines.add(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * 读取本地文件，返回订单数量上限为orderRange
     * @return
     */
    @Override
    public List<String> readFile() {
        String line = null;
        String[] orders = null;
        String[] finalOrders = null;
        String[] remainOrders = null;
        boolean isLoop = false;
        int index = 0;
        initReaderStream();
        try {
            orders = new String[orderRange];
            while ((line = br.readLine())!= null){
                orders[index++] = line;
                if(index >= orderRange){//订单在orderRange范围内循环写
                    index = index % orderRange;
                    isLoop = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            finalOrders = new String[index];
            if(isLoop){//isLoop=true:订单数组已满
                System.arraycopy(orders, 0, finalOrders, 0, finalOrders.length);
                System.arraycopy(orders, finalOrders.length, orders, 0, orders.length - finalOrders.length);
                System.arraycopy(finalOrders, 0, orders, orders.length - finalOrders.length, finalOrders.length);
            }else{//isLoop=false:订单数组未满
                System.arraycopy(orders, 0, finalOrders, 0, finalOrders.length);
                orders = finalOrders;
            }
        }


        return Arrays.asList(orders);
    }
}
