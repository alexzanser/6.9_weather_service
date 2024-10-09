import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class SimpleYandexWeather {
    private static final String API_KEY = "<api_key>";
    private static final double LAT = 55.75;
    private static final double LON = 37.62;
    private static final int LIMIT = 7;

    public SimpleYandexWeather() {
    }

    public static void main(String[] args) throws Exception {
        String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=%d", LAT, LON, LIMIT);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("X-Yandex-API-Key", API_KEY).GET().build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("Полный ответ от API:");
            System.out.println(responseBody);
            int tempIndex = responseBody.indexOf("\"temp\":");
            if (tempIndex != -1) {
                String tempSubstring = responseBody.substring(tempIndex + 7);
                int commaIndex = tempSubstring.indexOf(",");
                String tempValue = tempSubstring.substring(0, commaIndex);
                System.out.println("Текущая температура: " + tempValue + "°C");
            }

            double totalTemp = 0.0;
            int countDays = 0;

            for(int startIndex = 0; (startIndex = responseBody.indexOf("\"temp_avg\":", startIndex)) != -1; startIndex += 11) {
                String tempAvgSubstring = responseBody.substring(startIndex + 11);
                int tempAvgCommaIndex = tempAvgSubstring.indexOf(",");
                String tempAvgValue = tempAvgSubstring.substring(0, tempAvgCommaIndex);
                totalTemp += Integer.parseInt(tempAvgValue);
                ++countDays;
            }

            if (countDays > 0) {
                double averageTemp = totalTemp / (double)countDays;
                System.out.println("Средняя температура за " + countDays + " дней: " + averageTemp + "°C");
            }
        } else {
            System.out.println("Ошибка: код ответа " + response.statusCode());
        }

    }
}
