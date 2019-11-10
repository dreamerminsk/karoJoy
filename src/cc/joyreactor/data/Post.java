package cc.joyreactor.data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Post {

    private int id;

    private String tag;

    private List<Tag> tags;

    private Integer comments;

    private BigDecimal rating;

    private ZonedDateTime published;

    private User user;

    private List<Image> images;

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void setTags(List<Tag> tags) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        } else {
            this.tags.clear();
        }
        this.tags.addAll(tags);
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public BigDecimal getRating() {
        return new BigDecimal(rating.toString());
    }

    public void setRating(BigDecimal rating) {
        this.rating = new BigDecimal(rating.toString());
    }

    public ZonedDateTime getPublished() {
        return published;
    }

    public void setPublished(ZonedDateTime published) {
        this.published = published;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
