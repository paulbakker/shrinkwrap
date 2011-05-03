/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.impl.base.shrinkwrap276;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class InvalidByteStreamTestCase
{
   
   private static final Logger log = Logger.getLogger(InvalidByteStreamTestCase.class.getName());
   
   /*
    * ALR: Testcase in process.  @see notes on the JIRA.  When there's a failing case in place, 
    * put it her, reformat, and commit.
    */
   
   @Test
   public void shrinkwrap276() throws Exception{
      final ArchivePath path = ArchivePaths.create("someContext");
      final ResourceAdapterArchive raa = ShrinkWrap.create(ResourceAdapterArchive.class, "ra.rar").add(new NamedAsset()
      {
         
         @Override
         public InputStream openStream()
         {
            return new ByteArrayInputStream("something".getBytes());
         }
         
         @Override
         public String getName()
         {
            return path.get();
         }
      });

      final JavaArchive ja = ShrinkWrap.create(JavaArchive.class).addClasses(this.getClass());

      raa.addAsLibrary(ja);
      final File file = new File("/home/alr/Desktop/"+raa.getName());
      raa.as(ZipExporter.class).exportTo(file,true);
//      raa.addAsManifestResource("validator/" + archiveName + "/META-INF/ra.xml", "ra.xml");
      
      log.info(raa.toString(true));
      
      final JavaArchive roundtrip = ShrinkWrap.create(ZipImporter.class,raa.getName()).importFrom(file).as(JavaArchive.class);
      log.info(roundtrip.toString(true));
      Assert.assertTrue(roundtrip.contains(path));
      
      final InputStream is = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(is, 4092);
      final ZipInputStream zip = new ZipInputStream(bis);
      ZipEntry ze = zip.getNextEntry();
      log.info(ze.getName());
      ze = zip.getNextEntry();
      log.info(ze.getName());
      

      
   }

}
