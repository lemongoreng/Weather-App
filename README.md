# ☀️ Weather App

A feature-rich Java Swing weather application that displays current weather, hourly forecasts, and 7-day forecasts with a beautiful dark mode.

![Weather App Screenshot](screenshot.png)

## ✨ Features

- 🌤️ **Current Weather Display** - Real-time weather with visual icons
- 🕐 **24-Hour Forecast** - Hourly weather breakdown for the day
- 📅 **7-Day Forecast** - Weekly weather outlook
- ⭐ **Favorite Cities** - Save and quickly access your favorite locations
- 🌙 **Dark Mode** - Toggle between light and dark themes
- 📍 **Local Time & Date** - Shows weather in the city's local timezone
- 🌡️ **Metric Units** - Temperature in Celsius, wind in km/h

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Internet connection

### Setup

1. **Clone the repository**
```bash
   git clone https://github.com/YOUR_USERNAME/weather-app-java.git
   cd weather-app-java
```

2. **Get a free API key**
   - Go to [WeatherAPI.com](https://www.weatherapi.com/)
   - Sign up for a free account
   - Copy your API key from the dashboard

3. **Add your API key**
   - Open `WeatherAppGUI.java`
   - Find line 12: `private static final String API_KEY = "YOUR_API_KEY_HERE";`
   - Replace `YOUR_API_KEY_HERE` with your actual API key

4. **Compile and run**
```bash
   javac WeatherAppGUI.java
   java WeatherAppGUI
```

## 📖 How to Use

1. **Search for weather**: Enter a city name and click "Get Weather" or press Enter
2. **Add favorites**: Click "⭐ Add to Favorites" to save a city
3. **Quick access**: Double-click any favorite city to load its weather
4. **View forecasts**: Switch between Current, Hourly, and 7-Day tabs
5. **Toggle theme**: Click "🌙 Dark Mode" to switch between light and dark themes

## 🛠️ Technologies Used

- **Java Swing** - GUI framework
- **Java HttpClient** - HTTP requests
- **WeatherAPI.com** - Weather data provider

## 📝 API Information

This app uses the free tier of [WeatherAPI.com](https://www.weatherapi.com/) which provides:
- 1,000,000 calls per month
- Current weather data
- 7-day forecast
- Hourly forecast
- No credit card required

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests

## 📄 License

This project is open source and available under the MIT License.

## 👤 Author

Eamonn Nathanael - [lemongoreng](https://github.com/lemongoreng)

## 🙏 Acknowledgments

- Weather data provided by [WeatherAPI.com](https://www.weatherapi.com/)
- Icons and emojis from Unicode standard