package org.apache.maven.archiva.meeper;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class CsvReaderTest extends TestCase {

    private CsvReader reader;

    protected void setUp() throws Exception {
        super.setUp();

        reader = new CsvReader();
    }

    public void testParse() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "org/apache/maven/archiva/meeper/sync.csv");
        List repos = reader.parse(is);
        assertEquals(2, repos.size());
        for (Iterator it = repos.iterator(); it.hasNext();) {
            SyncedRepository repo = (SyncedRepository) it.next();
            System.out.println(repo);
        }
    }

}
