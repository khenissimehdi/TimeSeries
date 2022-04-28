package core.classes;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;


public class TimeSeries<T> {
    public final record Data<T>(long timestamp, T element){
        public Data {
            Objects.requireNonNull(element);
        }

        @Override
        public String toString() {
            return timestamp + " | " + element;
        }
    }
    private final ArrayList<Data<T>> list = new ArrayList<>();
    private int size;

    public final class Index implements Iterable<Data<T>> {
        private final int size;
        private final int[] indexes;

        private Index(int[] t) {
            this.indexes  = t;
            this.size = t.length;
        }

        /**
         * get the table that contains the indexes
         * @return int[]
         * */
        public int[] indexes() {
            return indexes;
        }

        /**
         * returns how many elements the Index got
         * @return int
         * */
        public int size() {
            return size;
        }

        /**
         * method that return and Index containing the indexes of the current Index
         * two indexes plus the indexes of another one passed as a param
         * @param index Index
         * @return Index
         * */
        public Index or(Index index) {
            Objects.requireNonNull(index);
             if(motherClassHashCode() != index.motherClassHashCode()) {
                 throw new IllegalArgumentException("The Index have to from the same mother class as the current index");

            }


           //  var concat = Stream.of(index.indexes, indexes).flatMapToInt(Arrays::stream).toArray();

             return new Index(IntStream.concat(IntStream.of(indexes), IntStream.of(index.indexes)).distinct().sorted().toArray());
        }

        /**
         * methods that return the hashCode of the mother class of the inner class
         * @return int hashCode
         * */
        public int motherClassHashCode() {
            return TimeSeries.this.hashCode();
        }

        @Override
        public Iterator<Data<T>> iterator() {
            return new Iterator<>() {
                private final int localSize = size;
                private int index;
                @Override
                public boolean hasNext() {
                    return index != localSize;
                }

                @Override
                public Data<T> next() {
                    if(!hasNext()) {
                        throw new NoSuchElementException("the iterator reached the end");
                    }
                    return list.get(indexes[index++]);
                }
            };
        }

        /**
         * foreach methods that will apply to each
         * Data object from 0 to size-1 of the List
         * in the Index Object
         * @param consumer Consumer<? super T>
         * */

        public void forEach(Consumer<? super Data<T>> consumer) {
            /*
             we use Consumer<? super T> because according, the pecs rule we
             (Producer Extends Consumer Super) a consumer have to be super
            * */
            IntStream.range(0, size).forEach(i-> consumer.accept(list.get(i)));
        }


        @Override
        public String toString() {
            if (size() == 0) {
                return "";
            }
            var sb = new StringBuilder();
            var itor =  Arrays.stream(indexes).iterator() ;
            while (itor.hasNext()){
                sb.append(list.get(itor.next())).append("\n");
            }
            return sb.substring(0,sb.length()-1);
        }
    }


    /**
     * returns an Index containing the indexes of the time series
     * @return Index
     * */
    public Index index() {

        return new Index(IntStream.range(0, size).toArray());
    }

    /**
     * returns an Index Created using the predicated passed as a param
     * @param predicate Predicate
     * @return Index
     * */
    public Index index(Predicate<? super T> predicate) {
        /* we use <? super T> because we are going to consume a predicate, (Producer Extends Consumer Super)
        and we want these methods to work with every predecessor of T */
        Objects.requireNonNull(predicate);
        //  don't use this code or your index won't support long operations ( unit test 8 indexOrALot )
        //  return new Index(list.stream().filter(e -> predicate.test(e.element)).toList().stream().mapToInt(list::indexOf).toArray());
        return  new Index(IntStream.range(0,list.size()).filter(e -> predicate.test(list.get(e).element)).toArray());

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
     * @return int
     * */
    public int size() {
        return size;
    }

    /**
     * returns the element at the position in the given index
     * @param index int
     * @return Data<T>
     * */
    public Data<T> get(int index) {
        return list.get(index);
    }
}
