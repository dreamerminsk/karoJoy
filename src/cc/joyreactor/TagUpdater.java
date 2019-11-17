package cc.joyreactor;

import cc.joyreactor.data.Tag;
import ch.caro62.services.WebClient;
import org.jsoup.nodes.Element;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class TagUpdater {

    public static final int THREAD_COUNT = 32;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_COUNT);

    private final ConcurrentSkipListMap<String, String> urlMap = new ConcurrentSkipListMap<>();
    private final Source source;
    private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

    public TagUpdater() throws SQLException {
        source = Source.getInstance();
        List<Tag> tags = source.getTags();

        tags.stream().filter((item) -> item.getAvatar() == null).forEachOrdered(tag -> urlMap.put(tag.getTag(), tag.getRef()));
    }

    public static void main(String... args) throws SQLException {
        TagUpdater u = new TagUpdater();
        IntStream.range(0, THREAD_COUNT).forEach(i -> scheduler.scheduleWithFixedDelay(u::parsePage,
                THREAD_COUNT * i + THREAD_COUNT,
                ThreadLocalRandom.current().nextInt(8, 16),
                TimeUnit.SECONDS));
    }

    private void parsePage() {
        Map.Entry<String, String> tagRef = urlMap.pollFirstEntry();
        WebClient.getDocSync(tagRef.getValue()).ifPresent((doc) -> {
            System.out.println("\t\t\t[" + Thread.currentThread().getName() + "]  NEXT '" + tagRef.getKey() + "' : " + tagRef);

            Tag tag = source.getTag(tagRef.getKey());
            if (tag.getAvatar() == null) {
                tag.setAvatar(parseTagAvatar(doc));
                System.out.println("\t\t\tAVATAR: " + tag.getAvatar().length);
                source.updateTag(tag);
            }
            if (tag.getBanner() == null) {
                tag.setBanner(parseTagBanner(doc));
                System.out.println("\t\t\tBANNER: " + tag.getBanner().length);
                source.updateTag(tag);
            }

        });
    }

    private byte[] parseTagAvatar(Element post) {
        return post.select("#blogHeader img.blog_avatar[src]").stream()
                .map(img -> WebClient.getBytesSync(img.attr("abs:src")).get()).findFirst().orElse(new byte[0]);
    }

    private byte[] parseTagBanner(Element post) {
        return post.select("#tagArticle img#contentInnerHeader[src]").stream()
                .map(img -> WebClient.getBytesSync(img.attr("abs:src")).get()).findFirst().orElse(new byte[0]);
    }
}
