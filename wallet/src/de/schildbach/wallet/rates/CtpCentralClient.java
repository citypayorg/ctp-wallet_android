package de.schildbach.wallet.rates;

import com.squareup.moshi.Moshi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class CtpCentralClient extends RetrofitClient {

    private static CtpCentralClient instance;

    public static CtpCentralClient getInstance() {
        if (instance == null) {
            instance = new CtpCentralClient("https://www.ctpcentral.org/");
        }
        return instance;
    }

    private CtpCentralService service;

    private CtpCentralClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new CtpCentralRateAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(CtpCentralService.class);
    }

    public Response<Rate> getCtpBtcPrice() throws IOException {
        return service.getCtpBtcPrice().execute();
    }

    private interface CtpCentralService {
        @GET("api/v1/public")
        Call<Rate> getCtpBtcPrice();
    }

}
