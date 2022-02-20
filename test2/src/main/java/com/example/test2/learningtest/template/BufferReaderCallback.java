package com.example.test2.learningtest.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferReaderCallback {
    Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
