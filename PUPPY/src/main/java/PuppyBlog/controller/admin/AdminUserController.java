package PuppyBlog.controller.admin;

import PuppyBlog.bindingModel.UserEditBindingModel;
import PuppyBlog.entity.Article;
import PuppyBlog.entity.Role;
import PuppyBlog.entity.User;
import PuppyBlog.repository.ArticleRepository;
import PuppyBlog.repository.RoleRepository;
import PuppyBlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String listUsers(Model model){
        List<User> users = this.userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("view", "admin/user/list");

        return "base-layout";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Integer id){
        if(!this.userRepository.exists(id)){
            return "redirect:/admin/users/";
        }
        User user = this.userRepository.findOne(id);
        List<Role> roles = this.roleRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("view", "admin/user/edit");
        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id, UserEditBindingModel userEditBindingModel){
        if(!this.userRepository.exists(id)){
            return "redirect:/admin/users/";
        }

        User user = this.userRepository.findOne(id);

        if(!StringUtils.isEmpty(userEditBindingModel.getPassword())
                && !StringUtils.isEmpty(userEditBindingModel.getConfirmPassword())){

            if(userEditBindingModel.getPassword().equals(userEditBindingModel.getConfirmPassword())){
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                user.setPassword(bCryptPasswordEncoder.encode(userEditBindingModel.getPassword()));
            }
        }
        user.setEmail(userEditBindingModel.getEmail());
        user.setFullName(userEditBindingModel.getFullName());

        Set<Role> roles =new HashSet<>();

        for(Integer roleId : userEditBindingModel.getRoles()){
            roles.add(this.roleRepository.findOne(roleId));
        }
        user.setRoles(roles);

        this.userRepository.saveAndFlush(user);

        return "redirect:/admin/users/";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Integer id){
        if(!this.userRepository.exists(id)){
            return "redirect:/admin/users/";
        }
        User user = this.userRepository.findOne(id);
        model.addAttribute("user", user);
        model.addAttribute("view", "admin/user/delete");
        return "base-layout";
    }
    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id){
        if(!this.userRepository.exists(id)){
            return "redirect:/admin/users/";
        }
        User user = this.userRepository.findOne(id);

        for(Article article : user.getArticles()){
            this.articleRepository.delete(article);
        }

        userRepository.delete(user);
        return "redirect:/admin/users/";
    }
}
