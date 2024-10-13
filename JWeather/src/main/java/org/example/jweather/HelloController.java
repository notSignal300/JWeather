package org.example.jweather;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

public class HelloController {
    @FXML
    Label tempLabel, minTempLabel, maxTempLabel, feelsLikeLabel, sunriseLabel, sunsetLabel, cityNameLabel, humidityLabel, pressureLabel, visibilityLabel, windSpeedLabel, weatherStatus, latitudeLabel, longitudeLabel, timeZoneLabel, localTimeLabel;
    @FXML
    ProgressBar sunSlider, humidityBar;
    @FXML
    ImageView weatherIcon, background, windDirectionImg;

    weatherData weather = new weatherData();

    public void start(String cityName, String API, String unitSys){
        weather.updateWeatherData(getWeatherData(cityName, API, unitSys));
        setDataOnWindow(unitSys);
    }

    private static String getWeatherData(String cityName, String API, String unitSys){
        try{
            String fullUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API + "&units=" + unitSys;
            System.out.println(fullUrl);
            URL url = new URL(fullUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if(responseCode == 200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                System.out.println("You fucked up: " + responseCode);
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void setDataOnWindow(String unitSys){
        tempLabel.setText(weather.temperature + "°");
        minTempLabel.setText(weather.minTemp + "°");
        maxTempLabel.setText(weather.maxTemp + "°");
        feelsLikeLabel.setText("Feels like: " + weather.feelsLike + "°");
        cityNameLabel.setText(weather.cityName + ", " + weather.countryCode);
        humidityLabel.setText(weather.humidity + "%");
        humidityBar.setProgress(weather.humidity / 100.0);
        pressureLabel.setText(weather.pressure + "mb");
        visibilityLabel.setText((unitSys == "metric") ? ((weather.visibility / 1000.0) + "km") : (String.format("%.2f", (weather.visibility / 1000.0) * 0.6213)) + "mi");
        windDirectionImg.setRotate(0);
        windDirectionImg.setRotate(windDirectionImg.getRotate() + weather.windAngle + 180);
        windSpeedLabel.setText(weather.windSpeed + ((unitSys == "metric") ? "m/s" : "mph"));
        weatherStatus.setText(weather.mainWeather);
        String imgPath = "/assets/" + weather.weatherIcon + ".png";
        Image image = new Image(getClass().getResourceAsStream(imgPath));
        weatherIcon.setImage(image);
        imgPath = "/assets/backgrounds/" + weather.weatherIcon + ".png";
        image = new Image(getClass().getResourceAsStream(imgPath));
        background.setImage(image);
        latitudeLabel.setText("Latitude: " + weather.latitude);
        longitudeLabel.setText("Longitude: " + weather.longitude);
        sunriseLabel.setText(convertUNIXtoTimeStamp(weather.unixSunrise));
        sunsetLabel.setText(convertUNIXtoTimeStamp(weather.unixSunset));
        timeZoneLabel.setText("Time zone: GMT" + convertOffsetToGMTtimeZone(weather.timeZoneShiftSecs));
        localTimeLabel.setText("Local time: " + convertUNIXtoTimeStamp(weather.unixDataTaken));
        sunSlider.setProgress(getSunPercentage(weather.unixSunrise, weather.unixSunset, weather.unixDataTaken));
    }

    private String convertUNIXtoTimeStamp(long unixTime){
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        final String formattedTime = Instant.ofEpochSecond(unixTime).atZone(ZoneOffset.of(convertOffsetToGMTtimeZone(weather.timeZoneShiftSecs))).format(formatter);

        return formattedTime;
    }

    // It's literally just a map lol
    private String convertOffsetToGMTtimeZone(int offset){
        Map<Integer, String> timeZones = new HashMap<>();
        timeZones.put(0, "+00:00");
        timeZones.put(3600, "+01:00");
        timeZones.put(7200, "+02:00");
        timeZones.put(10800, "+03:00");
        timeZones.put(14400, "+04:00");
        timeZones.put(18000, "+05:00");
        timeZones.put(19800, "+05:30");
        timeZones.put(21600, "+06:00");
        timeZones.put(25200, "+07:00");
        timeZones.put(28800, "+08:00");
        timeZones.put(32400, "+09:00");
        timeZones.put(34200, "+09:30");
        timeZones.put(36000, "+10:00");
        timeZones.put(39600, "+11:00");
        timeZones.put(43200, "+12:00");
        timeZones.put(46800, "+13:00");
        timeZones.put(50400, "+14:00");
        timeZones.put(-3600, "-01:00");
        timeZones.put(-7200, "-02:00");
        timeZones.put(-10800, "-03:00");
        timeZones.put(-12600, "-03:30");
        timeZones.put(-14400, "-04:00");
        timeZones.put(-18000, "-05:00");
        timeZones.put(-21600, "-06:00");
        timeZones.put(-25200, "-07:00");
        timeZones.put(-28800, "-08:00");
        timeZones.put(-32400, "-09:00");
        timeZones.put(-34200, "-09:30");
        timeZones.put(-36000, "-10:00");
        timeZones.put(-39600, "-11:00");
        timeZones.put(-43200, "-12:00");

        return timeZones.get(offset);
    }

    // all times are in UNIX
    private double getSunPercentage(long sunriseTime, long sunsetTime, long currentTime){
        double sunPercentage;
        sunPercentage = (double) (currentTime - sunriseTime) / (double) (sunsetTime - sunriseTime);
        return (sunPercentage > 1) ? 1 : (sunPercentage <= 0) ? 0 : sunPercentage;
    }

    @FXML
    private void goBackToStartScreen(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("start-screen.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}

class weatherData {
    int weatherId, temperature, feelsLike, maxTemp, minTemp, pressure, humidity, visibility, windAngle, cloudPercentage, timeZoneShiftSecs;
    double windSpeed, latitude, longitude;
    long unixDataTaken, unixSunrise, unixSunset;
    String mainWeather, weatherDescription, countryCode, weatherIcon, cityName;

    // You don't need to see this please no, no you don't
    public void updateWeatherData(String response){
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray weatherArray = jsonResponse.getJSONArray("weather");
            JSONObject weatherArrayObject = weatherArray.getJSONObject(0);

            weatherId = weatherArrayObject.getInt("id");                                  // Weather ID  Ex.: 800, 501
            mainWeather = weatherArrayObject.getString("main");                               // Main weather Ex.: Clear, Rainy, Foggy
            weatherDescription = weatherArrayObject.getString("description");                        // Weather description
            temperature = (int) Math.round(jsonResponse.getJSONObject("main").getDouble("temp"));           // Temperature
            feelsLike = (int) Math.round(jsonResponse.getJSONObject("main").getDouble("feels_like"));     // Sensation
            maxTemp = (int) Math.round(jsonResponse.getJSONObject("main").getDouble("temp_max"));       // Max temperature
            minTemp = (int) Math.round(jsonResponse.getJSONObject("main").getDouble("temp_min"));       // Min temperature
            pressure = jsonResponse.getJSONObject("main").getInt("pressure");          // Pressure
            humidity = jsonResponse.getJSONObject("main").getInt("humidity");          // Humidity (%)
            visibility = jsonResponse.getInt("visibility");                                  // Visibility
            windSpeed  = jsonResponse.getJSONObject("wind").getDouble("speed");         // Wind speed
            windAngle = jsonResponse.getJSONObject("wind").getInt("deg");              // Angle of wind (deg)
            cloudPercentage = jsonResponse.getJSONObject("clouds").getInt("all");            // Percentage of clouds (?)
            unixDataTaken = jsonResponse.getLong("dt");                                         // Get unix time stamp when data was taken (UTC)
            if(jsonResponse.getJSONObject("sys").has("country")){ countryCode = jsonResponse.getJSONObject("sys").getString("country"); }
            else{ countryCode = "??"; }    // Two letter country code
            unixSunrise = jsonResponse.getJSONObject("sys").getLong("sunrise");           // Unix time stamp of sunrise (UTC)
            unixSunset = jsonResponse.getJSONObject("sys").getLong("sunset");            // Unix time stamp of sunset (UTC)
            timeZoneShiftSecs = jsonResponse.getInt("timezone");                                   // Shift in seconds from UTC
            weatherIcon = weatherArrayObject.getString("icon");
            cityName = jsonResponse.getString("name");
            latitude = jsonResponse.getJSONObject("coord").getDouble("lat");
            longitude = jsonResponse.getJSONObject("coord").getDouble("lon");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}