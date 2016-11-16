package stock;

public class InvestRecord {
    private StockDateInfo dateInfo;
    private String stockCode;
    private int holdDays = 0;// 持仓天数
    private float profit = 0; // 盈利
    private boolean isSuccess; // 是否成功
    private boolean isfinshed = false; // 投资是否结束
    private float inPrice = 0;
    private float outPrice = 0;

    public float getInPrice() {
        return inPrice;
    }

    public void setInPrice(float inPrice) {
        this.inPrice = inPrice;
    }

    public float getOutPrice() {
        return outPrice;
    }

    public void setOutPrice(float outPrice) {
        this.outPrice = outPrice;
    }

    public boolean isIsfinshed() {
        return isfinshed;
    }

    public void setIsfinshed(boolean isfinshed) {
        this.isfinshed = isfinshed;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public int getHoldDays() {
        return holdDays;
    }

    public void setHoldDays(int holdDays) {
        this.holdDays = holdDays;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public StockDateInfo getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(StockDateInfo dateInfo) {
        this.dateInfo = dateInfo;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

}
