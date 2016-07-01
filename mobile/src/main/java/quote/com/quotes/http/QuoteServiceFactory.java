package quote.com.quotes.http;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import quote.com.quotes.R;
import quote.com.quotes.data.web.IQuote;
import quote.com.quotes.data.web.theysaidso.Quote;
import quote.com.quotes.data.web.theysaidso.QuoteDeserializer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Maor on 07/06/2016.
 */
public class QuoteServiceFactory {
    private static QODService QOD_SERVICE;
    private static TheySaidSoService THEYSAIDSO_SERVICE;
    private static ForismaticService FORISMATIC_SERVICE;
    private static final Object lock = new Object();

    public static QODService getQODService(Context ctx) {
        if (QOD_SERVICE == null) {
            synchronized (lock) {
                if (QOD_SERVICE == null) {
                    String service_url = ctx.getString(R.string.quotesondesign_url);

                    Retrofit REST_ADAPTER = new Retrofit.Builder()
                            .baseUrl(service_url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    QOD_SERVICE = REST_ADAPTER.create(QODService.class);
                }
            }
        }
        return QOD_SERVICE;
    }

    public static ForismaticService getForismaticService(Context ctx) {
        if (FORISMATIC_SERVICE == null) {
            synchronized (lock) {
                if (FORISMATIC_SERVICE == null) {
                    String service_url = ctx.getString(R.string.forismatic_url);

                    Retrofit REST_ADAPTER = new Retrofit.Builder()
                            .baseUrl(service_url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    FORISMATIC_SERVICE = REST_ADAPTER.create(ForismaticService.class);
                }
            }
        }
        return FORISMATIC_SERVICE;
    }

    public static TheySaidSoService getTheySaidSoService(Context ctx) {
        if (THEYSAIDSO_SERVICE == null) {
            synchronized (lock) {
                if (THEYSAIDSO_SERVICE == null) {
                    String service_url = ctx.getString(R.string.theyssaidso_url);

                    Type listType = new TypeToken<List<Quote>>() {}.getType();
                    Gson gson= new GsonBuilder()
                            .registerTypeAdapter(listType, new QuoteDeserializer()).create();

                    Retrofit REST_ADAPTER = new Retrofit.Builder()
                            .baseUrl(service_url)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                    THEYSAIDSO_SERVICE = REST_ADAPTER.create(TheySaidSoService.class);
                }
            }
        }
        return THEYSAIDSO_SERVICE;
    }
}
