package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value("null");
            return;
        }

        String hours = String.valueOf(duration.toHours());
        String minutes = String.valueOf(duration.toMinutesPart());

        jsonWriter.jsonValue("\"" + hours + ":" + minutes + "\"");
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        String[] durationElements = jsonReader.nextString().split(":");

        long minutes = Long.parseLong(durationElements[1]) + Long.parseLong(durationElements[0]) * 60;

        return Duration.ofMinutes(minutes);
    }
}
