package com.cl.data.stream;

import java.io.File;

/**
 * Created by 亮 on 2017/12/14.
 */
public interface ReaderStream<R> {

    void initReaderStream();

    R readFile();
}
