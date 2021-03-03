package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.Identifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractArrayListDao<T extends Identifiable<Long>> {
    protected AtomicLong maxId = new AtomicLong(0);
    protected List<T> items = new CopyOnWriteArrayList<>();

    public T getItem(@Nullable Long id) throws ItemNotFoundException, NullValuePassedException {
        if (id == null) {
            throw new NullValuePassedException();
        }

        return items.stream()
                .filter(item -> id.equals(item.getId()))
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    public void save(@Nullable T item) throws NullValuePassedException {
        if (item == null) {
            throw new NullValuePassedException();
        }
        if (item.getId() != null) {
            update(item);
        } else {
            add(item);
        }
    }

    private void update(T item) {
        Long id = item.getId();
        Optional<T> optionalItem = items.stream()
                .filter(i -> id.equals(i.getId()))
                .findAny();
        if (!optionalItem.isPresent()) {
            items.add(item);
        } else {
            Collections.replaceAll(items, optionalItem.get(), item);
        }
    }

    private void add(T item) {
        item.setId(maxId.incrementAndGet());
        items.add(item);
    }
}
