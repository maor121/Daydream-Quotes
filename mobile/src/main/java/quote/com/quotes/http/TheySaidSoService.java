package quote.com.quotes.http;

import java.util.List;

import quote.com.quotes.data.web.theysaidso.Quote;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Maor on 25/06/2016.
 */
public interface TheySaidSoService {
    @GET("/qod.json")
    Call<List<Quote>> getRandomQuotes(@Query("category") String category);
}
