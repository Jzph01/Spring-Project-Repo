package PuppyBlog.entity;

import javax.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    private Integer id;
    private String comment;
    private Article article;
    private User author;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "articleId")
    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Comment(String comment, Article article, User author) {
        this.comment = comment;
        this.article = article;
        this.author = author;
    }

    public Comment() {
    }


    @ManyToOne()
    @JoinColumn(nullable = false, name = "author")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
