package quote.com.quotes.http;

import java.util.List;
import java.util.Map;

import quote.com.quotes.data.web.qod.Quote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Maor on 07/06/2016.
 */
public interface QODService {
    @GET("/wp-json/posts?filter[orderby]=rand&filter[posts_per_page]=1")
    Call<List<Quote>> getRandomQuote();

    @GET("/wp-json/posts{?filter*}")
    Call<List<Quote>> getQuotesWithFilter(@Query("filter") Map<String, String> filter);
}
