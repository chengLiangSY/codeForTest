package com.cl.data.chain;

import com.google.common.primitives.*;

/**
 * Created by 亮 on 2017/12/7.
 * 订单差异比较器
 * 差异集大小默认：128
 */
public abstract class OrderComparisonChain {

    private OrderComparisonChain(){}

    public static OrderComparisonChain start(){
        return ACTIVE;
    }

    private static final OrderComparisonChain ACTIVE = new InactiveComparisonChain();

    private static final class InactiveComparisonChain extends  OrderComparisonChain{

        //差异集
        private boolean[] diffs = new boolean[128];

        //无差异标识
        private final byte[] NONE_FLAG = Ints.toByteArray(0);

        private int index = 0;

        @Override
        public OrderComparisonChain initDiffs(int diffsSize){
            this.diffs = new boolean[diffsSize];
            return this;
        }

        @Override
        public OrderComparisonChain compare(int left, int right) {
            return classify(Ints.compare(left, right));
        }

        @Override
        public OrderComparisonChain compare(long left, long right) {
            return classify(Longs.compare(left, right));
        }


        @Override
        public OrderComparisonChain compare(double left, double right) {
            return classify(Doubles.compare(left, right));
        }

        @Override
        public OrderComparisonChain compare(float left, float right) {
            return classify(Floats.compare(left, right));
        }

        @Override
        public OrderComparisonChain compare(byte[] left, byte[] right) {
            return classify(SignedBytes.lexicographicalComparator().compare(left, right));
        }

        @Override
        public OrderComparisonChain compare(String left, String right) {
            return classify(left.equals(right) == true ? 0 : 1);
        }

        private OrderComparisonChain classify(int result){
            if (result == 0){
                diffs[index++] = false;
            }else{
                diffs[index++] = true;
            }
            return this;
        }
        @Override
        public boolean[] result() {
            boolean [] _diffs = new boolean[index];
            System.arraycopy(diffs, 0, _diffs, 0, index);
            this.index = 0;
            return _diffs;
        }
    }


    public abstract OrderComparisonChain compare(int left, int right);

    public abstract OrderComparisonChain compare(long left, long right);

    public abstract OrderComparisonChain compare(double left, double right);

    public abstract OrderComparisonChain compare(float left, float right);

    public abstract OrderComparisonChain compare(byte[] left, byte[] right);

    public abstract OrderComparisonChain compare(String left, String right);

    public abstract OrderComparisonChain initDiffs(int diffsSize);

    public abstract boolean[] result();
}
