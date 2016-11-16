package stock;

import java.util.ArrayList;
import java.util.List;

public class InvestRobot {
    private List<InvestRecord> listInvestRecord = new ArrayList<InvestRecord>();

    private InvestPolicy investPolicy = new InvestPolicy();

    public void invest(String stockCode, int index) {
        // StockInfo stockInfo =
        // StockInfoManager.getOne().getStockInfo(stockCode + ".csv");

        StockInfo stockInfo = StockInfoManager.getOne().getStockInfo(stockCode);
        if (stockInfo == null) {
            return;
        }
        if (stockInfo.getListData().size() < investPolicy.getMinRow()) {// 是新股，退出
            return;
        }
        float fallFound = 0;
        int indexFall = -1;
        List<StockDateInfo> listContinueFall = new ArrayList<StockDateInfo>();
        List<StockDateInfo> listData = stockInfo.getListData();
        for (int i = index; i < listData.size(); i++) {
            StockDateInfo dateInfo = listData.get(i);
            if (dateInfo.getClosePrice() < dateInfo.getOpenPrice()) {
                listContinueFall.add(dateInfo);
                fallFound = fallFound + dateInfo.getVolume() * (dateInfo.getOpenPrice() - dateInfo.getClosePrice());
                indexFall = i;
            } else {
                indexFall = i;
                break;
            }
        }
        if (listContinueFall.size() != investPolicy.getFallDays()) { // 下跌数量不够，直接退出。
            return;
        }

        List<StockDateInfo> listContinueRise = new ArrayList<StockDateInfo>();
        float riseFound = 0;
        for (int i = indexFall; i < listData.size(); i++) {
            try {
                StockDateInfo dateInfo = listData.get(i);
                if (dateInfo.getClosePrice() >= dateInfo.getOpenPrice()) {
                    listContinueRise.add(dateInfo);
                    riseFound = riseFound + dateInfo.getVolume() * (dateInfo.getClosePrice() - dateInfo.getOpenPrice());
                } else {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println(stockCode + "   index =" + i);
            }
        }
        boolean checked = (fallFound - riseFound) / riseFound > investPolicy.getOutFund();

        if (checked) {// 投资了
            
            InvestRecord record = new InvestRecord();
            record.setStockCode(stockCode);
            record.setDateInfo(stockInfo.getListData().get(index));
            float investPrice = record.getDateInfo().getClosePrice();
            record.setInPrice(investPrice);
            
            if(stockCode.equals("SZ#000537.txt") && "2016/09/12".equals(record.getDateInfo().getDate())){
//                System.out.println(">>");
            }
            
            int nHoldDays = 0;
            StockDateInfo curStockDateInfo = null;
            for (int i = 0; i < investPolicy.getYieldDays(); i++) {
                if (index - 1 - i < 0) {// 天数不够
                    break;
                }
                curStockDateInfo = listData.get(index - 1 - i);
                nHoldDays++;
                if (curStockDateInfo.getHightPrice() >= investPrice * (1 + investPolicy.getYieldRate())) {// 成功了
                    record.setSuccess(true);// 投资成功
                    record.setIsfinshed(true);
                    record.setHoldDays(nHoldDays);
                    record.setOutPrice(investPrice * (1 + investPolicy.getYieldRate()));
                    break;
                }
                if (curStockDateInfo.getClosePrice() <= investPrice * (1 - investPolicy.getLossRate())) {// 价格止损了
                    record.setSuccess(false);// 投资失败
                    record.setIsfinshed(true);
                    record.setHoldDays(nHoldDays);
                    record.setOutPrice(curStockDateInfo.getClosePrice());
                    // System.err.println("价格止损:"+ stockCode +">>>>>" +
                    // curStockDateInfo +">>>>"+(record.getOutPrice() -
                    // record.getInPrice()) / record.getOutPrice());
                    break;
                }
            }
            if (nHoldDays == investPolicy.getYieldDays() && !record.isIsfinshed()) {// 天数止损
                record.setSuccess(false);// 投资失败
                record.setIsfinshed(true);
                record.setHoldDays(nHoldDays);
                record.setOutPrice(curStockDateInfo.getClosePrice());
                // System.err.println("时间止损:"+ stockCode +">>>>>" +
                // curStockDateInfo +">>>>"+(record.getOutPrice() -
                // record.getInPrice()) / record.getOutPrice());
            }
            if (record.isIsfinshed()) {
                record.setProfit((record.getOutPrice() - record.getInPrice()) / record.getInPrice());
            }
            listInvestRecord.add(record);
        }
    }

    public List<InvestRecord> getListInvestRecord() {
        return listInvestRecord;
    }

    public void setListInvestRecord(List<InvestRecord> listInvestRecord) {
        this.listInvestRecord = listInvestRecord;
    }

    public InvestPolicy getInvestPolicy() {
        return investPolicy;
    }

    public void setInvestPolicy(InvestPolicy investPolicy) {
        this.investPolicy = investPolicy;
    }

    public static void main(String[] args) throws Exception {
         for (int i = 0; i < 10; i++) {
         test((i + 1) * 20);
         }
        test(40);
    }

    public static void test(int nSample) throws Exception {

        List<String> listCode = StockInfoManager.getOne().getStockCodeList();
        InvestRobot investRobot = new InvestRobot();
        for (String stockCode : listCode) {
            try {
                for (int i = 0; i < nSample; i++) {
                    investRobot.invest(stockCode, i);
                }
            } catch (Exception e) {
                System.err.println(" 错误：stockCode:" + stockCode);
                e.printStackTrace();
            }
        }
        List<InvestRecord> listRecord = investRobot.getListInvestRecord();
        int nSucces = 0;
        int nFail = 0;
        int nRunning = 0;
        int nholdDays = 0;
        float profit = 0; // 盈利
        for (InvestRecord investRecord : listRecord) {
            if (investRecord.isSuccess() && investRecord.isIsfinshed()) {
                nholdDays = nholdDays + investRecord.getHoldDays();
                profit = profit + investRecord.getProfit();
                nSucces++;
            } else if (!investRecord.isSuccess() && investRecord.isIsfinshed()) {
                nholdDays = nholdDays + investRecord.getHoldDays();
                profit = profit + investRecord.getProfit();
//                System.err.println("投资失败>>>"+investRecord.getStockCode() +"|"+investRecord.getDateInfo());
                nFail++;
            } else if (!investRecord.isIsfinshed()) {
//                System.out.println("投资中:" + investRecord.getStockCode() + "-->" + investRecord.getDateInfo());
                nRunning++;
            } else {
                throw new Exception("状态不正确的投资记录");
            }
        }
        int nTotal = listRecord.size();
        System.out.println(String.format("投资次数:%d  成功次数:%d  失败次数:%d 在投次数:%d 成功率%.2f 平均持仓天数:%.2f  盈利:%.2f 倍 ", nTotal, nSucces, nFail, nRunning, (nSucces * 100.0 / (nTotal - nRunning)), 1.0 * nholdDays / (nSucces + nFail), profit / 10));
    }

    // public static void main2(String[] args) throws Exception {
    // List<String> listCode = StockInfoManager.getOne().getStockCodeList();
    // InvestRobot investRobot = new InvestRobot();
    // for (String stockCode : listCode) {
    // if (investRobot.canInvest(stockCode, 0)) {
    // System.out.println(stockCode);
    // }
    // }
    // }

    // public static void main1(String[] args) {
    // InvestRobot investRobot = new InvestRobot();
    // investRobot.canInvest("SH#601633.txt", 0);
    // }
}
