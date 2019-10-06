package de.schildbach.wallet.rates;

import com.squareup.moshi.Json;

import java.math.BigDecimal;

/**
 * @author Samuel Barbosa
 */
public class CtpCasaResponse {

    @Json(name = "ctprate")
    private final BigDecimal ctpVesPrice;

    public CtpCasaResponse(BigDecimal ctpVesPrice) {
        this.ctpVesPrice = ctpVesPrice;
    }

    public BigDecimal getCtpVesPrice() {
        return ctpVesPrice;
    }

}
