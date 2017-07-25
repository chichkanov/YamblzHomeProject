package dvinc.yamblzhomeproject.ui.weather;
/*
 * Created by DV on Space 5 
 * 19.07.2017
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dvinc.yamblzhomeproject.R;
import dvinc.yamblzhomeproject.repository.model.WeatherResponse;

import static android.content.Context.MODE_PRIVATE;

public class MvpWeatherFragment extends Fragment implements WeatherView {

    @BindView(R.id.getDataButton)
    Button getDataButton;
    @BindView(R.id.temperatureTextView)
    TextView temperatureTextView;
    @BindView(R.id.tempMaxTextView)
    TextView tempMaxTextView;
    @BindView(R.id.tempMinTextView)
    TextView tempMinTextView;
    @BindView(R.id.pressureTextView)
    TextView pressureTextView;
    @BindView(R.id.humidityTextView)
    TextView humidityTextVIew;
    @BindView(R.id.lastUpdateWeatherTextView)
    TextView lastUpdateWeatherTextView;
    @BindView(R.id.visibilityTextView)
    TextView visibilityTextView;
    @BindView(R.id.windTextView)
    TextView windTextView;
    public static final String SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME";

    public WeatherPresenterImpl<WeatherView> weatherPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather,
                container, false);
        ButterKnife.bind(this, view);
        weatherPresenter = new WeatherPresenterImpl<>();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        weatherPresenter.attachView(this);
        weatherPresenter.getWeatherDataFromCache(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        weatherPresenter.detachView();
    }

    /**
     * Method for getting new data from Repository (From internet).
     */
    @OnClick(R.id.getDataButton)
    public void getData() {
        weatherPresenter.getWeatherFromInternet(getContext());
    }

    /**
     * Method for updating ui.
     *
     * @param weatherData weather data from Repository.
     */
    @Override
    public void updateWeatherParameters(WeatherResponse weatherData) {
        SharedPreferences str = getActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        long lastUpdateTime = str.getLong("LAST UPDATE TIME", 0L);

        if (weatherData != null) {
            int temp = (int) (weatherData.getMain().getTemp() - 273);
            int tempMax = (int) (weatherData.getMain().getTempMax() - 273);
            int tempMin = (int) (weatherData.getMain().getTempMin() - 273);
            int pressure = (int) (weatherData.getMain().getPressure() * 0.75f);
            int humidity = weatherData.getMain().getHumidity();
            float visibility = weatherData.getVisibility() / 1000;
            float wind = weatherData.getWind().getSpeed();

            String temperature = temp + getResources().getString(R.string.temperature_celsius);
            String temperatureMax = tempMax + getResources().getString(R.string.temperature_celsius);
            String temperatureMin = tempMin + getResources().getString(R.string.temperature_celsius);
            String pressureString = pressure + getResources().getString(R.string.pressure_unit);
            String humidityString = humidity + getResources().getString(R.string.humidity_unit);
            String visibilityString = visibility + getResources().getString(R.string.visibility_unit);
            String windString = wind + getResources().getString(R.string.wind_speed_unit);

            Date date = new Date(lastUpdateTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()); // the format of your date
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+3")); // give a timezone reference for formating (see comment at the bottom
            String formattedDate = sdf.format(date);

            temperatureTextView.setText(temperature);
            tempMaxTextView.setText(temperatureMax);
            tempMinTextView.setText(temperatureMin);
            pressureTextView.setText(pressureString);
            humidityTextVIew.setText(humidityString);
            lastUpdateWeatherTextView.setText(formattedDate);
            visibilityTextView.setText(visibilityString);
            windTextView.setText(windString);
        }
    }

    @Override
    public void showError(String string) {
        Toast.makeText(getContext(), string, Toast.LENGTH_LONG).show();
    }
}