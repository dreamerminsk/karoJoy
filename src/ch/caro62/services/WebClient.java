package ch.caro62.services;

import ch.caro62.utils.CallbackFuture;
import ch.caro62.utils.UserAgentStrings;
import com.google.common.util.concurrent.RateLimiter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author karo62
 */
public class WebClient {

    private static final Random RND = new Random();

    private static final Map<String, RateLimiter> LIMITERS = new TreeMap<>();

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .callTimeout(32, TimeUnit.SECONDS)
            .connectTimeout(32, TimeUnit.SECONDS)
            .readTimeout(32, TimeUnit.SECONDS)
            .build();
    private static final String USER_AGENT = "User-Agent";

    private static CallbackFuture getRequest(Request req) {
        acquire(req.url().host(), 1600 + RND.nextInt(800));
        CallbackFuture result = new CallbackFuture();
        CLIENT.newCall(req).enqueue(result);
        return result;
    }

    public static Optional<Document> getDocSync(String ref) {
        Request req = createGetRequest(ref);
        acquire(req.url().host(), 4000 + RND.nextInt(8000));
        try (Response response = CLIENT.newCall(req).execute()) {
            return Optional.ofNullable(Jsoup.parse(Objects.requireNonNull(response.body()).string(),
                    req.url().scheme() + "://" + req.url().host()));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static Optional<byte[]> getBytesSync(String ref) {
        Request req = createGetRequest(ref);
        acquire(req.url().host(), 1000 + RND.nextInt(2000));
        try (Response response = CLIENT.newCall(req).execute()) {
            return Optional.of(Objects.requireNonNull(response.body()).bytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static void acquire(String host, int permits) {
        if (!LIMITERS.containsKey(host)) {
            LIMITERS.put(host, RateLimiter.create(1000));
        }
        LIMITERS.get(host).acquire(permits);
    }

    private static Request createGetRequest(String ref) {
        return new Request.Builder()
                .header(USER_AGENT, UserAgentStrings.getRandom())
                .url(ref).build();
    }

}
