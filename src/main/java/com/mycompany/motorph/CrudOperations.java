package com.mycompany.motorph;

import java.io.IOException;
import java.util.List;

public interface CrudOperations<T, K> {
    T create(T item) throws IOException;
    T read(K id) throws IOException;
    T update(K id, T item) throws IOException;
    boolean delete(K id) throws IOException;
    List<T> list() throws IOException;
}
