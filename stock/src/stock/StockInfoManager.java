package stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockInfoManager {
    private static StockInfoManager one = null;

    public static StockInfoManager getOne() {
        if (one == null) {
            one = new StockInfoManager();
        }
        return one;
    }

    private StockInfoManager() {
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() throws IOException {
        // File file = new File("F:/stock_study/tushare");
        File file = new File("F:/stock_study/tdx_data/export");
        File[] files = file.listFiles();
        for (File subFile : files) {
            StockInfo stockInfo = new StockInfo();
            stockInfo.parseFile(subFile);
            map.put(subFile.getName(), stockInfo);
        }
    }

    public StockInfo getStockInfo(String stockFileName) {
        return map.get(stockFileName);
    }

    private Map<String, StockInfo> map = new HashMap<String, StockInfo>();

    public List<String> getStockCodeList() throws IOException {

        List<String> list = new ArrayList<String>();
        for (String key : map.keySet()) {
            list.add(key);
        }
        return list ;
    }

    public static List<String> getStockCodeListForTushare() throws IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(new File("stock_basics.csv")));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String arr[] = line.split(",");
            String code = arr[0];
            list.add(code);
        }
        reader.close();
        return list;
    }
}
