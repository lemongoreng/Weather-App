import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WeatherAppGUI extends JFrame {
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Get your free API key from https://www.weatherapi.com/
    private static final String BASE_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String FORECAST_URL = "http://api.weatherapi.com/v1/forecast.json";
    private static final String FAVORITES_FILE = "weather_favorites.txt";
    
    private JTextField cityField;
    private JButton searchButton;
    private JButton addFavoriteButton;
    private JButton darkModeToggle;
    private JTextArea weatherDisplay;
    private JTextArea hourlyDisplay;
    private JTextArea weeklyDisplay;
    private JLabel statusLabel;
    private DefaultListModel<String> favoritesModel;
    private JList<String> favoritesList;
    private List<String> favorites;
    private JTabbedPane tabbedPane;
    private boolean isDarkMode = false;
    
    // Color schemes
    private Color lightBg = new Color(240, 248, 255);
    private Color lightText = Color.BLACK;
    private Color lightTextArea = Color.WHITE;
    private Color darkBg = new Color(45, 45, 48);
    private Color darkText = new Color(220, 220, 220);
    private Color darkTextArea = new Color(30, 30, 30);
    
    public WeatherAppGUI() {
        setTitle("Weather App");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load favorites from file
        favorites = new ArrayList<>();
        loadFavorites();
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(lightBg);
        
        // Top panel for search
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(lightBg);
        
        JLabel promptLabel = new JLabel("Enter City:");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        cityField = new JTextField(20);
        cityField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        searchButton = new JButton("Get Weather");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        addFavoriteButton = new JButton("⭐ Add to Favorites");
        addFavoriteButton.setFont(new Font("Arial", Font.BOLD, 12));
        addFavoriteButton.setBackground(new Color(255, 165, 0));
        addFavoriteButton.setForeground(Color.WHITE);
        addFavoriteButton.setFocusPainted(false);
        
        darkModeToggle = new JButton("🌙 Dark Mode");
        darkModeToggle.setFont(new Font("Arial", Font.BOLD, 12));
        darkModeToggle.setBackground(new Color(75, 75, 75));
        darkModeToggle.setForeground(Color.WHITE);
        darkModeToggle.setFocusPainted(false);
        
        searchPanel.add(promptLabel);
        searchPanel.add(cityField);
        searchPanel.add(searchButton);
        searchPanel.add(addFavoriteButton);
        searchPanel.add(darkModeToggle);
        
        // Create tabbed pane for current and hourly weather
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Center panel for weather display
        weatherDisplay = new JTextArea();
        weatherDisplay.setEditable(false);
        weatherDisplay.setFont(new Font("Monospaced", Font.PLAIN, 13));
        weatherDisplay.setBackground(Color.WHITE);
        weatherDisplay.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222), 2));
        weatherDisplay.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(weatherDisplay);
        
        // Hourly forecast panel
        hourlyDisplay = new JTextArea();
        hourlyDisplay.setEditable(false);
        hourlyDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hourlyDisplay.setBackground(Color.WHITE);
        hourlyDisplay.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222), 2));
        hourlyDisplay.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane hourlyScrollPane = new JScrollPane(hourlyDisplay);
        
        // Weekly forecast panel
        weeklyDisplay = new JTextArea();
        weeklyDisplay.setEditable(false);
        weeklyDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        weeklyDisplay.setBackground(Color.WHITE);
        weeklyDisplay.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222), 2));
        weeklyDisplay.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane weeklyScrollPane = new JScrollPane(weeklyDisplay);
        
        // Add tabs
        tabbedPane.addTab("☀️ Current Weather", scrollPane);
        tabbedPane.addTab("🕐 Hourly Forecast", hourlyScrollPane);
        tabbedPane.addTab("📅 7-Day Forecast", weeklyScrollPane);
        
        // Left panel for favorites
        JPanel favoritesPanel = new JPanel();
        favoritesPanel.setLayout(new BorderLayout(5, 5));
        favoritesPanel.setBackground(lightBg);
        favoritesPanel.setPreferredSize(new Dimension(180, 0));
        
        JLabel favLabel = new JLabel("⭐ Favorite Cities");
        favLabel.setFont(new Font("Arial", Font.BOLD, 13));
        favLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        
        favoritesModel = new DefaultListModel<>();
        for (String fav : favorites) {
            favoritesModel.addElement(fav);
        }
        
        favoritesList = new JList<>(favoritesModel);
        favoritesList.setFont(new Font("Arial", Font.PLAIN, 12));
        favoritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        favoritesList.setBackground(Color.WHITE);
        favoritesList.setBorder(BorderFactory.createLineBorder(new Color(176, 196, 222), 1));
        
        JScrollPane favScrollPane = new JScrollPane(favoritesList);
        
        JButton removeFavButton = new JButton("Remove");
        removeFavButton.setFont(new Font("Arial", Font.PLAIN, 11));
        
        favoritesPanel.add(favLabel, BorderLayout.NORTH);
        favoritesPanel.add(favScrollPane, BorderLayout.CENTER);
        favoritesPanel.add(removeFavButton, BorderLayout.SOUTH);
        
        // Bottom panel for status
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        
        // Add panels to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(favoritesPanel, BorderLayout.WEST);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Add action listeners
        searchButton.addActionListener(e -> fetchWeather());
        
        cityField.addActionListener(e -> fetchWeather());
        
        addFavoriteButton.addActionListener(e -> addToFavorites());
        
        favoritesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selected = favoritesList.getSelectedValue();
                    if (selected != null) {
                        cityField.setText(selected);
                        fetchWeather();
                    }
                }
            }
        });
        
        removeFavButton.addActionListener(e -> removeFromFavorites());
        
        darkModeToggle.addActionListener(e -> toggleDarkMode());
        
        setVisible(true);
    }
    
    private void fetchWeather() {
        String city = cityField.getText().trim();
        
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name!", 
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        statusLabel.setText("Fetching weather data...");
        searchButton.setEnabled(false);
        weatherDisplay.setText("Loading...");
        hourlyDisplay.setText("Loading...");
        weeklyDisplay.setText("Loading...");
        
        // Use SwingWorker to avoid freezing the UI
        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            @Override
            protected String[] doInBackground() {
                String currentWeather = getWeather(city);
                String forecast = getForecast(city, 7);
                return new String[]{currentWeather, forecast};
            }
            
            @Override
            protected void done() {
                try {
                    String[] results = get();
                    String weatherData = results[0];
                    String forecastData = results[1];
                    
                    if (weatherData != null) {
                        displayWeather(weatherData);
                        statusLabel.setText("Weather data updated");
                    } else {
                        weatherDisplay.setText("Failed to fetch weather data.\nPlease check the city name and try again.");
                        statusLabel.setText("Error fetching data");
                    }
                    
                    if (forecastData != null) {
                        displayHourlyForecast(forecastData);
                        displayWeeklyForecast(forecastData);
                    } else {
                        hourlyDisplay.setText("Failed to fetch hourly forecast.");
                        weeklyDisplay.setText("Failed to fetch 7-day forecast.");
                    }
                } catch (Exception e) {
                    weatherDisplay.setText("Error: " + e.getMessage());
                    hourlyDisplay.setText("Error: " + e.getMessage());
                    weeklyDisplay.setText("Error: " + e.getMessage());
                    statusLabel.setText("Error occurred");
                }
                searchButton.setEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private String getWeather(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + encodedCity;
            
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return null;
            }
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private String getForecast(String city, int days) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            String urlString = FORECAST_URL + "?key=" + API_KEY + "&q=" + encodedCity + "&days=" + days;
            
            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return null;
            }
            
        } catch (Exception e) {
            return null;
        }
    }
    
    private void displayWeather(String jsonResponse) {
        try {
            String cityName = extractValue(jsonResponse, "\"name\":\"", "\"");
            String country = extractValue(jsonResponse, "\"country\":\"", "\"");
            String region = extractValue(jsonResponse, "\"region\":\"", "\"");
            String localTime = extractValue(jsonResponse, "\"localtime\":\"", "\"");
            String tempC = extractValue(jsonResponse, "\"temp_c\":", ",");
            String condition = extractValue(jsonResponse, "\"text\":\"", "\"");
            String humidity = extractValue(jsonResponse, "\"humidity\":", ",");
            String feelsLikeC = extractValue(jsonResponse, "\"feelslike_c\":", ",");
            String visibility = extractValue(jsonResponse, "\"vis_km\":", ",");
            String uvIndex = extractValue(jsonResponse, "\"uv\":", ",");
            
            StringBuilder display = new StringBuilder();
            display.append("╔════════════════════════════════════════════════╗\n");
            display.append("  📍 ").append(cityName);
            if (!region.equals("N/A") && !region.isEmpty()) {
                display.append(", ").append(region);
            }
            display.append(", ").append(country).append("\n");
            display.append("  📅 ").append(localTime).append("\n");
            display.append("╚════════════════════════════════════════════════╝\n\n");
            
            display.append("🌡️  Temperature\n");
            display.append("    ").append(tempC).append("°C\n");
            display.append("    Feels like: ").append(feelsLikeC).append("°C\n\n");
            
            display.append("☁️  Conditions\n");
            display.append("    ").append(getWeatherIcon(condition)).append(" ").append(condition).append("\n\n");
            
            display.append("💧 Humidity:      ").append(humidity).append("%\n");
            display.append("👁️  Visibility:    ").append(visibility).append(" km\n");
            display.append("☀️  UV Index:      ").append(uvIndex).append("\n");
            
            weatherDisplay.setText(display.toString());
            
        } catch (Exception e) {
            weatherDisplay.setText("Error parsing weather data: " + e.getMessage());
        }
    }
    
    private void displayHourlyForecast(String jsonResponse) {
        try {
            // Extract city name and date for header
            String cityName = extractValue(jsonResponse, "\"name\":\"", "\"");
            String forecastDate = extractValue(jsonResponse, "\"date\":\"", "\"");
            
            StringBuilder display = new StringBuilder();
            display.append("╔════════════════════════════════════════════════╗\n");
            display.append("          24-HOUR WEATHER FORECAST\n");
            display.append("  📍 ").append(cityName).append("\n");
            display.append("  📅 ").append(forecastDate).append("\n");
            display.append("╚════════════════════════════════════════════════╝\n\n");
            
            // Find the forecast array
            int hourIndex = jsonResponse.indexOf("\"hour\":[");
            if (hourIndex == -1) {
                hourlyDisplay.setText("No hourly data available.");
                return;
            }
            
            String hourData = jsonResponse.substring(hourIndex);
            
            // Parse each hour
            int startPos = 0;
            int hourCount = 0;
            
            while (hourCount < 24) {
                int timeStart = hourData.indexOf("\"time\":\"", startPos);
                if (timeStart == -1) break;
                
                timeStart += 8;
                int timeEnd = hourData.indexOf("\"", timeStart);
                String time = hourData.substring(timeStart, timeEnd);
                
                // Extract just the hour part (HH:MM)
                String hourTime = time.substring(11, 16);
                
                // Get temperature
                int tempStart = hourData.indexOf("\"temp_c\":", timeEnd);
                if (tempStart == -1) break;
                tempStart += 9;
                int tempEnd = hourData.indexOf(",", tempStart);
                String temp = hourData.substring(tempStart, tempEnd).trim();
                
                // Get condition
                int condStart = hourData.indexOf("\"text\":\"", tempEnd);
                if (condStart == -1) break;
                condStart += 8;
                int condEnd = hourData.indexOf("\"", condStart);
                String condition = hourData.substring(condStart, condEnd);
                
                // Get chance of rain
                int rainStart = hourData.indexOf("\"chance_of_rain\":", condEnd);
                String chanceRain = "0";
                if (rainStart != -1) {
                    rainStart += 17;
                    int rainEnd = hourData.indexOf(",", rainStart);
                    if (rainEnd == -1) rainEnd = hourData.indexOf("}", rainStart);
                    chanceRain = hourData.substring(rainStart, rainEnd).trim();
                }
                
                // Format output
                display.append(String.format("%-8s │ %5s°C │ %s %-22s │ 💧 %3s%%\n", 
                    hourTime, temp, 
                    getWeatherIcon(condition),
                    condition.length() > 22 ? condition.substring(0, 19) + "..." : condition,
                    chanceRain));
                
                if (hourCount % 6 == 5 && hourCount < 23) {
                    display.append("─────────┼────────┼─────────────────────────────┼─────────\n");
                }
                
                startPos = condEnd;
                hourCount++;
            }
            
            hourlyDisplay.setText(display.toString());
            
        } catch (Exception e) {
            hourlyDisplay.setText("Error parsing hourly forecast: " + e.getMessage());
        }
    }
    
    private void displayWeeklyForecast(String jsonResponse) {
        try {
            // Extract city name
            String cityName = extractValue(jsonResponse, "\"name\":\"", "\"");
            
            StringBuilder display = new StringBuilder();
            display.append("╔════════════════════════════════════════════════════════╗\n");
            display.append("                  7-DAY WEATHER FORECAST\n");
            display.append("  📍 ").append(cityName).append("\n");
            display.append("╚════════════════════════════════════════════════════════╝\n\n");
            
            // Find the forecastday array
            int forecastIndex = jsonResponse.indexOf("\"forecastday\":[");
            if (forecastIndex == -1) {
                weeklyDisplay.setText("No forecast data available.");
                return;
            }
            
            String forecastData = jsonResponse.substring(forecastIndex);
            
            // Parse each day
            int startPos = 0;
            int dayCount = 0;
            
            while (dayCount < 7) {
                // Get date
                int dateStart = forecastData.indexOf("\"date\":\"", startPos);
                if (dateStart == -1) break;
                dateStart += 8;
                int dateEnd = forecastData.indexOf("\"", dateStart);
                String date = forecastData.substring(dateStart, dateEnd);
                
                // Get max temp
                int maxTempStart = forecastData.indexOf("\"maxtemp_c\":", dateEnd);
                if (maxTempStart == -1) break;
                maxTempStart += 12;
                int maxTempEnd = forecastData.indexOf(",", maxTempStart);
                String maxTemp = forecastData.substring(maxTempStart, maxTempEnd).trim();
                
                // Get min temp
                int minTempStart = forecastData.indexOf("\"mintemp_c\":", maxTempEnd);
                if (minTempStart == -1) break;
                minTempStart += 12;
                int minTempEnd = forecastData.indexOf(",", minTempStart);
                String minTemp = forecastData.substring(minTempStart, minTempEnd).trim();
                
                // Get condition
                int condStart = forecastData.indexOf("\"text\":\"", minTempEnd);
                if (condStart == -1) break;
                condStart += 8;
                int condEnd = forecastData.indexOf("\"", condStart);
                String condition = forecastData.substring(condStart, condEnd);
                
                // Get chance of rain
                int rainStart = forecastData.indexOf("\"daily_chance_of_rain\":", condEnd);
                String chanceRain = "0";
                if (rainStart != -1) {
                    rainStart += 23;
                    int rainEnd = forecastData.indexOf(",", rainStart);
                    if (rainEnd == -1) rainEnd = forecastData.indexOf("}", rainStart);
                    chanceRain = forecastData.substring(rainStart, rainEnd).trim();
                }
                
                // Format output
                display.append(String.format("%-12s │ %s %-18s\n", 
                    date, 
                    getWeatherIcon(condition),
                    condition.length() > 18 ? condition.substring(0, 15) + "..." : condition));
                display.append(String.format("             │ 🌡️  %s°C - %s°C  │  💧 %s%%\n\n",
                    minTemp, maxTemp, chanceRain));
                
                if (dayCount < 6) {
                    display.append("─────────────┴────────────────────────────────────────\n\n");
                }
                
                startPos = condEnd;
                dayCount++;
            }
            
            weeklyDisplay.setText(display.toString());
            
        } catch (Exception e) {
            weeklyDisplay.setText("Error parsing weekly forecast: " + e.getMessage());
        }
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        
        if (isDarkMode) {
            darkModeToggle.setText("☀️ Light Mode");
            applyDarkMode();
        } else {
            darkModeToggle.setText("🌙 Dark Mode");
            applyLightMode();
        }
    }
    
    private void applyDarkMode() {
        // Main panels
        Container contentPane = getContentPane();
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel) {
                applyDarkModeToPanel((JPanel) comp);
            }
        }
        
        // Text areas
        weatherDisplay.setBackground(darkTextArea);
        weatherDisplay.setForeground(darkText);
        hourlyDisplay.setBackground(darkTextArea);
        hourlyDisplay.setForeground(darkText);
        weeklyDisplay.setBackground(darkTextArea);
        weeklyDisplay.setForeground(darkText);
        
        // Favorites list
        favoritesList.setBackground(darkTextArea);
        favoritesList.setForeground(darkText);
        
        // City field
        cityField.setBackground(darkTextArea);
        cityField.setForeground(darkText);
        cityField.setCaretColor(darkText);
        
        // Tabbed pane
        tabbedPane.setBackground(darkBg);
        tabbedPane.setForeground(darkText);
        
        // Status label
        statusLabel.setForeground(darkText);
    }
    
    private void applyLightMode() {
        // Main panels
        Container contentPane = getContentPane();
        for (Component comp : contentPane.getComponents()) {
            if (comp instanceof JPanel) {
                applyLightModeToPanel((JPanel) comp);
            }
        }
        
        // Text areas
        weatherDisplay.setBackground(lightTextArea);
        weatherDisplay.setForeground(lightText);
        hourlyDisplay.setBackground(lightTextArea);
        hourlyDisplay.setForeground(lightText);
        weeklyDisplay.setBackground(lightTextArea);
        weeklyDisplay.setForeground(lightText);
        
        // Favorites list
        favoritesList.setBackground(lightTextArea);
        favoritesList.setForeground(lightText);
        
        // City field
        cityField.setBackground(Color.WHITE);
        cityField.setForeground(lightText);
        cityField.setCaretColor(lightText);
        
        // Tabbed pane
        tabbedPane.setBackground(lightBg);
        tabbedPane.setForeground(lightText);
        
        // Status label
        statusLabel.setForeground(new Color(100, 100, 100));
    }
    
    private void applyDarkModeToPanel(JPanel panel) {
        panel.setBackground(darkBg);
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                applyDarkModeToPanel((JPanel) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkText);
            }
        }
    }
    
    private void applyLightModeToPanel(JPanel panel) {
        panel.setBackground(lightBg);
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                applyLightModeToPanel((JPanel) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(lightText);
            }
        }
    }
    
    private String extractValue(String json, String startKey, String endChar) {
        int startIndex = json.indexOf(startKey);
        if (startIndex == -1) return "N/A";
        
        startIndex += startKey.length();
        int endIndex = json.indexOf(endChar, startIndex);
        
        if (endIndex == -1) return "N/A";
        
        return json.substring(startIndex, endIndex).trim();
    }
    
    private String getWeatherIcon(String condition) {
        String lower = condition.toLowerCase();
        
        // Sunny/Clear
        if (lower.contains("sunny") || lower.contains("clear")) {
            return "☀️";
        }
        // Partly cloudy
        else if (lower.contains("partly cloudy")) {
            return "⛅";
        }
        // Cloudy/Overcast
        else if (lower.contains("cloudy") || lower.contains("overcast")) {
            return "☁️";
        }
        // Rain
        else if (lower.contains("rain") || lower.contains("drizzle")) {
            if (lower.contains("heavy")) {
                return "🌧️";
            }
            return "🌦️";
        }
        // Thunderstorm
        else if (lower.contains("thunder") || lower.contains("storm")) {
            return "⛈️";
        }
        // Snow
        else if (lower.contains("snow") || lower.contains("blizzard")) {
            return "🌨️";
        }
        // Fog/Mist
        else if (lower.contains("fog") || lower.contains("mist") || lower.contains("haze")) {
            return "🌫️";
        }
        // Wind
        else if (lower.contains("wind")) {
            return "💨";
        }
        // Night
        else if (lower.contains("night")) {
            return "🌙";
        }
        // Default
        else {
            return "🌤️";
        }
    }
    
    private void addToFavorites() {
        String city = cityField.getText().trim();
        
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city name first!", 
                "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if already in favorites
        if (favorites.contains(city)) {
            JOptionPane.showMessageDialog(this, city + " is already in your favorites!", 
                "Already Added", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        favorites.add(city);
        favoritesModel.addElement(city);
        saveFavorites();
        statusLabel.setText("Added " + city + " to favorites");
    }
    
    private void removeFromFavorites() {
        String selected = favoritesList.getSelectedValue();
        
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a city to remove!", 
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Remove " + selected + " from favorites?", 
            "Confirm Removal", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            favorites.remove(selected);
            favoritesModel.removeElement(selected);
            saveFavorites();
            statusLabel.setText("Removed " + selected + " from favorites");
        }
    }
    
    private void saveFavorites() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FAVORITES_FILE))) {
            for (String city : favorites) {
                writer.write(city);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving favorites: " + e.getMessage());
        }
    }
    
    private void loadFavorites() {
        File file = new File(FAVORITES_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(FAVORITES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    favorites.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading favorites: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGUI());
    }
}
