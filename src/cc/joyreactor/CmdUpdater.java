package cc.joyreactor;

import cc.joyreactor.data.Image;
import cc.joyreactor.data.Post;
import cc.joyreactor.data.Tag;
import cc.joyreactor.data.User;
import cc.joyreactor.models.UpdateStats;
import ch.caro62.services.WebClient;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CmdUpdater {

    public static final int THREAD_COUNT = 1;
    private final static ConcurrentSkipListMap<Instant, String> urlMap = new ConcurrentSkipListMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_COUNT);
    private static final ThreadLocalRandom tlr = ThreadLocalRandom.current();
    private static Source source;
    private static UpdateStats stats;

    public CmdUpdater(UpdateStats stats) throws SQLException {
        CmdUpdater.stats = stats;
        source = Source.getInstance();
        urlMap.put(Instant.now().minusSeconds(10), "http://joyreactor.cc/tag/%D0%BA%D1%80%D0%B0%D1%81%D0%B8%D0%B2%D1%8B%D0%B5+%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(20), "http://joyreactor.cc/tag/%D0%B0%D1%80%D1%82+%D0%B1%D0%B0%D1%80%D1%8B%D1%88%D0%BD%D1%8F/new");
        urlMap.put(Instant.now().minusSeconds(30), "http://joyreactor.cc/tag/Fantasy/new");
        urlMap.put(Instant.now().minusSeconds(40), "http://joyreactor.cc/tag/Sci-Fi/new");
        urlMap.put(Instant.now().minusSeconds(50), "http://joyreactor.cc/tag/%D0%9C%D1%80%D0%B0%D1%87%D0%BD%D1%8B%D0%B5+%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(60), "http://joyreactor.cc/tag/Traditional+art/new");
        urlMap.put(Instant.now().minusSeconds(70), "http://joyreactor.cc/tag/digital+art/new");
        urlMap.put(Instant.now().minusSeconds(80), "http://joyreactor.cc/tag/animal+art/new");
        urlMap.put(Instant.now().minusSeconds(90), "http://joyreactor.cc/tag/Pin-Up/new");
        urlMap.put(Instant.now().minusSeconds(100), "http://joyreactor.cc/tag/%D0%BE%D0%B1%D0%BE%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(110), "http://joyreactor.cc/tag/Sci-Fi/new");
        urlMap.put(Instant.now().minusSeconds(120), "http://joyreactor.cc/tag/%D0%9C%D1%80%D0%B0%D1%87%D0%BD%D1%8B%D0%B5+%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(130), "http://joyreactor.cc/tag/Traditional+art/new");
        urlMap.put(Instant.now().minusSeconds(140), "http://joyreactor.cc/tag/digital+art/new");
        urlMap.put(Instant.now().minusSeconds(150), "http://joyreactor.cc/tag/art/new");

        urlMap.put(Instant.now().minusSeconds(160), "http://anime.reactor.cc/new");
        urlMap.put(Instant.now().minusSeconds(170), "http://anime.reactor.cc/tag/Anime+Ero/new");
        urlMap.put(Instant.now().minusSeconds(180), "http://anime.reactor.cc/tag/%D0%AD%D1%82%D1%82%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(190), "http://anime.reactor.cc/tag/Animal+Ears/new");
        urlMap.put(Instant.now().minusSeconds(200), "http://anime.reactor.cc/tag/Anime+%D0%9A%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B/new");
        urlMap.put(Instant.now().minusSeconds(210), "http://anime.reactor.cc/tag/Anime+%D0%93%D0%B8%D1%84%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(220), "http://anime.reactor.cc/tag/Anime+Cosplay/new");
        urlMap.put(Instant.now().minusSeconds(230), "http://anime.reactor.cc/tag/Anime+%D0%9D%D1%8F%D1%88%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(240), "http://anime.reactor.cc/tag/Manga/new");
        urlMap.put(Instant.now().minusSeconds(250), "http://anime.reactor.cc/tag/AMV/new");
        urlMap.put(Instant.now().minusSeconds(260), "http://anime.reactor.cc/tag/Anime+Ero+Pantsu/new");
        urlMap.put(Instant.now().minusSeconds(270), "http://anime.reactor.cc/tag/Oppai/new");
        urlMap.put(Instant.now().minusSeconds(280), "http://anime.reactor.cc/tag/Anime+Ero+Swim/new");
        urlMap.put(Instant.now().minusSeconds(290), "http://anime.reactor.cc/tag/Anime+Ero+Ass/new");
        urlMap.put(Instant.now().minusSeconds(300), "http://anime.reactor.cc/tag/Kyonyuu/new");
        urlMap.put(Instant.now().minusSeconds(310), "http://anime.reactor.cc/tag/Anime+Ero+Pussy/new");
        urlMap.put(Instant.now().minusSeconds(320), "http://anime.reactor.cc/tag/Anime+Ero+Yuri/new");

        urlMap.put(Instant.now().minusSeconds(330), "http://pr.reactor.cc/new");
        urlMap.put(Instant.now().minusSeconds(340), "http://pr.reactor.cc/tag/Fully+girl%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(350), "http://pr.reactor.cc/tag/Lingerie%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(360), "http://pr.reactor.cc/tag/Panties%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(370), "http://pr.reactor.cc/tag/Panties+%26+Stockings%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(380), "http://pr.reactor.cc/tag/Stockings%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(390), "http://anime.reactor.cc/tag/Anime+Cosplay/new");
        urlMap.put(Instant.now().minusSeconds(400), "http://pr.reactor.cc/tag/Photoset%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(410), "http://pr.reactor.cc/tag/swimsuit%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(420), "http://pr.reactor.cc/tag/Pleasure+Room+gif/new");
        urlMap.put(Instant.now().minusSeconds(430), "http://pr.reactor.cc/tag/%D0%9A%D0%B0%D1%87%D0%B5%D1%81%D1%82%D0%B2%D0%BE+%D0%B2+%D0%BA%D0%BE%D0%BC%D0%BC%D0%B5%D0%BD%D1%82%D0%B0%D1%80%D0%B8%D1%8F%D1%85%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(440), "http://pr.reactor.cc/tag/Joy+Model%28PR%29/new");
        urlMap.put(Instant.now().minusSeconds(450), "http://pr.reactor.cc/tag/Pleasure+Room+Art/new");
        urlMap.put(Instant.now().minusSeconds(460), "http://pr.reactor.cc/tag/Amateur%28PR%29/new");


        urlMap.put(Instant.now().minusSeconds(470), "http://joyreactor.cc/tag/%D0%AD%D1%80%D0%BE%D1%82%D0%B8%D0%BA%D0%B0/new");
        urlMap.put(Instant.now().minusSeconds(480), "http://joyreactor.cc/tag/%D1%81%D0%B8%D1%81%D1%8C%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(490), "http://joyreactor.cc/tag/%D0%B4%D0%BE%D0%BC%D0%B0%D1%88%D0%BD%D1%8F%D1%8F+%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D0%BA%D0%B0/new");
        urlMap.put(Instant.now().minusSeconds(500), "http://joyreactor.cc/tag/%D0%BF%D0%B5%D1%81%D0%BE%D1%87%D0%BD%D0%B8%D1%86%D0%B0+%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(510), "http://joyreactor.cc/tag/%D0%BF%D0%BE%D0%BF%D0%B0/new");
        urlMap.put(Instant.now().minusSeconds(520), "http://joyreactor.cc/tag/%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B5+%D0%B3%D0%B8%D1%84%D0%BA%D0%B8/new");
        urlMap.put(Instant.now().minusSeconds(530), "http://joyreactor.cc/tag/%D0%BB%D0%B5%D0%B3%D0%BA%D0%B0%D1%8F+%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D0%BA%D0%B0/new");
        urlMap.put(Instant.now().minusSeconds(540), "http://joyreactor.cc/tag/%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D1%87%D0%B5%D1%81%D0%BA%D0%B8%D0%B9+%D0%BF%D0%B8%D1%80%D1%81%D0%B8%D0%BD%D0%B3/new");
        urlMap.put(Instant.now().minusSeconds(550), "http://joyreactor.cc/tag/ero-art/new");
        urlMap.put(Instant.now().minusSeconds(560), "http://joyreactor.cc/tag/ero+monochrome/new");
        urlMap.put(Instant.now().minusSeconds(570), "http://joyreactor.cc/tag/%D0%BF%D0%BB%D1%8F%D0%B6%D0%BD%D0%B0%D1%8F+%D1%8D%D1%80%D0%BE%D1%82%D0%B8%D0%BA%D0%B0/new");


        scheduler.scheduleWithFixedDelay(() -> {
            List<Tag> tags = source.getTags();
            Collections.shuffle(tags, ThreadLocalRandom.current());
            tags.stream().limit(16).forEachOrdered(tag -> {
                if (tag.getRef().endsWith("/")) {
                    urlMap.put(Instant.now().minusSeconds(tlr.nextInt(1000, 6000)), tag.getRef() + "new");
                } else {
                    urlMap.put(Instant.now().minusSeconds(tlr.nextInt(1000, 6000)), tag.getRef() + "/new");
                }
            });
        }, 0, 10, TimeUnit.MINUTES);
    }

    public static void main(String... args) throws SQLException {
        IntStream.range(0, THREAD_COUNT).forEach(i -> scheduler.scheduleWithFixedDelay(CmdUpdater::parsePage,
                THREAD_COUNT * i + THREAD_COUNT,
                ThreadLocalRandom.current().nextInt(8, 16),
                TimeUnit.SECONDS));
        CmdUpdater u = new CmdUpdater(new UpdateStats());
    }

    private static void parsePage() {
        Map.Entry<Instant, String> tagRef;
        if (tlr.nextBoolean()) {
            tagRef = urlMap.pollFirstEntry();
        } else {
            tagRef = urlMap.pollLastEntry();
        }
        WebClient.getDocSync(tagRef.getValue()).ifPresent((doc) -> {
            stats.startTask(Thread.currentThread(), tagRef.getKey(), tagRef.getValue(), parseTagString(doc));
            Tag tag = source.getTag(parseTagString(doc));
            if (tag != null) {
                if (tag.getAvatar() == null) {
                    tag.setAvatar(parseTagAvatar(doc));
                    source.updateTag(tag);
                }
                if (tag.getBanner() == null) {
                    tag.setBanner(parseTagBanner(doc));
                    source.updateTag(tag);
                }
            }
            doc.select("a.next").forEach(next -> urlMap.putIfAbsent(Instant.now(), next.attr("abs:href")));
            doc.select("div.postContainer").stream().map(
                    CmdUpdater::parsePost)
                    .peek(stats::processed)
                    .forEachOrdered(CmdUpdater::update);
            System.out.println("[" + Thread.currentThread().getName() + "]\r\nNEXT '" + parseTagString(doc) + "' : " + tagRef);
            stats.startTask(Thread.currentThread(), Instant.now(), tagRef.getValue(), "[" + parseTagString(doc) + "]");
        });
    }

    private static void update(Post post) {
        Post dbPost = source.getPost(post.getId());
        System.out.println("STATS {");
        if (dbPost == null || dbPost.getUser() == null) {
            System.out.println("\tNEW POSTS: " + stats.incPosts());
            System.out.println("\tNEW COMMENTS: " + stats.addComments(post.getComments()));
            System.out.println("\tNEW RATING: " + stats.addRating(post.getRating()));
            source.insertPost(post);
        } else {
            System.out.println("\tNEW COMMENTS: " + stats.addComments(post.getComments() - dbPost.getComments()));
            System.out.println("\tNEW RATING: " + stats.addRating(post.getRating().subtract(dbPost.getRating())));
            source.updatePost(post);
        }
        if (source.getPostTags(post.getId()).size() == 0) {
            source.setPostTags(post.getId(), post.getTags());
        }
        if (source.getPostImages(post.getId()).size() == 0) {
            source.setPostImages(post.getId(), post.getImages());
        }
        System.out.println("}");
    }

    private static String parseTagString(Element post) {
        return post.select("div#blogName h1").stream()
                .map(Element::text).findFirst().orElse("---JoyReactor---");
    }

    private static Post parsePost(Element post) {
        Post postItem = new Post();
        System.out.println("POST {");
        postItem.setId(parsePostId(post));
        System.out.println("\tID: " + postItem.getId());
        postItem.setUser(parseUser(post));
        System.out.println("\tUser: " + postItem.getUser().getName());
        postItem.setTags(parseTags(post));
        System.out.println("\tTags: " + postItem.getTags());
        postItem.setImages(parseImages(post));
        System.out.println("\tImages: " + postItem.getImages());
        postItem.setComments(parseComments(post));
        System.out.println("\tComments: " + postItem.getComments());
        postItem.setRating(parseRating(post));
        System.out.println("\tRatings: " + postItem.getRating());
        postItem.setPublished(parsePublished(post));
        System.out.println("\tPublished: " + postItem.getPublished());
        System.out.println("}");
        return postItem;
    }

    private static List<Image> parseImages(Element post) {
        return post.select("div.post_content div.image img[src]").stream().map(tag -> new Image(
                Integer.parseInt("0" + tag.attr("width")),
                Integer.parseInt("0" + tag.attr("height")),
                tag.attr("abs:src")
        )).collect(Collectors.toList());
    }

    private static User parseUser(Element post) {
        return post.select("div.uhead_nick").stream()
                .flatMap(user -> user.select(".avatar").stream()
                        .map(av -> {
                            System.out.println("\tAvatar: " +av.attr("abs:src"));
                            User dbUser = source.getUser(user.text());
                            if (dbUser == null) {
                                System.out.println("\tNEW USERS: " + stats.incUsers());
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

    private static ZonedDateTime parsePublished(Element post) {
        return post.select("span.date span[data-time]").stream()
                .map(user -> Instant.ofEpochMilli(1000 * Long.parseLong(user.attr("data-time")))
                        .atZone(ZoneId.of("Europe/Moscow"))).findFirst().orElse(null);
    }

    private static BigDecimal parseRating(Element post) {
        try {
            return post.select(".ufoot .post_rating").stream().map(Element::text)
                    .map(BigDecimal::new).findFirst().orElse(new BigDecimal(0.0d));
        } catch (Exception e) {
            return new BigDecimal(0.0d);
        }
    }

    private static Integer parsePostId(Element post) {
        return post.select(".ufoot .link_wr a.link").stream()
                .map(user -> user.attr("href"))
                .map(id -> id.replace("/post/", ""))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    private static Integer parseComments(Element post) {
        return post.select(".commentnum").stream()
                .map(Element::text)
                .map(text -> text.replace("Комментарии ", "0"))
                .map(Integer::parseInt).findFirst().orElse(-1);

    }

    private static List<Tag> parseTags(Element post) {
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

    private static byte[] parseTagAvatar(Element post) {
        byte[] bytes = new byte[0];
        return post.select("#blogHeader img.blog_avatar[src]").stream()
                .map(img -> WebClient.getBytesSync(img.attr("abs:src")).get()).findFirst().orElse(bytes);
    }

    private static byte[] parseTagBanner(Element post) {
        byte[] bytes = new byte[0];
        return post.select("#tagArticle img.contentInnerHeader[src]").stream()
                .map(img -> WebClient.getBytesSync(img.attr("abs:src")).get()).findFirst().orElse(bytes);
    }

}

