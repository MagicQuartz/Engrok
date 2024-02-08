package io.github.magicquartz.engrok.integrations;

import io.github.magicquartz.engrok.Engrok;
import io.github.magicquartz.engrok.config.EngrokConfig;
import me.shedaniel.autoconfig.AutoConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            byte[] input = postData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Engrok.LOGGER.info("Created Gist: " + response.toString());

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            config.gistId = jsonObject.get("id").getAsString();
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
            byte[] input = postData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            Engrok.LOGGER.info("Edited Gist: " + response.toString());
        }

        connection.disconnect();
    }

}
