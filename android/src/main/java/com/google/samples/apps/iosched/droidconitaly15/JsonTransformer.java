package com.google.samples.apps.iosched.droidconitaly15;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;
import com.google.samples.apps.iosched.BuildConfig;
import com.google.samples.apps.iosched.droidconitaly15.model.DI15Session;
import com.google.samples.apps.iosched.droidconitaly15.model.DI15Speaker;
import com.google.samples.apps.iosched.io.model.Block;
import com.google.samples.apps.iosched.io.model.Room;
import com.google.samples.apps.iosched.io.model.Session;
import com.google.samples.apps.iosched.io.model.Speaker;
import com.google.samples.apps.iosched.io.model.Tag;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_BLOCKS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_ROOMS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_SESSIONS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_SPEAKERS;
import static com.google.samples.apps.iosched.sync.ConferenceDataHandler.DATA_KEY_TAGS;

public final class JsonTransformer {

    private static final ThreadLocal<DateFormat> DATE_PARSER = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            // "9 April 2015 11:00"
            final String format = "d MMMM yyyy kk:mm";
            final DateFormat dateParser = new SimpleDateFormat(format, Locale.US);
            dateParser.setTimeZone(TimeZone.getTimeZone("Rome"));
            return dateParser;
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMATTER = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            // "2014-06-26T23:00:00.000Z"
            final String format = "yyyy-MM-dd'T'kk:mm:ss.S'Z'";
            final DateFormat dateFormatter = new SimpleDateFormat(format, Locale.US);
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormatter;
        }
    };

    private JsonTransformer() {
    }

    @Nullable
    public static String[] transformJson(@Nullable String[] dataBodies) {

        if (dataBodies != null) {
            final Gson gson = new GsonBuilder().create();
            for (int i = 0, end = dataBodies.length; i < end; i++) {
                dataBodies[i] = transformJson(dataBodies[i], gson);
            }
        }
        return dataBodies;
    }

    @Nullable
    private static String transformJson(@Nullable String dataBody, @NonNull Gson gson) {

        if (dataBody == null) return null;

        final Map<String, Speaker> speakers = new HashMap<>();
        final Map<String, Room> rooms = new HashMap<>();
        final Map<String, Tag> tags = new HashMap<>();
        final List<Block> blocks = new ArrayList<>();
        final List<Session> sessions = new ArrayList<>();

        // read all the sessions incrementally and aggregate the data in the new format
        final JsonReader reader = new JsonReader(new StringReader(dataBody));
        try {
            reader.setLenient(true); // To err is human

            // the whole file is a single JSON object
            reader.beginObject();
            while (reader.hasNext()) {
                if ("sessions".equals(reader.nextName())) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        final DI15Session di15Session = gson.fromJson(reader, DI15Session.class);
                        if (di15Session != null) {
                            transformJson(di15Session, speakers, rooms, tags, blocks, sessions);
                        }
                    }
                    break;
                } else {
                    reader.skipValue();
                }
            }
        } catch (JsonParseException | ParseException | IOException e) {
            if (BuildConfig.DEBUG) {
                Log.w("JsonTransformer", "Error while transforming Json.", e);
            }
            return dataBody; // don't transform if it's not what we expect
        } finally {
            try {
                reader.close();
            } catch (IOException ignore) {
            }
        }

        final Map<String, Object> transformed = new HashMap<>();
        transformed.put(DATA_KEY_SPEAKERS, speakers.values());
        transformed.put(DATA_KEY_ROOMS, rooms.values());
        transformed.put(DATA_KEY_BLOCKS, blocks);
        transformed.put(DATA_KEY_SESSIONS, sessions);
        transformed.put(DATA_KEY_TAGS, tags.values());
        return gson.toJson(transformed);
    }

    private static void transformJson(
            @NonNull DI15Session di15Session,
            @NonNull Map<String, Speaker> speakers,
            @NonNull Map<String, Room> rooms,
            @NonNull Map<String, Tag> tags,
            @NonNull List<Block> blocks,
            @NonNull List<Session> sessions) throws ParseException {

        // *** SPEAKERS ***

        final List<DI15Speaker> di15speakers = di15Session.speakers;
        if (di15speakers != null) {
            for (DI15Speaker di15Speaker : di15speakers) {
                if (!speakers.containsKey(di15Speaker.speaker_id)) {
                    final Speaker speaker = new Speaker();
                    speaker.id = di15Speaker.speaker_id;
                    speaker.name = di15Speaker.post_title;
                    speaker.company = di15Speaker.header;
                    speaker.bio = di15Speaker.bio;
                    speaker.thumbnailUrl = di15Speaker.post_image;
                    speaker.plusoneUrl = di15Speaker.url;
                    speakers.put(speaker.id, speaker);
                }
            }
        }

        // *** ROOMS ***

        final String roomName = di15Session.location;
        if (roomName != null) {
            if (!rooms.containsKey(roomName)) {
                final Room room = new Room();
                room.id = roomName;
                room.name = roomName;
                rooms.put(roomName, room);
            }
        }

        // *** TAGS ***

        final List<String> di15Tags = di15Session.track;
        if (di15Tags != null) {
            for (String di15tag : di15Tags) {
                if (!tags.containsKey(di15tag)) {
                    final Tag tag = new Tag();
                    tag.category = "TOPIC";
                    tag.name = di15tag;
                    tag.tag = di15tag;
                }
            }
        }

        // *** SESSIONS ***

        final Session session = new Session();
        session.id = di15Session.url;
        session.title = di15Session.post_title;
        session.description = di15Session.content;
        session.url = di15Session.url;
        session.room = roomName;
        session.tags =
                di15Tags == null ? new String[0] : di15Tags.toArray(new String[di15Tags.size()]);

        // speakers
        final int size = di15speakers == null ? 0 : di15speakers.size();
        final String[] speakerIds = new String[size];
        for (int i = 0; i < size; i++) {
            speakerIds[i] = di15speakers.get(i).speaker_id;
        }
        session.speakers = speakerIds;

        // timestamps
        final DateFormat dateParser = DATE_PARSER.get();
        final DateFormat dateFormatter = DATE_FORMATTER.get();
        session.startTimestamp =
                convertDateTime(di15Session.date, di15Session.time, dateParser, dateFormatter);
        session.endTimestamp =
                convertDateTime(di15Session.date, di15Session.end_time, dateParser, dateFormatter);

        sessions.add(session);

        // *** BLOCKS ***

        final Block block = new Block();
        block.start = session.startTimestamp;
        block.end = session.endTimestamp;
        block.title = session.title;
        block.subtitle = roomName;
        blocks.add(block);
    }

    @Nullable
    private static String convertDateTime(
            @NonNull String date,
            @NonNull String time,
            @NonNull DateFormat dateParser,
            @NonNull DateFormat dateFormatter)
            throws ParseException {

        time = time.replace('.', ':');
        return dateFormatter.format(dateParser.parse(date + " " + time));
    }
}
