package cc.joyreactor;

import cc.joyreactor.data.Image;
import cc.joyreactor.data.Post;
import cc.joyreactor.data.Tag;
import cc.joyreactor.data.User;
import cc.joyreactor.models.UpdateStats;
import ch.caro62.services.WebClient;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jsoup.nodes.Element;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoField.*;

public class Updater extends SwingWorker<UpdateStats, String> {

    public static final int THREAD_COUNT = 16;

    private final static DateTimeFormatter LOCAL_TIME = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_COUNT);
    private final ConcurrentSkipListMap<String, String> urlMap = new ConcurrentSkipListMap<>();
    private final Source source;
    private final UpdateStats stats;

    public Updater(UpdateStats stats) throws SQLException {
        this.stats = stats;
        source = Source.getInstance();
        urlMap.put("JoyReactor", "http://joyreactor.cc/best");
        urlMap.put("Pleasure Room", "http://pr.reactor.cc/new");
        urlMap.put("Anime", "http://anime.reactor.cc/new");
        //urlMap.put("Эротика", "http://joyreactor.cc/tag/Эротика/new");
        List<Tag> tags = source.getTags();
        Collections.shuffle(tags, ThreadLocalRandom.current());

        tags.stream().limit(THREAD_COUNT).forEachOrdered(tag -> {
            if (tag.getRef().endsWith("/")) {
                urlMap.put(tag.getTag(), tag.getRef() + "new");
            } else {
                urlMap.put(tag.getTag(), tag.getRef() + "/new");
            }
        });
    }

    @Override
    protected UpdateStats doInBackground() {
        IntStream.range(0, THREAD_COUNT).forEach(i -> scheduler.scheduleWithFixedDelay(this::parsePage,
                THREAD_COUNT * i + THREAD_COUNT,
                ThreadLocalRandom.current().nextInt(8, 16),
                TimeUnit.SECONDS));
        return stats;
    }

    private void parsePage() {
        Map.Entry<String, String> tagRef;
        if (ThreadLocalRandom.current().nextBoolean()) {
            tagRef = urlMap.pollLastEntry();
        } else {
            tagRef = urlMap.pollFirstEntry();
        }
        WebClient.getDocSync(tagRef.getValue()).ifPresent((doc) -> {
            System.out.println("\t\t\t[" + Thread.currentThread().getName() + "]  NEXT '" + tagRef.getKey() + "' : " + tagRef);
            doc.select("a.next").forEach(next -> System.out.println("\t\t\tNEXT: " + next.attr("abs:href")));
            doc.select("a.next").forEach(next -> urlMap.putIfAbsent(tagRef.getKey(), next.attr("abs:href")));

            doc.select("div.postContainer").stream().map(this::parsePost)
                    .forEachOrdered(this::update);
        });
    }

    private void update(Post post) {
        Post dbPost = source.getPost(post.getId());
        if (dbPost == null || dbPost.getUser() == null) {
            System.out.println("\t\t\tNEW POSTS: " + stats.incPosts());
            System.out.println("\t\t\tNEW COMMENTS: " + stats.addComments(post.getComments()));
            System.out.println("\t\t\tNEW RATING: " + stats.addRating(post.getRating()));
            source.insertPost(post);
        } else {
            System.out.println("\t\t\tNEW COMMENTS: " + stats.addComments(post.getComments() - dbPost.getComments()));
            System.out.println("\t\t\tNEW RATING: " + stats.addRating(post.getRating().subtract(dbPost.getRating())));
            source.updatePost(post);
        }
        if (source.getPostTags(post.getId()).size() == 0) {
            source.setPostTags(post.getId(), post.getTags());
        }
        if (source.getPostImages(post.getId()).size() == 0) {
            source.setPostImages(post.getId(), post.getImages());
        }
    }

    private Post parsePost(Element post) {
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

    private List<Image> parseImages(Element post) {
        return post.select("div.post_content div.image img[src]").stream().map(tag -> new Image(
                Integer.parseInt("0" + tag.attr("width")),
                Integer.parseInt("0" + tag.attr("height")),
                tag.attr("abs:src")
        )).collect(Collectors.toList());
    }

    private User parseUser(Element post) {
        return post.select("div.uhead_nick").stream()
                .flatMap(user -> user.select(".avatar").stream()
                        .map(av -> {
                            System.out.println(av.attr("abs:src"));
                            User dbUser = source.getUser(user.text());
                            if (dbUser == null) {
                                System.out.println("\t\t\tNEW USERS: " + stats.incUsers());
                                byte[] avatar = WebClient.getBytesSync(av.attr("abs:src")).get();
                                User u = new User(0, user.text(), avatar);
                                source.insertUser(u);
                                return u;
                            } else {
                                return dbUser;
                            }
                        })
                ).findFirst().orElse(null);
    }

    private ZonedDateTime parsePublished(Element post) {
        return post.select("span.date span[data-time]").stream()
                .map(user -> Instant.ofEpochMilli(1000 * Long.parseLong(user.attr("data-time")))
                        .atZone(ZoneId.of("Europe/Moscow"))).findFirst().orElse(null);
    }

    private BigDecimal parseRating(Element post) {
        return post.select(".ufoot .post_rating").stream().map(Element::text)
                .map(BigDecimal::new).findFirst().orElse(new BigDecimal(0.0d));
    }

    private Integer parsePostId(Element post) {
        return post.select(".ufoot .link_wr a.link").stream()
                .map(user -> user.attr("href"))
                .map(id -> id.replace("/post/", ""))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    private Integer parseComments(Element post) {
        return post.select(".commentnum").stream()
                .map(Element::text)
                .map(text -> text.replace("Комментарии ", "0"))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    private List<Tag> parseTags(Element post) {
        List<Tag> tags = new ArrayList<>();
        post.select(".taglist a[title]").forEach(tag -> {
            String idString = tag.attr("data-ids").split(",")[0];
            Tag dbTag = source.getTag(tag.attr("title"));
            if (dbTag == null) {
                source.insertTag(new Tag(0,
                        tag.attr("title"),
                        tag.attr("abs:href"),
                        tag.attr("data-ids")));
                tags.add(source.getTag(tag.attr("title")));
            } else {
                tags.add(dbTag);
            }
        });

        return tags;
    }

}
