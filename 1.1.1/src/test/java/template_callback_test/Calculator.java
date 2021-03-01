package template_callback_test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filepath) throws IOException {
        LineCallback sumCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filepath, sumCallback, 0);
    }
    public Integer calcMult(String numFilePath) throws IOException {
        LineCallback mulCallback = new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.valueOf(line);
            }
        };
        return lineReadTemplate(numFilePath, mulCallback, 1);
    }
    public String concentrate(String filePath) throws IOException {
        LineCallback concatCallback = new LineCallback() {
            @Override
            public Object doSomethingWithLine(String line, Object value) {
                return value + line;
            }
        };
        return lineReadTemplate(filePath, concatCallback, "");
    }
//    public Integer calcMult(String numFilePath) throws IOException {
//        BufferedReaderCallback mulCallback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer res = 1;
//                String line = null;
//                while((line = br.readLine()) != null) {
//                    res *= Integer.valueOf(line);
//                }
//                return res;
//            }
//        };
//        return this.fileReadTemplate(numFilePath, mulCallback);
//    }
    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            int res = callback.doSomethingWithReader(br);
            return res;

        } catch (IOException e){
            System.out.println("e.getMessage() = " + e.getMessage());
            throw e;
        } finally {
            if(br != null){
                try{
                    br.close();
                } catch (IOException e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                }
            }
        }
    }
    public <T> T lineReadTemplate(String filepath, LineCallback<T> lineCallback, T initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line = null;
            while((line = br.readLine()) != null){
                res = lineCallback.doSomethingWithLine(line, res);
            }
            return res;

        } catch (IOException e){
            System.out.println("e.getMessage() = " + e.getMessage());
            throw e;
        } finally {
            if(br != null){
                try{
                    br.close();
                } catch (IOException e) {
                    System.out.println("e.getMessage() = " + e.getMessage());
                }
            }
        }
    }
}
