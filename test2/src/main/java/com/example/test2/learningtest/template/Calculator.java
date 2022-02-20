package com.example.test2.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer fileReadTemplate(String filepath, BufferReaderCallback callback) throws IOException{
        BufferedReader br = null;

        try {
            br = new BufferedReader((new FileReader(filepath)));

            Integer num = callback.doSomethingWithReader(br);

            br.close();
            return num;
        }catch (IOException e ){
            throw e;
        }finally {
            if(br!=null){
                try{ br.close();}
                catch (IOException e){System.out.println(e.getMessage());}
            }
        }
    }

    public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal)throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader((new FileReader(filepath)));
            T res = initVal   ;
            String line = null;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line,res);
            }

            br.close();
            return res;
        }catch (IOException e ){
            throw e;
        }finally {
            if(br!=null){
                try{ br.close();}
                catch (IOException e){System.out.println(e.getMessage());}
            }
        }
    }

    public Integer calcSum(String filepath)throws IOException{

        return lineReadTemplate(filepath, new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.valueOf(line);
            }
        },0);
    }

    public Integer calcMultiply(String filepath)throws IOException{

        return lineReadTemplate(filepath, new LineCallback<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.valueOf(line);
            }
        },1);
    }

    public String concatenate(String filepath)throws IOException{
        return lineReadTemplate(filepath, new LineCallback<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) {
                return value + line;
            }
        }, "");
    }
}
