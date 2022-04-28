package core.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TimeSeries<T> {
    public final record Data<T>(long timestamp, T element){
        public Data {
            Objects.requireNonNull(element);
        }
    }

    private final ArrayList<Data<T>> list = new ArrayList<>();
    private int size;


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
     * return the number of added element
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
