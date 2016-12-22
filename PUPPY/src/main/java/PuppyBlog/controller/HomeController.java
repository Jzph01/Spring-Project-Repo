package PuppyBlog.controller;

import PuppyBlog.entity.Article;
import PuppyBlog.entity.Category;
import PuppyBlog.entity.Product;
import PuppyBlog.repository.ArticleRepository;
import PuppyBlog.repository.CategoryRepository;
import PuppyBlog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String index(Model model){

        List<Article> articles = this.articleRepository.findAll();

        articles = articles.stream()
                .sorted(Comparator.comparingInt(Article::getId).reversed())
                .collect(Collectors.toList());

        model.addAttribute("articles", articles);
        model.addAttribute("view", "home/index");


        return  "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model){
        model.addAttribute("view", "error/403");
        return "base-layout";
    }

    @RequestMapping("/contact")
    public String contact(Model model){
        model.addAttribute("view", "contact/contactInfo");
        return "base-layout";
    }

    @GetMapping("/shop")
    public String shop(Model model){

        List<Category> categories = this.categoryRepository.findAll();
        categories = categories.stream()
                .sorted(Comparator.comparingInt(Category::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        model.addAttribute("view", "shop/shopIndex");
        return "base-layout";
    }



    @GetMapping("/category/{id}")
    public String listProducts(Model model,@PathVariable Integer id){
        if(!this.categoryRepository.exists(id)){
            return "redirect:/shop/";
        }
        Category category = this.categoryRepository.findOne(id);


        Set<Product> products = category.getProducts();




        model.addAttribute("category", category);
        model.addAttribute("products", products);
        model.addAttribute("view", "shop/list-products");

        return "base-layout";
    }

}

