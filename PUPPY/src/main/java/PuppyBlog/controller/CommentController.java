package PuppyBlog.controller;

import PuppyBlog.bindingModel.CommentBindingModel;
import PuppyBlog.bindingModel.ProductBindingModel;
import PuppyBlog.entity.*;
import PuppyBlog.repository.ArticleRepository;
import PuppyBlog.repository.CommentRepository;
import PuppyBlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;




    @PostMapping("/comment/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(CommentBindingModel commentBindingModel, @PathVariable Integer id){
        Article article = this.articleRepository.findOne(id);

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        Comment comment = new Comment(commentBindingModel.getComment(),article,userEntity
                );
        this.commentRepository.saveAndFlush(comment);
        return "redirect:/article/"+article.getId();
    }

    @GetMapping("/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(!isUserAuthorOrAdmin(comment)){
            return "redirect:/article/" + comment.getArticle().getId();
        }
        model.addAttribute("comment", comment);
        model.addAttribute("view", "comment/edit");
        return  "base-layout";
    }

    @PostMapping("/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, CommentBindingModel commentBindingModel){
        if(!this.commentRepository.exists(id)){
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(!isUserAuthorOrAdmin(comment)){
            return "redirect:/article/" + comment.getArticle().getId();
        }

        comment.setComment(commentBindingModel.getComment());
        this.commentRepository.saveAndFlush(comment);
        return "redirect:/article/"+comment.getArticle().getId();
    }

    @GetMapping("/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Integer id, Model model){
        if(!this.commentRepository.exists(id)){
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(!isUserAuthorOrAdmin(comment)){
            return "redirect:/article/" + comment.getArticle().getId();
        }

        model.addAttribute("comment", comment);
        model.addAttribute("view", "comment/delete");
        return "base-layout";
    }

    @PostMapping("/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id){
        if(!this.commentRepository.exists(id)){
            return "redirect:/";
        }
        Comment comment = this.commentRepository.findOne(id);
        if(!isUserAuthorOrAdmin(comment)){
            return "redirect:/article/" + comment.getArticle().getId();
        }

        this.commentRepository.delete(comment);
        return "redirect:/";
    }



    private boolean isUserAuthorOrAdmin(Comment comment){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        User userEntity = this.userRepository.findByEmail(user.getUsername());
        return userEntity.isAdmin() || userEntity.isAuthorOfTheComment(comment);
    }
}
