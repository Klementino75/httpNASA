import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String URL_PIC =
            "https://api.nasa.gov/planetary/apod?api_key=qWtbF7dIk9GeW4bMWrOBvemSArNx1fDxjmSYMqtl";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000) // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000) // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(URL_PIC);
        CloseableHttpResponse response = httpClient.execute(request);

        // преобразование ответа в Java-объект
        NASAPic nasaObject = mapper.readValue(response.getEntity().getContent(), NASAPic.class);
        System.out.println("\nURL ссылка на картинку: " + nasaObject.getUrl());
        System.out.println("\nОбъект NASA: " + nasaObject); // объект
        // создание имени файла с картинкой
        String[] arr = nasaObject.getUrl().split("/");
        String fileName = arr[arr.length - 1];
        System.out.println("\nИмя файла: " + fileName);

        response = httpClient.execute(new HttpGet(nasaObject.getUrl()));
        HttpEntity entity = response.getEntity();
        //сохраняем в файл
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            entity.writeTo(fos);
            System.out.printf("\nФайл %s записан на диск.\n", fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}