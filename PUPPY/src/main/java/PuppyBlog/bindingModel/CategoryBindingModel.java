package PuppyBlog.bindingModel;


import javax.validation.constraints.NotNull;

public class CategoryBindingModel {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
