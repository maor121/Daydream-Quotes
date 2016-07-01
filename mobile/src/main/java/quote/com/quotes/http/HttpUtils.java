package quote.com.quotes.http;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import java.util.List;
import java.util.Random;

import quote.com.quotes.data.web.IQuote;
import quote.com.quotes.data.web.forismatic.Quote;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Maor on 25/06/2016.
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();

    public static void getRandomQuote(Context ctx, final IQuoteCallback callback) {
        Random r = new Random();
        int serviceIndex = r.nextInt(2); //ignore theysaidso (paid api)

        switch (serviceIndex) {
            case 0: {
                QODService service = QuoteServiceFactory.getQODService(ctx);
                Call<List<quote.com.quotes.data.web.qod.Quote>> call = service.getRandomQuote();
                call.enqueue(new Callback<List<quote.com.quotes.data.web.qod.Quote>>() { //Async
                    @Override
                    public void onResponse(Call<List<quote.com.quotes.data.web.qod.Quote>> call, Response<List<quote.com.quotes.data.web.qod.Quote>> response) {
                        if (response.isSuccessful()) {
                            quote.com.quotes.data.web.qod.Quote quote = response.body().get(0);

                            callback.updateQuote(quote);
                        } else {
                            // handle request errors yourself
                            Log.d(TAG, "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<quote.com.quotes.data.web.qod.Quote>> call, Throwable t) {
                        // handle execution failures like no internet connectivity
                        Log.d(TAG, "Error: " + t.toString());
                    }
                });
            }
            break;
            case 1 : {
                ForismaticService service = QuoteServiceFactory.getForismaticService(ctx);
                Call<quote.com.quotes.data.web.forismatic.Quote> call = service.getQuotes(
                        "getQuote","en","json"
                );
                call.enqueue(new Callback<Quote>() {
                    @Override
                    public void onResponse(Call<Quote> call, Response<Quote> response) {
                        if (response.isSuccessful()) {
                            quote.com.quotes.data.web.forismatic.Quote quote = response.body();

                            callback.updateQuote(quote);
                        } else {
                            // handle request errors yourself
                            Log.d(TAG, "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Quote> call, Throwable t) {
                        Log.d(TAG, "Error: " + t.toString());
                    }
                });
            }
            break;
            case 2 : {
                TheySaidSoService service = QuoteServiceFactory.getTheySaidSoService(ctx);
                Call<List<quote.com.quotes.data.web.theysaidso.Quote>> call = service.getRandomQuotes("inspire");
                call.enqueue(new Callback<List<quote.com.quotes.data.web.theysaidso.Quote>>() { //Async
                    @Override
                    public void onResponse(Call<List<quote.com.quotes.data.web.theysaidso.Quote>> call, Response<List<quote.com.quotes.data.web.theysaidso.Quote>> response) {
                        if (response.isSuccessful()) {
                            quote.com.quotes.data.web.theysaidso.Quote quote = response.body().get(0);

                            callback.updateQuote(quote);
                        } else {
                            // handle request errors yourself
                            Log.d(TAG, "Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<quote.com.quotes.data.web.theysaidso.Quote>> call, Throwable t) {
                        // handle execution failures like no internet connectivity
                        Log.d(TAG, "Error: " + t.toString());
                    }
                });
            }
                break;
        }
    }

    public interface IQuoteCallback {
        void updateQuote(IQuote quote);
    }
}
