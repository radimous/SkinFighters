package com.radimous.skinfighters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class RemoteNames {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[0-9a-zA-Z_]+", Pattern.CASE_INSENSITIVE);

    private static List<String> remoteNames = new ArrayList<>();

    public static List<String> getRemoteNames() {
        return Collections.unmodifiableList(remoteNames);
    }

    public static void removeRemoteNames() {
        remoteNames.clear();
    }

    /**
     * reloads names from url
     *
     * @return true if at least 1 username is invalid
     * @throws IOException failed to get data from supplied url
     */
    public static boolean reloadRemoteNames() throws IOException {
        boolean invalidUsername = false;
        List<String> names = new ArrayList<>();
        URL url = new URL(Config.NAME_URL.get());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String name = line.trim();
                if (name.length() > 32 || !USERNAME_PATTERN.matcher(name).matches()) {
                    invalidUsername = true;
                    continue;
                }
                names.add(line.trim());
            }
        }
        remoteNames = names;
        SkinFighters.logger.info("Fetched " + remoteNames.size() + " skins from url");
        return invalidUsername;
    }
}
