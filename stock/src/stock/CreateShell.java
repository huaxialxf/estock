package stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class CreateShell {
    public static void main(String[] args) throws IOException {

        FileOutputStream fos = new FileOutputStream(new File("download.bat"));
        BufferedReader reader = new BufferedReader(new FileReader(new File("stock_basics.csv")));
        String line = reader.readLine();
        while ((line = reader.readLine()) != null) {
            String arr[] = line.split(",");
            String code = arr[0];
            String command = String.format("python F:/stock_study/py/get_stock_info.py %s F:/stock_study/tushare/%s.csv", code, code);
            System.out.println(command);
            fos.write(command.getBytes());
            fos.write("\n".getBytes());
        }
        reader.close();
        fos.close();

    }
}
