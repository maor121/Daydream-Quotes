package quote.com.quotes.data.web.theysaidso;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Maor on 25/06/2016.
 */
public class QuoteDeserializer implements JsonDeserializer<List<Quote>> {
    @Override
    public List<Quote> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException
    {
        // Get the "content" element from the parsed JSON
        JsonElement content = je.getAsJsonObject().get("contents");
        JsonArray quotes = content.getAsJsonObject().getAsJsonArray("quotes");

        // Deserialize it. You use a new instance of Gson to avoid infinite recursion
        // to this deserializer
        Type listType = new TypeToken<List<Quote>>() {}.getType();
        return new Gson().fromJson(quotes, listType);

    }
}
