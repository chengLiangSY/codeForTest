package com.cl.data.entry;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 亮 on 2017/12/11.
 */
public class Order<K, E extends Order.Element<K>>  {

    public static interface Element<K> extends  Comparable<K>{

        public K getKey();
    }

    /**
     * 点单状态
     */
    public static enum OrderState{
        RESV, IN, OUT, CNL, NS
    }

    /**
     * 基于二分查找，定位元素索引
     * @param elements 元素集合
     * @param key 元素查找key
     * @param <K> key类型
     * @param <E> 元素集合类型
     * @return
     */
    protected static <K, E extends Comparable<K>> int search(final List<E> elements, final K key){

        return elements == null ? -1 : Collections.binarySearch(elements, key);
    }

    private static <E> void remove(final List<E> elements, final int i, final E expected){
        final E removed = elements.remove(-i - 1);
        Preconditions.checkState(removed == expected, "removed != expected");
    }

    private List<E> elements = null;

    public List<E> getElements() {
        return elements;
    }

    protected Order(){}

    protected Order(final List<E> elements){
        this.elements = elements;
    }

    public int searchIndex(final List<E> elements, final K key){
        return search(elements, key);
    }

    /**
     * 根据订单营业日期、订单ID查询订单
     * @param key
     * @return
     */
    public E search(K key) {
        final int e = search(elements, key);
        return e < 0 ? null : elements.get(e);
    }

    /**
     * 集合中添加元素
     * @param elements
     * @param element
     * @param i
     */
    private void insert(final List<E> elements, final E element, final int i){
        if(elements == null){
            this.elements = new ArrayList<E>(10);
        }
        this.elements.add(-i - 1, element);
    }

    /**
     * 创建元素
     * @param element
     * @return
     */
    public int create(final E element){
        final int e = search(elements, element.getKey());
        insert(elements, element, e);
        return e;
    }









}
