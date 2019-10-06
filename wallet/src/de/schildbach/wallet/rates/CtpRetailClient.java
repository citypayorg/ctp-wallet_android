package de.schildbach.wallet.rates;

import android.support.annotation.Nullable;

import com.squareup.moshi.Moshi;

import org.ctp.wallet.common.data.BigDecimalAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

public class CtpRetailClient extends RetrofitClient implements ExchangeRatesClient {

    private static final String CTP_CURRENCY_SYMBOL = "CTP";

    private static CtpRetailClient instance;

    public static CtpRetailClient getInstance() {
        if (instance == null) {
            instance = new CtpRetailClient("https://rates2.ctpretail.org/");
        }
        return instance;
    }

    private CtpRetailService service;

    private CtpRetailClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new BigDecimalAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(CtpRetailService.class);
    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        Response<List<CtpRetailRate>> response = service.getRates().execute();
        List<CtpRetailRate> rates = response.body();

        if (rates == null || rates.isEmpty()) {
            throw new IllegalStateException("Failed to fetch prices from CtpRetail");
        }

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for (CtpRetailRate rate : rates) {
            if (CTP_CURRENCY_SYMBOL.equals(rate.getBaseCurrency())) {
                exchangeRates.add(new ExchangeRate(rate.getQuoteCurrency(), rate.getPrice().toPlainString()));
            }
        }

        return exchangeRates;
    }

    private interface CtpRetailService {
        @GET("rates?source=ctpretail")
        Call<List<CtpRetailRate>> getRates();
    }

}
