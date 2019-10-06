package de.schildbach.wallet.rates;

import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import java.util.List;

import retrofit2.Call;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class CtpRatesClient extends RetrofitClient implements ExchangeRatesClient {

    private static CtpRatesClient instance;

    public static CtpRatesClient getInstance() {
        if (instance == null) {
            instance = new CtpRatesClient();
        }
        return instance;
    }

    private CtpRatesService ctpRatesService;

    private CtpRatesClient() {
        super("https://api.get-spark.com/");
        Moshi moshi = moshiBuilder.add(new ExchangeRateListMoshiAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        ctpRatesService = retrofit.create(CtpRatesService.class);
    }

    @Override
    @Nullable
    public List<ExchangeRate> getRates() throws Exception {
        List<ExchangeRate> rates = ctpRatesService.getRates().execute().body();
        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Failed to fetch prices from CtpRates source");
        }
        return rates;
    }

    private interface CtpRatesService {
        @GET("list")
        Call<List<ExchangeRate>> getRates();
    }

}
