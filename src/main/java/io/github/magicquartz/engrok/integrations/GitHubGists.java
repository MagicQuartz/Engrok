package io.github.magicquartz.engrok.integrations;

import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GitHubGists {
    EngrokConfig config = AutoConfig.getConfigHolder(EngrokConfig.class).getConfig();
    private final String GITHUB_API_BASE_URL = "https://api.github.com";
    private final String GITHUB_TOKEN = config.gitHubAuthToken;

    public void setIpGist(String ip) {
        if(!config.gitHubAuthToken.isEmpty()) {
            String gistId = config.gistId; // If editing an existing gist
            String fileName = "engrok_server_ip.txt";

            // Create or edit a gist
            try {
                if (gistId.isEmpty()) {
                    createGist(ip, fileName);
                } else {
                    editGist(gistId, ip, fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGist(String content, String fileName) throws IOException {
        String apiUrl = GITHUB_API_BASE_URL + "/gists";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String postData = "{\"files\": {\"" + fileName + "\": {\"content\": \"" + content + "\"}}}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            config.gistId = jsonObject.get("id").getAsString();
            Engrok.LOGGER.info("Created Gist, url: " + jsonObject.get("html_url").getAsString());
            Engrok.configHolder.save();
        }
        connection.disconnect();
    }
    private void editGist(String gistId, String content, String fileName) throws IOException {
        String apiUrl = GITHUB_API_BASE_URL + "/gists/" + gistId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST"); // Use POST method
        connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Specify that it's a PATCH operation using the _method parameter
        String postData = "{\"_method\": \"PATCH\", \"files\": {\"" + fileName + "\": {\"content\": \"" + content + "\"}}}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            Engrok.LOGGER.info("Edited Gist, new address " + jsonObject.get("html_url").getAsString());
        }

        connection.disconnect();
    }

    public String getGistUrl() throws IOException
    {
        String gistId = config.gistId;
        if(gistId.isEmpty())
            return "Error: No Gist ID Exists in the config file!";
        String apiUrl = GITHUB_API_BASE_URL + "/gists/" + gistId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                content.append(responseLine);
            }
        }

        connection.disconnect();

        JsonObject jsonObject = JsonParser.parseString(content.toString()).getAsJsonObject();
        return jsonObject.get("html_url").getAsString();
    }
    
    public String getGistContent() throws IOException {
        String gistId = config.gistId;
        if(gistId.isEmpty())
            return "Error: No Gist ID Exists in the config file!";
        String apiUrl = GITHUB_API_BASE_URL + "/gists/" + gistId;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "token " + GITHUB_TOKEN);

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                content.append(responseLine);
            }
        }

        connection.disconnect();

        // Parse the JSON response to extract the gist content
        JsonObject gistJson = JsonParser.parseString(content.toString()).getAsJsonObject();
        JsonObject files = gistJson.getAsJsonObject("files");

        String fileName = files.keySet().iterator().next(); // Get the first file name
        JsonObject file = files.getAsJsonObject(fileName);

        return file.get("content").getAsString();
    }
}
