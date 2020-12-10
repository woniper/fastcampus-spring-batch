
package fastcampus.spring.batch.part3;

import org.springframework.batch.item.ItemReader;

import java.util.List;

public class CustomItemReader<T> implements ItemReader<T> {

    private final List<T> items;

    public CustomItemReader(List<T> items) {
        this.items = items;
    }

    @Override
    public T read() {
        if (!items.isEmpty()) {
            return items.remove(0);
        }

        return null;
    }
}
