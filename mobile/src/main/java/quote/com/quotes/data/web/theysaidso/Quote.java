package quote.com.quotes.data.web.theysaidso;

import com.google.gson.annotations.SerializedName;

import quote.com.quotes.data.web.IQuote;

/**
 * Created by Maor on 25/06/2016.
 */
public class Quote implements IQuote {

    @SerializedName("id")
    String id;

    @SerializedName("quote")
    String quote;

    @SerializedName("author")
    String author;


    public String getContent() {
        return quote;
    }

    public String getTitle() {
        return author;
    }

    public String getId() {
        return id;
    }
}
