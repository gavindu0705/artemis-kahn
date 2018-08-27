package com.artemis.kahn.service;

import com.artemis.kahn.dao.mapper.ProductMapper;
import com.artemis.kahn.dao.pojo.Product;
import com.artemis.kahn.dao.pojo.ProductExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    public List<Product> findProducts() {
        return productMapper.selectByExample(new ProductExample());
    }
}
