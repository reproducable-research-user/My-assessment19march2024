package assessment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeLatestStoriesExtractor {

    public static void main(String[] args) {
        String url = "https://time.com";
        try {
            String htmlContent = fetchHTMLContent(url);
            String jsonResult = extractAndFormatLatestStories(htmlContent);
            System.out.println(jsonResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String fetchHTMLContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        connection.disconnect();
        return content.toString();
    }

    private static String extractAndFormatLatestStories(String htmlContent) {
        Pattern pattern = Pattern.compile("<h2 class=\"title\">\\s*<a href=\"([^\"]+)\">([^<]+)</a>");
        Matcher matcher = pattern.matcher(htmlContent);

        StringBuilder jsonResult = new StringBuilder("[\n");
        int count = 0;
        while (matcher.find() && count < 6) {
            String title = matcher.group(2);
            String link = matcher.group(1);
            jsonResult.append("{\n")
                      .append("\"title\": \"").append(title).append("\",\n")
                      .append("\"link\": \"").append(link).append("\"\n")
                      .append("},\n");
            count++;
        }
        // Remove the trailing comma and newline character
        if (jsonResult.length() > 2) {
            jsonResult.delete(jsonResult.length() - 2, jsonResult.length());
        }
        jsonResult.append("\n]");
        return jsonResult.toString();
    }
}

