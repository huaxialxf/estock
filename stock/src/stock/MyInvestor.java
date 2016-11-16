package stock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MyInvestor {
    public static InvestRecord createInvestRecord(InvestPolicy investPolicy, InvestPoint investPoint) {
        InvestRecord investRecord = new InvestRecord();
        if (investPoint.getListAllDateInfo().size() < investPolicy.getMinRow()) {// 是新股，退出
            return null;
        }
        if (investPoint.getListContinueFall().size() != investPolicy.getFallDays()) {
            return null;
        }
        if ((investPoint.getFallFound() - investPoint.getRiseFound()) / investPoint.getRiseFound() > investPolicy.getOutFund()) {
            investRecord.setDateInfo(investPoint.getStockDateInfo());
            investRecord.setInPrice(investPoint.getStockDateInfo().getClosePrice());
            investRecord.setIsfinshed(false);
            investRecord.setStockCode(investPoint.getStockCode());
            return investRecord;
        }
        return null;
    }

    public static void updateInvestRecord(InvestPolicy investPolicy, InvestRecord investRecord) {
        String stockCode = investRecord.getStockCode();
        if (stockCode.equals("SZ#000537.txt") && "2016/09/12".equals(investRecord.getDateInfo().getDate())) {
            // System.out.println(">>");
        }
        List<StockDateInfo> listData = StockInfoManager.getOne().getStockInfo(stockCode).getListData();
        int index = listData.indexOf(investRecord.getDateInfo());
        for (int i = 1; i <= investPolicy.getYieldDays(); i++) {
            if (index < i) {
                return;
            }
            StockDateInfo dateInfo = listData.get(index - i);
            if (dateInfo.getHightPrice() >= (1 + investPolicy.getYieldRate()) * investRecord.getInPrice()) {// 投资成功
                investRecord.setOutPrice((1 + investPolicy.getYieldRate()) * investRecord.getInPrice());
                investRecord.setIsfinshed(true);
                investRecord.setHoldDays(i);
                investRecord.setSuccess(true);
                investRecord.setProfit((investRecord.getOutPrice() - investRecord.getInPrice()) / investRecord.getInPrice());
                return;
            }
            // if (dateInfo.getClosePrice() <= (1 - investPolicy.getLossRate())
            // * investRecord.getInPrice()) {
            // investRecord.setOutPrice(dateInfo.getClosePrice());
            // investRecord.setIsfinshed(true);
            // investRecord.setHoldDays(i);
            // investRecord.setSuccess(false);
            // investRecord.setProfit(investPolicy.getInvestFound() *
            // (investRecord.getOutPrice() - investRecord.getInPrice()) /
            // investRecord.getInPrice());
            // return;
            // }
            if (dateInfo.getLowPrice() <= (1 - investPolicy.getLossRate()) * investRecord.getInPrice()) {
                investRecord.setOutPrice((1 - investPolicy.getLossRate()) * investRecord.getInPrice());
                investRecord.setIsfinshed(true);
                investRecord.setHoldDays(i);
                investRecord.setSuccess(false);
                investRecord.setProfit((investRecord.getOutPrice() - investRecord.getInPrice()) / investRecord.getInPrice());
                return;
            }
        }
        int i = investPolicy.getYieldDays();
        StockDateInfo dateInfo = listData.get(index - i);
        investRecord.setOutPrice(dateInfo.getClosePrice());
        investRecord.setIsfinshed(true);
        investRecord.setHoldDays(i);
        investRecord.setSuccess(false);
        investRecord.setProfit((investRecord.getOutPrice() - investRecord.getInPrice()) / investRecord.getInPrice());
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            test(20 * i, (i + 1) * 20);
        }
        test(0, 1000);
    }

    public static void test(int start, int end) throws Exception {
        List<String> listCode = StockInfoManager.getOne().getStockCodeList();
        List<InvestRecord> listRecord = new ArrayList<InvestRecord>();
        InvestPolicy investPolicy = new InvestPolicy();
        for (String stockCode : listCode) {

            for (int i = start; i < end; i++) {
                // for (int i = 0; i < nSample; i++) {
                InvestPoint investPoint = InvestPoint.CreateInvestPoint(stockCode, i);
                if (investPoint == null) {
                    break;
                }
                InvestRecord investRecord = createInvestRecord(investPolicy, investPoint);
                if (investRecord != null) {
                    listRecord.add(investRecord);
                }
            }
        }
        for (InvestRecord investRecord : listRecord) {
            updateInvestRecord(investPolicy, investRecord);
        }
        int nSucces = 0;
        int nFail = 0;
        int nRunning = 0;
        int nholdDays = 0;
        float profit = 0; // 盈利
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        // for (InvestRecord investRecord : listRecord) {
        // if (map.containsKey(investRecord.getDateInfo().getDate())) {
        // map.put(investRecord.getDateInfo().getDate(),
        // map.get(investRecord.getDateInfo().getDate()) + 1);
        // } else {
        // map.put(investRecord.getDateInfo().getDate(), 1);
        // }
        // }
        for (InvestRecord investRecord : listRecord) {
            if (map.containsKey(investRecord.getDateInfo().getDate())) {
                map.put(investRecord.getDateInfo().getDate(), map.get(investRecord.getDateInfo().getDate()) + 1);
            } else {
                map.put(investRecord.getDateInfo().getDate(), 1);
            }

            int n = map.get(investRecord.getDateInfo().getDate());
            if (n > 10) {
                continue;
            }

            if (investRecord.isSuccess() && investRecord.isIsfinshed()) {
                nholdDays = nholdDays + investRecord.getHoldDays();
                profit = profit + investRecord.getProfit();
                nSucces++;
            } else if (!investRecord.isSuccess() && investRecord.isIsfinshed()) {
                nholdDays = nholdDays + investRecord.getHoldDays();
                profit = profit + investRecord.getProfit();
                // System.err.println("投资失败>>>" + investRecord.getStockCode() +
                // "|" + investRecord.getDateInfo() + "|" +
                // investRecord.getProfit());
                nFail++;
            } else if (!investRecord.isIsfinshed()) {
                 System.out.println("投资中:" + investRecord.getStockCode() +
                 "-->" + investRecord.getDateInfo());
                nRunning++;
            } else {
                throw new Exception("状态不正确的投资记录");
            }
        }
        // int nTotal = listRecord.size();
        int nTotal = nSucces + nFail + nRunning;
        System.out.println(String.format("投资次数:%d  成功次数:%d  失败次数:%d 在投次数:%d 成功率%.2f 平均持仓天数:%.2f  盈利:%.4f 倍 平均盈利:%.4f 倍 ", nTotal, nSucces, nFail, nRunning, (nSucces * 100.0 / (nTotal - nRunning)), 1.0 * nholdDays / (nSucces + nFail),
                profit, profit / nTotal));
//        for (String key : map.keySet()) {
//            System.out.println(String.format("%s >>>> %d", key, map.get(key)));
//        }

    }
}
