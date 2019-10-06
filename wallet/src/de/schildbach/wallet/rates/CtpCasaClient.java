package de.schildbach.wallet.rates;

import com.squareup.moshi.Moshi;

import org.ctp.wallet.common.data.BigDecimalAdapter;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.moshi.MoshiConverterFactory;
import retrofit2.http.GET;

/**
 * @author Samuel Barbosa
 */
public class CtpCasaClient extends RetrofitClient {

    private static CtpCasaClient instance;
    private CtpCasaService service;

    public static CtpCasaClient getInstance() {
        if (instance == null) {
            instance = new CtpCasaClient("https://ctp.casa/");
        }
        return instance;
    }

    private CtpCasaClient(String baseUrl) {
        super(baseUrl);

        Moshi moshi = moshiBuilder.add(new BigDecimalAdapter()).build();
        retrofit = retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi)).build();
        service = retrofit.create(CtpCasaService.class);
    }

    public Response<CtpCasaResponse> getRates() throws IOException {
        return service.getRates().execute();
    }

    private interface CtpCasaService {
        @GET("api/?cur=VES")
        Call<CtpCasaResponse> getRates();
    }

}
