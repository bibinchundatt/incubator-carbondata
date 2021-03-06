/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.carbondata.core.carbon.datastorage.filesystem.store.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.carbondata.core.datastorage.store.impl.DFSFileHolderImpl;

import mockit.Mock;
import mockit.MockUp;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pentaho.di.core.util.Assert.assertNull;

public class DFSFileHolderImplUnitTest {

  private static DFSFileHolderImpl dfsFileHolder;
  private static String fileName;
  private static String fileNameWithEmptyContent;
  private static File file;
  private static File fileWithEmptyContent;

  @BeforeClass public static void setup() {
    dfsFileHolder = new DFSFileHolderImpl();
    file = new File("Test.carbondata");
    fileWithEmptyContent = new File("TestEXception.carbondata");

    if (!file.exists()) try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!fileWithEmptyContent.exists()) try {
      fileWithEmptyContent.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      FileOutputStream of = new FileOutputStream(file, true);
      BufferedWriter br = new BufferedWriter(new OutputStreamWriter(of, "UTF-8"));
      br.write("Hello World");
      br.close();
    } catch (Exception e) {
      e.getMessage();
    }
    fileName = file.getAbsolutePath();
    fileNameWithEmptyContent = fileWithEmptyContent.getAbsolutePath();
  }

  @AfterClass public static void tearDown() {
    file.delete();
    fileWithEmptyContent.delete();
    dfsFileHolder.finish();
  }

  @Test public void testReadByteArray() {
    byte[] result = dfsFileHolder.readByteArray(fileName, 1);
    byte[] expected_result = new byte[] { 72 };
    assertThat(result, is(equalTo(expected_result)));
  }

  @Test public void testReadByteArrayWithFilePath() {
    byte[] result = dfsFileHolder.readByteArray(fileName, 2L, 2);
    byte[] expected_result = { 108, 108 };
    assertThat(result, is(equalTo(expected_result)));
  }

  @Test public void testReadLong() {
    long actualResult = dfsFileHolder.readLong(fileName, 1L);
    long expectedResult = 7308335519855243122L;
    assertThat(actualResult, is(equalTo(expectedResult)));
  }

  @Test public void testReadLongForIoException() throws IOException {
    dfsFileHolder.readLong(fileNameWithEmptyContent, 1L);

  }

  @Test public void testReadIntForIoException() throws IOException {
    dfsFileHolder.readInt(fileNameWithEmptyContent, 1L);
  }

  @Test public void testReadInt() {
    int actualResult = dfsFileHolder.readInt(fileName, 1L);
    int expectedResult = 1701604463;
    assertThat(actualResult, is(equalTo(expectedResult)));
  }

  @Test public void testReadIntWithFileName() {
    int actualResult = dfsFileHolder.readInt(fileName);
    int expectedResult = 1701604463;
    assertThat(actualResult, is(equalTo(expectedResult)));
  }

  @Test public void testReadIntWithFileNameForIOException() {
    dfsFileHolder.readInt(fileNameWithEmptyContent);

  }

  @Test public void testDouble() {
    double actualResult = dfsFileHolder.readDouble(fileName, 1L);
    double expectedResult = 7.3083355198552433E18;
    assertThat(actualResult, is(equalTo(expectedResult)));
  }

  @Test public void testDoubleForIoException() throws IOException {
    dfsFileHolder.readDouble(fileNameWithEmptyContent, 1L);

  }

  @Test public void testDoubleForIoExceptionwithUpdateCache() throws Exception {
    new MockUp<FileSystem>() {
      @SuppressWarnings("unused") @Mock public FSDataInputStream open(Path file)
          throws IOException {
        throw new IOException();
      }

    };
    String expected = null;
    try {
      dfsFileHolder.readDouble(fileName, 1L);
    } catch (Exception e) {
      assertNull(e.getMessage());
    }

  }

}
