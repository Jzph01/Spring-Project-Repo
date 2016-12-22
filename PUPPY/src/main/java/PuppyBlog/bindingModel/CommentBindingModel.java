package PuppyBlog.bindingModel;

import javax.validation.constraints.NotNull;

/**
 * Created by ysf on 20/12/2016.
 */
public class CommentBindingModel {
    @NotNull
    private String comment;




    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
