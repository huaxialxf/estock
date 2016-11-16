package stock;

public class StockDateInfo {
    private String date; // 日期
    private float openPrice;// 开盘价
    private float closePrice;// 收盘价
    private float hightPrice;// 最高价
    private float lowPrice;// 最低价
    private float volume;// 成交量

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(float openPrice) {
        this.openPrice = openPrice;
    }

    public float getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(float closePrice) {
        this.closePrice = closePrice;
    }

    public float getHightPrice() {
        return hightPrice;
    }

    public void setHightPrice(float hightPrice) {
        this.hightPrice = hightPrice;
    }

    public float getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(float lowPrice) {
        this.lowPrice = lowPrice;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public StockDateInfo(String line){
        this.parse(line);
    }

    private void parseForTushare(String line) {
        String[] arr = line.split(",");
        this.date = arr[0];
        this.openPrice = Float.parseFloat(arr[1]);// 开盘价
        this.hightPrice = Float.parseFloat(arr[2]);// 最高价
        this.closePrice = Float.parseFloat(arr[3]);// 收盘价
        this.lowPrice = Float.parseFloat(arr[4]);// 最低价
        this.volume =  Float.parseFloat(arr[5]);// 成交量
    }
    

    private void parse(String line) {
        String[] arr = line.split("\t");
        this.date = arr[0];
        this.openPrice = Float.parseFloat(arr[1]);// 开盘价
        this.hightPrice = Float.parseFloat(arr[2]);// 最高价
        this.lowPrice = Float.parseFloat(arr[3]);// 最低价
        this.closePrice = Float.parseFloat(arr[4]);// 收盘价
        this.volume =  Float.parseFloat(arr[5]);// 成交量
    }

    public String toString() {
        return String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f", this.date, this.openPrice, this.hightPrice, this.closePrice,  this.lowPrice, this.volume);
    }
    public static void main(String[] args) {
        String line = "2011/10/24 8.91 9.21 8.87 9.16 94932084 864185472.00";
        System.out.println(new StockDateInfo(line).toString());
    }
}
