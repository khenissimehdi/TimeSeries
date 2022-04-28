package core.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public class TimeSeries<T> {
    public final record Data<T>(long timestamp, T element){
        public Data {
            Objects.requireNonNull(element);
        }
    }
    private final ArrayList<Data<T>> list = new ArrayList<>();
    private int size;

    public final class Index {
        private int size;
        private final int[] indexs;

        private Index(int[] t, int size) {
            this.indexs  = t;
            this.size = size;
        }

        /**
         * get the table that contains the indexes
         * */

        public int[] indexes() {
            return indexs;
        }

        /**
         * returns how many elements the Index got
         * */
        public int size() {
            return size;
        }


        @Override
        public String toString() {
            if(size == 0) {
                return "";
            }
            var sb = new StringBuilder();
            for (var i: indexs) {
                sb.append(list.get(i).timestamp).append(" | ").append(list.get(i).element.toString()).append("\n");
            }
            return sb.substring(0, sb.length()-1);
        }
    }


    /**
     * creates an Index containing the indexes of the time series
     * */
    public Index index() {
        var l = IntStream.range(0, size).toArray();
        return new Index(l,size);
    }

    /**
     * add an element to the time series
     * @param timestamp long
     * @param element T
     * @throws IllegalStateException when the added element is smaller than the last added value
     * @throws NullPointerException if element is null
     * */
    public void add(long timestamp,T element) {
        Objects.requireNonNull(element);
        if(list.isEmpty()) {
            size++;
            list.add(new Data<>(timestamp, element));
        } else {
           if(list.get(list.size()-1).timestamp > timestamp) {
               throw new IllegalStateException("the timestamp have to bigger than the last added value");
           }
           size++;
           list.add(new Data<>(timestamp, element));
        }
    }

    /**
     * returns the number of added element
     * */
    public int size() {
        return size;
    }

    /**
     * returns the element at the position in the given index
     * @param index int
     * */
    public Data<T> get(int index) {
        return list.get(index);
    }
}
