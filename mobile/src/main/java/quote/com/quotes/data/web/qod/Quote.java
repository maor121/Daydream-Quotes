package quote.com.quotes.data.web.qod;

import com.google.gson.annotations.SerializedName;

import quote.com.quotes.data.web.IQuote;

/**
 * Created by Maor on 07/06/2016.
 */
public class Quote implements IQuote {

    @SerializedName("ID")
    private long id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;



    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }
}