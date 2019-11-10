package ch.caro62.utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author karo62
 */
public class CallbackFuture extends CompletableFuture<Optional<Response>> implements Callback {

    @Override
    public void onResponse(Call call, Response response) {
        super.complete(Optional.ofNullable(response));
    }

    @Override
    public void onFailure(Call call, IOException e) {
        super.completeExceptionally(e);
    }
}
