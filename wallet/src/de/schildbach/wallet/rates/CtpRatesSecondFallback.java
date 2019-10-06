package de.schildbach.wallet.rates;

import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Samuel Barbosa
 */
public class CtpRatesSecondFallback implements ExchangeRatesClient {

    private static CtpRatesSecondFallback instance;
    private static final String VES_CURRENCY_CODE = "VES";
    private List<String> excludedRates = Arrays.asList("BTC", "BCH", "XAG", "XAU", "VEF");

    public static CtpRatesSecondFallback getInstance() {
        if (instance == null) {
            instance = new CtpRatesSecondFallback();
        }
        return instance;
    }

    private CtpRatesSecondFallback() {

    }

    @Nullable
    @Override
    public List<ExchangeRate> getRates() throws Exception {
        List<BitPayRate> rates = BitPayClient.getInstance().getRates().body().getRates();
        BigDecimal ctpCentralPrice = CtpCentralClient.getInstance().getCtpBtcPrice().body().getRate();
        BigDecimal poloniexPrice = PoloniexClient.getInstance().getRates().body().getRate();
        BigDecimal ctpVesPrice = LocalBitcoinsClient.getInstance().getRates().body().getCtpVesPrice();

        if (rates == null || rates.isEmpty() || (ctpCentralPrice == null && poloniexPrice == null)) {
            throw new IllegalStateException("Failed to fetch prices from Fallback2");
        }

        BigDecimal ctpBtcRate = null;
        if (poloniexPrice.compareTo(BigDecimal.ZERO) > 0) {
            if (ctpCentralPrice.compareTo(BigDecimal.ZERO) > 0) {
                ctpBtcRate = ctpCentralPrice.add(poloniexPrice).divide(BigDecimal.valueOf(2));
            } else {
                ctpBtcRate = poloniexPrice;
            }
        } else if (ctpCentralPrice.compareTo(BigDecimal.ZERO) > 0) {
            ctpBtcRate = ctpCentralPrice;
        }

        List<ExchangeRate> exchangeRates = new ArrayList<>();
        for(BitPayRate rate : rates) {
            if (!excludedRates.contains(rate.getCode())) {
                if (VES_CURRENCY_CODE.equalsIgnoreCase(rate.getCode()) && ctpVesPrice != null
                        && ctpVesPrice.compareTo(BigDecimal.ZERO) > 0) {
                    exchangeRates.add(new ExchangeRate(rate.getCode(), ctpVesPrice.toPlainString()));
                    continue;
                }
                exchangeRates.add(new ExchangeRate(rate.getCode(),
                        ctpBtcRate.multiply(rate.getRate()).toPlainString()));
            }
        }

        return exchangeRates;
    }

}
