package stock;

public class InvestPolicy {
    private int fallDays = 7;// 下跌天数
    private float outFund = 0.5f; // 流出资金比例
    private float yieldRate = 0.04f;// 收益率
    private int yieldDays = 6;// 最大持仓天数
    private int minRow = 15;// 最小记录数 //屏蔽掉新股
    private float lossRate = 0.02f;// 止损率
    private float investFound = 10000;// 单次投资资金
    
    

    public float getInvestFound() {
        return investFound;
    }

    public void setInvestFound(float investFound) {
        this.investFound = investFound;
    }

    public float getLossRate() {
        return lossRate;
    }

    public void setLossRate(float lossRate) {
        this.lossRate = lossRate;
    }

    public int getMinRow() {
        return minRow;
    }

    public void setMinRow(int minRow) {
        this.minRow = minRow;
    }

    public int getFallDays() {
        return fallDays;
    }

    public void setFallDays(int fallDays) {
        this.fallDays = fallDays;
    }

    public float getOutFund() {
        return outFund;
    }

    public void setOutFund(float outFund) {
        this.outFund = outFund;
    }

    public float getYieldRate() {
        return yieldRate;
    }

    public void setYieldRate(float yieldRate) {
        this.yieldRate = yieldRate;
    }

    public int getYieldDays() {
        return yieldDays;
    }

    public void setYieldDays(int yieldDays) {
        this.yieldDays = yieldDays;
    }

}
