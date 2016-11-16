package stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockInfo {
    private List<StockDateInfo> listData = new ArrayList<StockDateInfo>();

    public List<StockDateInfo> getListData() {
        return listData;
    }

    public void setListData(List<StockDateInfo> listData) {
        this.listData = listData;
    }

    public StockInfo() {
    }

    public void parseFile(File file) throws IOException {
        // if ( file.getName().equals("SZ#000929.txt") ) {
        if (true) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("2016")) {
                    StockDateInfo dateInfo = new StockDateInfo(line);
                    if (/* dateInfo.getDate().startsWith("2011") */true) {
                        listData.add(dateInfo);
                    }
                }
            }
            reader.close();
            Collections.reverse(listData);

        }
    }

    public static void main(String[] args) throws IOException {
        StockInfo info = new StockInfo();
        info.parseFile(new File("C:/new_tdx/T0002/export/SH#600000.txt"));
        for (StockDateInfo dateInfo : info.getListData()) {
            System.out.println(dateInfo);
        }
    }

}
