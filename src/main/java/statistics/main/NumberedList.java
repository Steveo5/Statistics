package statistics.main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NumberedList<E> {

    class NumberedItem {

        private long number;
        private E element;

        public NumberedItem(long number, E element) {
            this.number = number;
            this.element = element;
        }

        public E getElement() {
            return element;
        }

        public long getNumber() {
            return number;
        }

    }

    private List<NumberedItem> list;

    public NumberedList() {
        list = new ArrayList<>();
    }

    /**
     * Add an element to the list
     * @param number not a key but the number to make sums, etc
     * @param element
     */
    public void add(long number, E element) {
        list.add(new NumberedItem(number, element));
        list.sort(Comparator.comparing(NumberedItem::getNumber));
    }

    /**
     * Sum all the numbers in the list
     * @return sum
     */
    public int sum() {
        int total = 0;
        for(NumberedItem numberedItem : list) total+= numberedItem.number;
        return total;
    }

    public int average() {
        return sum() / list.size();
    }

    /**
     * Get the element with the highest number
     * @return E
     */
    public E getMax() {
        return list.get(list.size() - 1).element;
    }

    public E getMin() {
        return list.get(0).element;
    }

}
