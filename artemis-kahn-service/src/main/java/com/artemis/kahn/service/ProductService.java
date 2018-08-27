package com.artemis.kahn.service;



import com.artemis.kahn.dao.pojo.Product;
import java.util.List;

public interface ProductService {

    List<Product> findProducts();
}
