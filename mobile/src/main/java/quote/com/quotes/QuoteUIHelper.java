package quote.com.quotes;

import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import quote.com.quotes.data.web.IQuote;
import quote.com.quotes.data.web.qod.Quote;
import quote.com.quotes.http.HttpUtils;
import quote.com.quotes.http.QODService;
import quote.com.quotes.http.QuoteServiceFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Maor on 07/06/2016.
 */
public class QuoteUIHelper {
    private static String TAG = QuoteUIHelper.class.getSimpleName();

    public static void changeQuote(final TextView quoteView, final TextView sourceView) {

        HttpUtils.IQuoteCallback callback = new HttpUtils.IQuoteCallback() {
            @Override
            public void updateQuote(IQuote quote) {
                quoteView.setText("\""+ Html.fromHtml(quote.getContent().replace("</p>","\"</p>")));
                sourceView.setText("-"+quote.getTitle());
            }
        };
        HttpUtils.getRandomQuote(quoteView.getContext(), callback);
    }
}
