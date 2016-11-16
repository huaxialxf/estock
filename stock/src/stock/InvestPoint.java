package stock;

import java.util.ArrayList;
import java.util.List;

public class InvestPoint {
    private String stockCode;// 股票代码
    private StockDateInfo stockDateInfo;
    private int index; // 索引号
    private boolean isFall; // 是否下跌

    private float riseFound = 0f;// 上涨资金
    private float fallFound = 0f;// 下跌资金

    private List<StockDateInfo> listContinueFall = new ArrayList<StockDateInfo>();// 连续下跌列表
    private List<StockDateInfo> listContinueRise = new ArrayList<StockDateInfo>();// 连续上涨列表
    private List<StockDateInfo> listAllDateInfo;// 所有的股票记录

    public static InvestPoint CreateInvestPoint(String stockCode, int index) {
        try {
            InvestPoint investPoint = new InvestPoint(stockCode, index);
            return investPoint;
        } catch (Exception e) {
            return null;
        }

    }

    public InvestPoint(String stockCode, int index) throws Exception {
        this.stockCode = stockCode;
        this.index = index;
        this.listAllDateInfo = StockInfoManager.getOne().getStockInfo(stockCode).getListData();
        if (index >= listAllDateInfo.size()) {
            throw new Exception("index erro");
        }
        this.stockDateInfo = listAllDateInfo.get(index);
        this.isFall = stockDateInfo.getClosePrice() < stockDateInfo.getOpenPrice();// 是否上涨
        if (isFall) {
            int n = index;
            while (n < listAllDateInfo.size()) {
                StockDateInfo dateInfo = listAllDateInfo.get(n);
                if (dateInfo.getClosePrice() < dateInfo.getOpenPrice()) {
                    listContinueFall.add(dateInfo);
                } else {
                    break;
                }
                n++;
            }
            while (n < listAllDateInfo.size()) {
                StockDateInfo dateInfo = listAllDateInfo.get(n);
                if (dateInfo.getClosePrice() >= dateInfo.getOpenPrice()) {
                    listContinueRise.add(dateInfo);
                } else {
                    break;
                }
                n++;
            }
        }
        for (StockDateInfo stockDateInfo : listContinueFall) {
            fallFound = fallFound + (stockDateInfo.getOpenPrice() - stockDateInfo.getClosePrice()) * stockDateInfo.getVolume();
        }
        for (StockDateInfo stockDateInfo : listContinueRise) {
            riseFound = riseFound + (stockDateInfo.getClosePrice() - stockDateInfo.getOpenPrice()) * stockDateInfo.getVolume();
        }
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public StockDateInfo getStockDateInfo() {
        return stockDateInfo;
    }

    public void setStockDateInfo(StockDateInfo stockDateInfo) {
        this.stockDateInfo = stockDateInfo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isFall() {
        return isFall;
    }

    public void setFall(boolean isFall) {
        this.isFall = isFall;
    }

    public float getRiseFound() {
        return riseFound;
    }

    public void setRiseFound(float riseFound) {
        this.riseFound = riseFound;
    }

    public float getFallFound() {
        return fallFound;
    }

    public void setFallFound(float fallFound) {
        this.fallFound = fallFound;
    }

    public List<StockDateInfo> getListContinueFall() {
        return listContinueFall;
    }

    public void setListContinueFall(List<StockDateInfo> listContinueFall) {
        this.listContinueFall = listContinueFall;
    }

    public List<StockDateInfo> getListContinueRise() {
        return listContinueRise;
    }

    public void setListContinueRise(List<StockDateInfo> listContinueRise) {
        this.listContinueRise = listContinueRise;
    }

    public List<StockDateInfo> getListAllDateInfo() {
        return listAllDateInfo;
    }

    public void setListAllDateInfo(List<StockDateInfo> listAllDateInfo) {
        this.listAllDateInfo = listAllDateInfo;
    }
    public static void main(String[] args) throws Exception {
        InvestPoint investPoint = new InvestPoint("SH#600522.txt", 0);
    }
}
