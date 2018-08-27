package com.artemis.kahn.controllers;


import com.artemis.kahn.dao.pojo.Product;
import com.artemis.kahn.batch.Hello;
import com.artemis.kahn.batch.Hello2;
import com.artemis.kahn.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping(value = "/index")
    @ResponseBody
    public void index() {
        Hello hello = new Hello();
        hello.say("xiaoyu");

        Hello2 hello2 = new Hello2();
        hello2.say2("xiaoyu22222");
    }

    @RequestMapping(value = "/list")
    public void list(ModelMap modelMap, String name) {
        List<Product> list = productService.findProducts();
        System.out.println(list.size());
        modelMap.addAttribute("products", list);
    }


}
