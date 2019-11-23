package cc.joyreactor;

import cc.joyreactor.data.Tag;
import ch.caro62.services.WebClient;
import org.jsoup.nodes.Element;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class TagUpdater {

    public static final int THREAD_COUNT = 32;

    private static final DecimalFormat df = new DecimalFormat("###,###");

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(THREAD_COUNT);

    private final ConcurrentLinkedDeque<Tag> urlMap = new ConcurrentLinkedDeque<>();
    private final Source source;
    private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

    private final AtomicLong avatarCount = new AtomicLong(0);
    private final AtomicLong avatarSize = new AtomicLong(0);
    private final AtomicLong bannerCount = new AtomicLong(0);
    private final AtomicLong bannerSize = new AtomicLong(0);

    public TagUpdater() throws SQLException {
        source = Source.getInstance();
        List<Tag> tags = source.getTags();

        tags.stream().filter((item) -> item.getAvatar() == null).forEachOrdered(urlMap::addLast);
    }

    public static void main(String... args) throws SQLException {
        TagUpdater u = new TagUpdater();
        IntStream.range(0, THREAD_COUNT).forEach(i -> scheduler.scheduleWithFixedDelay(u::parsePage,
                THREAD_COUNT * i + THREAD_COUNT,
                ThreadLocalRandom.current().nextInt(8, 16),
                TimeUnit.SECONDS));
    }

    private void parsePage() {
        Tag tagRef = urlMap.pollFirst();
        WebClient.getDocSync(Objects.requireNonNull(tagRef).getRef()).ifPresent((doc) -> {
            System.out.println("\t\t[" + Thread.currentThread().getName() + "]  NEXT '" + tagRef.getTag() + "' : " + tagRef);

            if (tagRef.getAvatar() == null) {
                tagRef.setAvatar(parseTagAvatar(doc));
                System.out.println("\t\tAVATAR-" + avatarCount.incrementAndGet() + ": " + tagRef.getAvatar().length);
                System.out.println("\t\tAVATAR-ALL: " + df.format(avatarSize.addAndGet(tagRef.getAvatar().length)));
                source.updateTag(tagRef);
            }
            if (tagRef.getBanner() == null) {
                tagRef.setBanner(parseTagBanner(doc));
                System.out.println("\t\tBANNER-" + bannerCount.incrementAndGet() + ": " + tagRef.getBanner().length);
                System.out.println("\t\tBANNER-ALL: " + df.format(bannerSize.addAndGet(tagRef.getBanner().length)));
                source.updateTag(tagRef);
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
