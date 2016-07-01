package quote.com.quotes.http;

import java.util.List;

import quote.com.quotes.data.web.forismatic.Quote;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Maor on 25/06/2016.
 */
public interface ForismaticService {
    @POST("/api/1.0/")
    @FormUrlEncoded
    Call<Quote> getQuotes(@Field("method") String method,
                                @Field("lang") String lang,
                                @Field("format") String format);
}
