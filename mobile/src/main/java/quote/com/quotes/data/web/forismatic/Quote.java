package quote.com.quotes.data.web.forismatic;

import com.google.gson.annotations.SerializedName;

import quote.com.quotes.data.web.IQuote;

/**
 * Created by Maor on 25/06/2016.
 */
public class Quote implements IQuote{


    @SerializedName("quoteText")
    String quote;

    @SerializedName("quoteAuthor")
    String author;

    @Override
    public String getContent() {
        return quote;
    }

    @Override
    public String getTitle() {
        return author;
    }
}
