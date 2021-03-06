package cc.joyreactor;

import cc.joyreactor.data.Image;
import cc.joyreactor.data.Post;
import cc.joyreactor.data.Tag;
import cc.joyreactor.data.User;
import ch.caro62.services.WebClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.*;

public class PostListParser {

    private final static DateTimeFormatter LOCAL_TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public static void parsePage(Document doc) {
        //doc.select("a.next").forEach(next -> urlMap.putIfAbsent(tagRef.getKey(), next.attr("abs:href")));

        //doc.select("div.postContainer").stream().map(this::parsePost)
        //.forEachOrdered(this::update);
    }

    public static String getPageNum(String value) {
        String[] parts = value.split("/");
        if (parts.length > 0) {
            try {
                return parts[parts.length - 1];
            } catch (Exception e) {
                return "---";
            }
        }
        return "---";
    }

    public static int getThreadNum(String name) {
        String[] parts = name.split("-");
        if (parts.length > 0) {
            try {
                return Integer.parseInt(parts[parts.length - 1]);
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static Post parsePost(Element post) {
        Post postItem = new Post();
        postItem.setId(parsePostId(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getId());
        postItem.setUser(parseUser(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getUser().getName());
        postItem.setTags(parseTags(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getTags());
        postItem.setImages(parseImages(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getImages());
        postItem.setComments(parseComments(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getComments());
        postItem.setRating(parseRating(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getRating());
        postItem.setPublished(parsePublished(post));
        System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + postItem.getPublished());
        //System.out.println(postItem.getId() + " " + postItem.getTags());
        //System.out.println("\t" + postItem.getUser().getName() + " " + postItem.getPublished());
        //System.out.println("\t" + postItem.getImages().size() + ", " + postItem.getComments() + ", " + postItem.getRating());
        return postItem;
    }

    public static List<Image> parseImages(Element post) {
        return post.select("div.post_content div.image img[src]").stream().map(tag -> new Image(
                Integer.parseInt("0" + tag.attr("width")),
                Integer.parseInt("0" + tag.attr("height")),
                tag.attr("abs:src")
        )).collect(Collectors.toList());
    }

    public static User parseUser(Element post) {
        return post.select("div.uhead_nick").stream()
                .flatMap(user -> user.select(".avatar").stream()
                        .map(av -> {
                            byte[] avatar = WebClient.getBytesSync(av.attr("abs:src")).get();
                            User u = new User(0, user.text(), avatar);
                            return u;
                        })
                ).findFirst().orElse(null);
    }

    public static ZonedDateTime parsePublished(Element post) {
        return post.select("span.date span[data-time]").stream()
                .map(user -> Instant.ofEpochMilli(1000 * Long.parseLong(user.attr("data-time")))
                        .atZone(ZoneId.of("Europe/Moscow"))).findFirst().orElse(null);
    }

    public static BigDecimal parseRating(Element post) {
        return post.select(".ufoot .post_rating").stream().map(Element::text)
                .map(BigDecimal::new).findFirst().orElse(new BigDecimal(0.0d));
    }

    public static Integer parsePostId(Element post) {
        return post.select(".ufoot .link_wr a.link").stream()
                .map(user -> user.attr("href"))
                .map(id -> id.replace("/post/", ""))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    public static Integer parseComments(Element post) {
        return post.select(".commentnum").stream()
                .map(Element::text)
                .map(text -> text.replace("Комментарии ", "0"))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    public static List<Tag> parseTags(Element post) {
        List<Tag> tags = new ArrayList<>();
        post.select(".taglist a[title]").forEach(tagItem -> {
            String idString = tagItem.attr("data-ids").split(",")[0];
            Tag tag = new Tag(0,
                    tagItem.attr("title"),
                    tagItem.attr("abs:href"),
                    tagItem.attr("data-ids"));
            tags.add(tag);
        });
        return tags;
    }

}
