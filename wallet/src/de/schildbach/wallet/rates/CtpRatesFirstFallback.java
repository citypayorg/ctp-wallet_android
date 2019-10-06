package de.schildbach.wallet.rates;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Samuel Barbosa
 */
public class CtpRatesFirstFallback implements ExchangeRatesClient {

    private static CtpRatesFirstFallback instance;
    private static final String VES_CURRENCY_CODE = "VES";

    public static CtpRatesFirstFallback getInstance() {
        if (instance == null) {
            instance = new CtpRatesFirstFallback();
        }
        return instance;
    }

    private CtpRatesFirstFallback() {

    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        BitcoinAverageClient btcAvgClient = BitcoinAverageClient.getInstance();
        CryptoCompareClient cryptoCompareClient = CryptoCompareClient.getInstance();

        List<ExchangeRate> rates = btcAvgClient.getGlobalIndices().body();
        Rate ctpBtcRate = cryptoCompareClient.getCtpCustomAverage().body();

        BigDecimal ctpVesPrice = CtpCasaClient.getInstance().getRates().body().getCtpVesPrice();

        if (rates == null || rates.isEmpty() || ctpBtcRate == null || ctpVesPrice == null) {
            throw new IllegalStateException("Failed to fetch prices from Fallback1");
        }

        boolean vesRateExists = false;
        for (ExchangeRate rate : rates) {
            if (VES_CURRENCY_CODE.equalsIgnoreCase(rate.getCurrencyCode())) {
                vesRateExists = true;
                if (ctpVesPrice.compareTo(BigDecimal.ZERO) > 0) {
                    rate.setRate(ctpVesPrice.toPlainString());
                }
            } else {
                BigDecimal currencyBtcRate = new BigDecimal(rate.getRate());
                rate.setRate(currencyBtcRate.multiply(ctpBtcRate.getRate()).toPlainString());
            }
        }
        if (!vesRateExists) {
            rates.add(new ExchangeRate(VES_CURRENCY_CODE, ctpVesPrice.toPlainString()));
        }

        return rates;
    }

}
