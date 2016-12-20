package PuppyBlog.controller;

import PuppyBlog.bindingModel.ProductBindingModel;
import PuppyBlog.entity.Category;
import PuppyBlog.entity.Product;
import PuppyBlog.entity.User;
import PuppyBlog.repository.CategoryRepository;
import PuppyBlog.repository.ProductRepository;
import PuppyBlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/product/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        List<Category> categories = this.categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("view", "product/create");
        return "base-layout";
    }
    @PostMapping("/product/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ProductBindingModel productBindingModel){
        Category category = this.categoryRepository.findOne(productBindingModel.getCategoryId());

        Product product = new Product(productBindingModel.getName(),
                productBindingModel.getPrice(),
                productBindingModel.getDescription(),
                category);
        this.productRepository.saveAndFlush(product);
        return "redirect:/shop";
    }

    @GetMapping("/product/{id}")
    public String details(@PathVariable Integer id,Model model){
        if(!this.productRepository.exists(id)){
            return "redirect:/shop";
        }
        if(!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)){

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            User entityUser = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", entityUser);
        }
        Product product = this.productRepository.findOne(id);
        Category category = product.getCategory();
        model.addAttribute("category", category);
        model.addAttribute("product", product);
        model.addAttribute("view", "product/details");
        return "base-layout";
    }

    @GetMapping("/product/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id,Model model){
        if(!this.productRepository.exists(id)){
            return "redirect:/shop";
        }
        Product product = this.productRepository.findOne(id);

        if(!isUserAdmin(product)){
            return "redirect:/product/"+id;
        }

        List<Category> categories = this.categoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("product", product);
        model.addAttribute("view", "product/edit");
        return "base-layout";
    }
    @PostMapping("/product/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(ProductBindingModel productBindingModel,
                              @PathVariable Integer id){
        if(!this.productRepository.exists(id)){
            return "redirect:/shop";
        }
        Product product = this.productRepository.findOne(id);

        if(!isUserAdmin(product)){
            return "redirect:/product/"+id;
        }

        Category category = this.categoryRepository.findOne(productBindingModel.getCategoryId());


        product.setCategory(category);
        product.setName(productBindingModel.getName());
        product.setPrice(productBindingModel.getPrice());
        product.setDescription(productBindingModel.getDescription());

        this.productRepository.saveAndFlush(product);
        return "redirect:/product/"+product.getId();
    }

    @GetMapping("/product/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Integer id,Model model){
        if(!this.productRepository.exists(id)){
            return "redirect:/shop";
        }
        Product product = this.productRepository.findOne(id);

        if(!isUserAdmin(product)){
            return "redirect:/product/"+id;
        }

        model.addAttribute("product", product);
        model.addAttribute("view", "product/delete");
        return  "base-layout";
    }
    @PostMapping("/product/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id){
        if(!this.productRepository.exists(id)){
            return "redirect:/shop";
        }
        Product product = this.productRepository.findOne(id);

        if(!isUserAdmin(product)){
            return "redirect:/product/"+id;
        }

        this.productRepository.delete(product);
        return "redirect:/shop";
    }

    private boolean isUserAdmin(Product product){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());
        return userEntity.isAdmin();
    }
}
